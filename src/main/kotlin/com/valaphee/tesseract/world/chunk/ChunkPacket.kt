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
 *
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.world.chunk.storage.BlockStorage
import com.valaphee.tesseract.world.chunk.storage.readSection
import it.unimi.dsi.fastutil.ints.Int2ShortMap
import it.unimi.dsi.fastutil.ints.Int2ShortMaps

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class ChunkPacket(
    val position: Int2,
    val blockStorage: BlockStorage,
    val blockExtraData: Int2ShortMap,
    val blockEntities: Array<CompoundTag>,
    val cache: Boolean,
    val blobIds: LongArray? = null
) : Packet {
    override val id get() = 0x3A

    constructor(position: Int2, blockStorage: BlockStorage, blockExtraData: Int2ShortMap, blockEntities: Array<CompoundTag>) : this(position, blockStorage, blockExtraData, blockEntities, false, null)

    constructor(position: Int2, blockStorage: BlockStorage, blockExtraData: Int2ShortMap, blockEntities: Array<CompoundTag>, blobIds: LongArray) : this(position, blockStorage, blockExtraData, blockEntities, true, blobIds)

    override fun write(buffer: PacketBuffer, version: Int) {
        val (x, z) = position
        buffer.writeVarInt(x)
        buffer.writeVarInt(z)
        var sectionCount = blockStorage.sections.size - 1
        while (sectionCount >= 0 && blockStorage.sections[sectionCount].empty) sectionCount--
        buffer.writeVarUInt(++sectionCount)
        buffer.writeBoolean(cache)
        if (cache) blobIds!!.let {
            buffer.writeVarUInt(it.size)
            it.forEach(buffer::writeLongLE)
        }
        val dataLengthIndex = buffer.writerIndex()
        buffer.writeZero(PacketBuffer.MaximumVarUIntLength)
        if (!cache) {
            repeat(sectionCount) { i -> blockStorage.sections[i].writeToBuffer(buffer) }
            buffer.writeBytes(ByteArray(BlockStorage.XZSize * BlockStorage.XZSize))
        }
        buffer.writeByte(0)
        buffer.writeVarInt(blockExtraData.size)
        blockExtraData.forEach {
            buffer.writeVarInt(it.key)
            buffer.writeShortLE(it.value.toInt())
        }
        buffer.toNbtOutputStream().use { stream -> blockEntities.forEach { stream.writeTag(it) } }
        buffer.setMaximumLengthVarUInt(dataLengthIndex, buffer.writerIndex() - (dataLengthIndex + PacketBuffer.MaximumVarUIntLength))
    }

    override fun handle(handler: PacketHandler) = handler.chunk(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChunkPacket

        if (position != other.position) return false
        if (blockStorage != other.blockStorage) return false
        if (cache != other.cache) return false
        if (blobIds != null) {
            if (other.blobIds == null) return false
            if (!blobIds.contentEquals(other.blobIds)) return false
        } else if (other.blobIds != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = position.hashCode()
        result = 31 * result + blockStorage.hashCode()
        result = 31 * result + cache.hashCode()
        result = 31 * result + (blobIds?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object ChunkPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): ChunkPacket {
        val position = Int2(buffer.readVarInt(), buffer.readVarInt())
        val sectionCount = buffer.readVarUInt()
        val cache = buffer.readBoolean()
        val blobIds = if (cache) LongArray(buffer.readVarUInt()) { buffer.readLongLE() } else null
        buffer.readVarUInt()
        val blockStorage: BlockStorage
        if (!cache) {

            blockStorage = BlockStorage(buffer.blockStates.getKey(airKey), Array(sectionCount) { buffer.readSection(buffer.blockStates.getKey(airKey)) })
            buffer.readBytes(ByteArray(BlockStorage.XZSize * BlockStorage.XZSize))
        } else blockStorage = BlockStorage(buffer.blockStates.getKey(airKey), sectionCount)
        buffer.readByte()
        /*val blockExtraData = Int2ShortOpenHashMap().apply { repeat(buffer.readVarInt()) { this[buffer.readVarInt()] = buffer.readShortLE() } }*/
        return ChunkPacket(position, blockStorage, Int2ShortMaps.EMPTY_MAP, emptyArray(), cache, blobIds)
    }

    private const val airKey = "minecraft:air"
}
