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

package com.valaphee.tesseract.net.base

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.LittleEndianVarIntByteBufOutputStream
import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.util.nbt.NbtOutputStream

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class EntityIdentifiersPacket private constructor(
    val data: ByteArray?,
    val tag: CompoundTag?
) : Packet() {
    override val id get() = 0x77

    constructor(data: ByteArray) : this(data, null)

    constructor(tag: CompoundTag?) : this(null, tag)

    override fun write(buffer: PacketBuffer, version: Int) {
        data?.let { buffer.writeBytes(it) } ?: NbtOutputStream(LittleEndianVarIntByteBufOutputStream(buffer)).use { it.writeTag(tag) }
    }

    override fun handle(handler: PacketHandler) = handler.entityIdentifiers(this)

    override fun toString() = "EntityIdentifiersPacket(tag=$tag)"
}

/**
 * @author Kevin Ludwig
 */
object EntityIdentifiersPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = EntityIdentifiersPacket(buffer.toNbtInputStream().use { it.readTag()?.asCompoundTag() })
}
