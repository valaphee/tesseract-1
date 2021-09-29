/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.valaphee.tesseract.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.collect.HashBiMap
import com.google.inject.AbstractModule
import com.google.inject.Key
import com.google.inject.TypeLiteral
import com.google.inject.binder.AnnotatedBindingBuilder
import com.google.inject.util.Types
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.Argument
import com.valaphee.tesseract.data.block.Block
import com.valaphee.tesseract.util.getCompoundTag
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.jackson.Float2Deserializer
import com.valaphee.tesseract.util.jackson.Float2Serializer
import com.valaphee.tesseract.util.jackson.Float3Deserializer
import com.valaphee.tesseract.util.jackson.Float3Serializer
import com.valaphee.tesseract.util.nbt.NbtInputStream
import com.valaphee.tesseract.util.nbt.TagType
import io.github.classgraph.ClassGraph
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.PooledByteBufAllocator
import org.apache.logging.log4j.LogManager
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/**
 * @author Kevin Ludwig
 */
class DataModule(
    private val argument: Argument? = null
) : AbstractModule() {
    override fun configure() {
        run {
            val buffer = PooledByteBufAllocator.DEFAULT.directBuffer()
            try {
                buffer.writeBytes(DataModule::class.java.getResourceAsStream("/runtime_block_states.dat")!!.readBytes())
                val blocks = HashMap<String, ArrayList<Map<String, Any>>>()
                NbtInputStream(ByteBufInputStream(buffer)).use { it.readTag() }?.asCompoundTag()?.get("blocks")?.asListTag()!!.toList().map { it.asCompoundTag()!! }.forEach {
                    blocks.getOrPut(it.getString("name")) { ArrayList() }.add(it.getCompoundTag("states").toMap().mapValues {
                        when (it.value.type) {
                            TagType.Byte -> it.value.asNumberTag()!!.toByte() != 0.toByte()
                            TagType.Int -> it.value.asNumberTag()!!.toInt()
                            TagType.String -> it.value.asArrayTag()!!.valueToString()
                            else -> TODO()
                        }
                    })
                }
                bind(object : Key<Map<String, @JvmSuppressWildcards Block>>() {}).toInstance(blocks.mapValues { Block(it.key, it.value) })
                log.info("Bound tesseract:block, with ${blocks.size} entries")
            } finally {
                buffer.release()
            }
        }

        val objectMapper = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(
                SimpleModule()
                    .addSerializer(Float2::class.java, Float2Serializer)
                    .addDeserializer(Float2::class.java, Float2Deserializer)
                    .addSerializer(Float3::class.java, Float3Serializer)
                    .addDeserializer(Float3::class.java, Float3Deserializer)
            )
            propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
            setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
            enable(SerializationFeature.INDENT_OUTPUT)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            enable(JsonParser.Feature.ALLOW_COMMENTS)
        }.also { bind(ObjectMapper::class.java).toInstance(it) }

        classByType.clear()
        ClassGraph().acceptPackages(this::class.java.packageName).enableClassInfo().enableAnnotationInfo().scan().use {
            val dataType = DataType::class.jvmName
            classByType.putAll(it.getClassesWithAnnotation(dataType).associate { it.getAnnotationInfo(dataType).parameterValues.getValue("value") as String to Class.forName(it.name).kotlin })
        }

        ClassGraph().acceptPaths("data").scan().use {
            val (keyed, other) = it.allResources
                .map {
                    val url = it.url
                    when (val extension = url.file.substring(url.file.lastIndexOf('.') + 1)) {
                        "json" -> {
                            @Suppress("UNCHECKED_CAST")
                            objectMapper.readValue<Data>(url)
                        }
                        else -> TODO(extension)
                    }
                }
                .partition { it is KeyedData }
            keyed.filterIsInstance<KeyedData>()
                .groupBy { typeByClassOrNull(it::class) }
                .forEach { (key, value) ->
                    key?.let {
                        @Suppress("UNCHECKED_CAST")
                        (bind(TypeLiteral.get(Types.mapOf(String::class.java, value.first()::class.java))) as AnnotatedBindingBuilder<Any>).toInstance(value.associateBy { it.key })
                        log.info("Bound $it, with ${value.size} entries")
                    }
                }
            other.forEach {
                @Suppress("UNCHECKED_CAST")
                (bind(it::class.java) as AnnotatedBindingBuilder<Any>).toInstance(it)
                log.info("Bound ${it::class.jvmName}")
            }
        }

        argument?.let {
            bind(Argument::class.java).toInstance(argument)
            bind(Config::class.java).toInstance((if (argument.config.exists()) objectMapper.readValue(argument.config) else Config()).also { objectMapper.writeValue(argument.config, it) })
        }
    }

    companion object {
        private val log = LogManager.getLogger(DataModule::class.java)
        internal val classByType = HashBiMap.create<String, KClass<*>>()

        fun classByTypeOrNull(key: String) = classByType[key]

        fun typeByClassOrNull(value: KClass<*>) = classByType.inverse()[value]
    }
}
