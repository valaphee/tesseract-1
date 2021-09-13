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

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import com.google.inject.binder.AnnotatedBindingBuilder
import com.google.inject.util.Types
import com.valaphee.tesseract.Argument
import com.valaphee.tesseract.data.block.Block
import com.valaphee.tesseract.data.block.BlockState
import com.valaphee.tesseract.data.block.BlockWrapper
import com.valaphee.tesseract.data.item.Item
import com.valaphee.tesseract.data.item.ItemWrapper
import io.github.classgraph.ClassGraph
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.graalvm.polyglot.Context
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmName

/**
 * @author Kevin Ludwig
 */
class DataModule(
    private val argument: Argument
) : AbstractModule() {
    override fun configure() {
        ComponentRegistry.scan()

        ClassGraph().enableClassInfo().enableAnnotationInfo().scan().use {
            it.allClasses.forEach {
                if (it.hasAnnotation(Index::class.jvmName)) when (val entry = Class.forName(it.name).kotlin.primaryConstructor!!.call()) {
                    is Block -> BlockState.byKey(entry.key).forEach { it.block = entry }
                    is Item -> com.valaphee.tesseract.inventory.item.Item.byKey(entry.key).item = entry
                }
            }
        }
        val context = Context.newBuilder("js")
            .allowAllAccess(true)
            .build()
        val objectMapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategies.KEBAB_CASE
            enable(SerializationFeature.INDENT_OUTPUT)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            enable(JsonParser.Feature.ALLOW_COMMENTS)
        }
        ClassGraph().acceptPaths("data").scan().use {
            val (keyed, other) = it.allResources
                .map {
                    val url = it.url
                    when (url.file.substring(url.file.lastIndexOf('.') + 1)) {
                        "js" -> {
                            context.eval("js", url.readText())?.let {
                                val firstMemberKey = it.memberKeys.first()
                                val firstMember = it.getMember(firstMemberKey)
                                when (firstMemberKey) {
                                    "tesseract:block" -> BlockWrapper(firstMember)
                                    "tesseract:item" -> ItemWrapper(firstMember)
                                    else -> throw UnknownComponentException(firstMemberKey)
                                }
                            } ?: TODO()
                        }
                        "json" -> {
                            @Suppress("UNCHECKED_CAST")
                            objectMapper.readValue<Data>(url)
                        }
                        else -> TODO()
                    }
                }
                .partition { it is Keyed }
            keyed.filterIsInstance<Keyed>()
                .groupBy { ComponentRegistry.byValueOrNull(it::class) }
                .forEach { (key, value) ->
                    key?.let {
                        @Suppress("UNCHECKED_CAST")
                        (bind(TypeLiteral.get(Types.mapOf(String::class.java, value.first()::class.java))) as AnnotatedBindingBuilder<Any>).toInstance(value.associateBy { it.key })
                    }
                }
            other.forEach {
                @Suppress("UNCHECKED_CAST")
                (bind(it::class.java) as AnnotatedBindingBuilder<Any>).toInstance(it)
            }
        }
        bind(Config::class.java).toInstance((if (argument.config.exists()) objectMapper.readValue(argument.config) else Config()).also { objectMapper.writeValue(argument.config, it) })
    }

    companion object {
        private val log: Logger = LogManager.getLogger(DataModule::class.java)
    }
}
