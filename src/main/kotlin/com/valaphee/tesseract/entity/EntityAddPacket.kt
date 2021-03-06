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

package com.valaphee.tesseract.entity

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.entity.attribute.Attributes
import com.valaphee.tesseract.entity.metadata.Metadata
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.safeList

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class EntityAddPacket(
    val uniqueEntityId: Long,
    val runtimeEntityId: Long,
    val type: String,
    val position: Float3,
    val velocity: Float3,
    val rotation: Float2,
    val headRotationYaw: Float,
    val attributes: Attributes,
    val metadata: Metadata,
    val links: List<Link>
) : Packet() {
    override val id get() = 0x0D

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarLong(uniqueEntityId)
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeString(type)
        buffer.writeFloat3(position)
        buffer.writeFloat3(velocity)
        buffer.writeFloat2(rotation)
        buffer.writeFloatLE(headRotationYaw)
        attributes.writeToBuffer(buffer, false)
        metadata.writeToBuffer(buffer)
        buffer.writeVarUInt(links.size)
        if (version >= 407) links.forEach(buffer::writeLink) else links.forEach(buffer::writeLinkPre407)
    }

    override fun handle(handler: PacketHandler) = handler.entityAdd(this)

    override fun toString() = "EntityAddPacket(uniqueEntityId=$uniqueEntityId, runtimeEntityId=$runtimeEntityId, type='$type', position=$position, velocity=$velocity, rotation=$rotation, headRotationYaw=$headRotationYaw, attributes=$attributes, metadata=$metadata, links=$links)"
}

/**
 * @author Kevin Ludwig
 */
object EntityAddPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = EntityAddPacket(
        buffer.readVarLong(),
        buffer.readVarULong(),
        buffer.readString(),
        buffer.readFloat3(),
        buffer.readFloat3(),
        buffer.readFloat2(),
        buffer.readFloatLE(),
        Attributes().apply { readFromBuffer(buffer, false) },
        Metadata().apply { readFromBuffer(buffer) },
        safeList(buffer.readVarUInt()) { if (version >= 407) buffer.readLink() else buffer.readLinkPre407() }
    )
}
