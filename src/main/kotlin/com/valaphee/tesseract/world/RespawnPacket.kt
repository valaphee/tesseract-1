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

package com.valaphee.tesseract.world

import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
class RespawnPacket(
    val runtimeEntityId: Long,
    val state: State,
    val position: Float3
) : Packet() {
    enum class State {
        ServerSearching, ServerReady, ClientReady
    }

    override val id get() = 0x2D

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeFloat3(position)
        buffer.writeByte(state.ordinal)
        buffer.writeVarULong(runtimeEntityId)
    }

    override fun handle(handler: PacketHandler) = handler.respawn(this)

    override fun toString() = "RespawnPacket(runtimeEntityId=$runtimeEntityId, state=$state, position=$position)"
}

/**
 * @author Kevin Ludwig
 */
object RespawnPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): RespawnPacket {
        val position = buffer.readFloat3()
        val state = RespawnPacket.State.values()[buffer.readByte().toInt()]
        val runtimeEntityId = buffer.readVarULong()
        return RespawnPacket(runtimeEntityId, state, position)
    }
}
