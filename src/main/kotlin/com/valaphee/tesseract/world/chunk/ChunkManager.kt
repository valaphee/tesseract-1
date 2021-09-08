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

package com.valaphee.tesseract.world.chunk

import com.google.inject.Inject
import com.valaphee.foundry.ecs.Consumed
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.Config
import com.valaphee.tesseract.ServerInstance
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.terrain.TerrainRuntime
import com.valaphee.tesseract.world.chunk.terrain.blockUpdates
import com.valaphee.tesseract.world.chunk.terrain.fastBlockUpdates
import com.valaphee.tesseract.world.chunk.terrain.generator.Generator
import com.valaphee.tesseract.world.chunk.terrain.modified
import com.valaphee.tesseract.world.entity.addEntities
import com.valaphee.tesseract.world.entity.removeEntities
import com.valaphee.tesseract.world.filter
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

/**
 * @author Kevin Ludwig
 */
class ChunkManager @Inject constructor(
    private val instance: ServerInstance,
    config: Config,
    private val generator: Generator
) : BaseFacet<WorldContext, ChunkManagerMessage>(ChunkManagerMessage::class) {
    private val chunks = Long2ObjectMaps.synchronize(Long2ObjectOpenHashMap<Chunk>())

    private val awaitedChunksChannel = Channel<AwaitedChunk>()
    private val requestedChunksChannel = Channel<Int2>(Channel.UNLIMITED)
    private val loadedChunksChannel = Channel<Chunk>()

    private val workers = List(config.concurrency) {
        instance.coroutineScope.launch {
            for (requestedChunk in requestedChunksChannel) {
                val (x, z) = requestedChunk
                loadedChunksChannel.send((instance.worldContext.provider.loadChunk(encodePosition(x, z)) ?: instance.worldContext.entityFactory.chunk(requestedChunk, generator.generate(requestedChunk))).asMutableEntity().apply {
                    addAttribute(TerrainRuntime(fastBlockUpdates))
                    addAttribute(Actors())
                })
            }
        }
    }

    private val loader = instance.coroutineScope.launch {
        val requestedChunks = mutableMapOf<Int2, MutableList<AwaitedChunk>>()
        while (true) select<Unit> {
            awaitedChunksChannel.onReceive { awaitedChunk ->
                requestedChunks[awaitedChunk.position]?.add(awaitedChunk) ?: run {
                    requestedChunks[awaitedChunk.position] = mutableListOf(awaitedChunk)
                    requestedChunksChannel.send(awaitedChunk.position)
                }
            }
            loadedChunksChannel.onReceive { loadedChunk ->
                val (x, z) = loadedChunk.position
                chunks[encodePosition(x, z)] = loadedChunk

                val blockUpdates = loadedChunk.blockUpdates
                val fastBlockUpdates = loadedChunk.fastBlockUpdates
                chunks[encodePosition(x - 1, z + 0)]?.let {
                    blockUpdates.chunks[0] = it.fastBlockUpdates
                    it.blockUpdates.chunks[2] = fastBlockUpdates
                }
                chunks[encodePosition(x + 0, z - 1)]?.let {
                    blockUpdates.chunks[1] = it.fastBlockUpdates
                    it.blockUpdates.chunks[3] = fastBlockUpdates
                }
                chunks[encodePosition(x + 1, z + 0)]?.let {
                    blockUpdates.chunks[2] = it.fastBlockUpdates
                    it.blockUpdates.chunks[0] = fastBlockUpdates
                }
                chunks[encodePosition(x + 0, z + 1)]?.let {
                    blockUpdates.chunks[3] = it.fastBlockUpdates
                    it.blockUpdates.chunks[1] = fastBlockUpdates
                }
                chunks[encodePosition(x - 1, z - 1)]?.let {
                    blockUpdates.chunks[4] = it.fastBlockUpdates
                    it.blockUpdates.chunks[7] = fastBlockUpdates
                }
                chunks[encodePosition(x - 1, z + 1)]?.let {
                    blockUpdates.chunks[5] = it.fastBlockUpdates
                    it.blockUpdates.chunks[6] = fastBlockUpdates
                }
                chunks[encodePosition(x + 1, z - 1)]?.let {
                    blockUpdates.chunks[6] = it.fastBlockUpdates
                    it.blockUpdates.chunks[5] = fastBlockUpdates
                }
                chunks[encodePosition(x + 1, z + 1)]?.let {
                    blockUpdates.chunks[7] = it.fastBlockUpdates
                    it.blockUpdates.chunks[4] = fastBlockUpdates
                }

                requestedChunks.remove(loadedChunk.position)!!.forEach { it.awaiting.complete(loadedChunk) }
            }
        }
    }

    override suspend fun receive(message: ChunkManagerMessage): Response {
        val context = message.context
        when (message) {
            is ChunkAcquire -> {
                val chunks = message.positions.map { chunks[it] }.filterNotNull()
                if (chunks.isNotEmpty()) {
                    message.usage.chunks = chunks.onEach { chunk -> message.source?.filter<PlayerType> { chunk.actors += it } }.toTypedArray()
                    message.source?.sendMessage(message.usage)
                }
                if (message.positions.size != chunks.size) instance.coroutineScope.launch {
                    val loadedChunks = message.positions.filterNot(this@ChunkManager.chunks::containsKey).map { AwaitedChunk(decodePosition(it)).also { awaitedChunksChannel.send(it) }.awaiting.await() }.toTypedArray()
                    context.world.addEntities(context, message.source, *loadedChunks)
                    message.usage.chunks = loadedChunks
                    message.source?.sendMessage(message.usage)
                }
            }
            is ChunkRelease -> {
                val chunksRemoved = message.positions.filter { chunkPosition ->
                    chunks[chunkPosition]?.let { chunk ->
                        message.source?.filter<PlayerType> { chunk.actors -= it }
                        chunk.actors.none { it.type == PlayerType }
                    } ?: false // TODO
                }.map(chunks::remove)
                context.provider.saveChunks(chunksRemoved.filter { it.modified }.onEach {
                    val (x, z) = it.position
                    chunks[encodePosition(x - 1, z + 0)]?.let { it.blockUpdates.chunks[2] = null }
                    chunks[encodePosition(x + 0, z - 1)]?.let { it.blockUpdates.chunks[3] = null }
                    chunks[encodePosition(x + 1, z + 0)]?.let { it.blockUpdates.chunks[0] = null }
                    chunks[encodePosition(x + 0, z + 1)]?.let { it.blockUpdates.chunks[1] = null }
                    chunks[encodePosition(x - 1, z - 1)]?.let { it.blockUpdates.chunks[7] = null }
                    chunks[encodePosition(x - 1, z + 1)]?.let { it.blockUpdates.chunks[6] = null }
                    chunks[encodePosition(x + 1, z - 1)]?.let { it.blockUpdates.chunks[5] = null }
                    chunks[encodePosition(x + 1, z + 1)]?.let { it.blockUpdates.chunks[4] = null }
                })
                context.world.removeEntities(context, message.source, *chunksRemoved.toTypedArray())
            }
        }

        return Consumed
    }

    private inner class AwaitedChunk(
        val position: Int2,
        val awaiting: CompletableDeferred<Chunk> = CompletableDeferred()
    )
}
