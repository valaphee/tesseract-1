/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.tesseract.nbt.NbtOutputStream
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.LittleEndianVarIntByteBufOutputStream

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class ChunkAddPacket(
    var chunk: Chunk,
    var cache: Boolean,
    val blobIds: LongArray? = null
) : Packet {
    override val id get() = 0x3A

    constructor(chunk: Chunk) : this(chunk, false, null)

    constructor(chunk: Chunk, blobIds: LongArray) : this(chunk, true, blobIds)

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarInt(chunk.x)
        buffer.writeVarInt(chunk.z)
        var subChunkCount = chunk.subChunks.size - 1
        while (subChunkCount >= 0 && chunk.subChunks[subChunkCount].empty) subChunkCount--
        buffer.writeVarUInt(++subChunkCount)
        buffer.writeBoolean(cache)
        if (cache) blobIds!!.let {
            buffer.writeVarUInt(it.size)
            it.forEach { buffer.writeLongLE(it) }
        }
        val dataLengthIndex = buffer.writerIndex()
        buffer.writeZero(PacketBuffer.MaximumVarUIntLength)
        if (!cache) {
            repeat(subChunkCount) { i -> chunk.subChunks[i].writeToBuffer(buffer) }
            buffer.writeBytes(chunk.biomes)
        }
        buffer.writeByte(0)
        buffer.writeVarInt(chunk.blockExtraData.size)
        chunk.blockExtraData.forEach {
            buffer.writeVarInt(it.key)
            buffer.writeShortLE(it.value.toInt())
        }
        NbtOutputStream(LittleEndianVarIntByteBufOutputStream(buffer)).use { stream -> chunk.blockEntities.forEach { stream.writeTag(it) } }
        buffer.setMaximumLengthVarUInt(dataLengthIndex, buffer.writerIndex() - (dataLengthIndex + PacketBuffer.MaximumVarUIntLength))
    }

    override fun handle(handler: PacketHandler) = handler.chunkAdd(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChunkAddPacket

        if (chunk != other.chunk) return false
        if (cache != other.cache) return false
        if (blobIds != null) {
            if (other.blobIds == null) return false
            if (!blobIds.contentEquals(other.blobIds)) return false
        } else if (other.blobIds != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chunk.hashCode()
        result = 31 * result + cache.hashCode()
        result = 31 * result + (blobIds?.contentHashCode() ?: 0)
        return result
    }
}
