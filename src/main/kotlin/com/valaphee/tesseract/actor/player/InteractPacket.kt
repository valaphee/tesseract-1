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

package com.valaphee.tesseract.actor.player

import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.Actor
import com.valaphee.tesseract.actor.AnyActorOfWorld
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
data class InteractPacket(
    var action: Action,
    var actor: AnyActorOfWorld?,
    var mousePosition: Float3?
) : Packet {
    enum class Action {
        None, Interact, Damage, LeaveVehicle, Mouseover, NpcOpen, OpenInventory
    }

    override val id get() = 0x21

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(action.ordinal)
        buffer.writeVarULong(actor?.id ?: 0)
        if (action == Action.Mouseover || action == Action.NpcOpen) buffer.writeFloat3(mousePosition!!)
    }

    override fun handle(handler: PacketHandler) = handler.interact(this)
}

/**
 * @author Kevin Ludwig
 */
class InteractPacketReader(
    private val context: WorldContext
) : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): InteractPacket {
        val action = InteractPacket.Action.values()[buffer.readByte().toInt()]
        @Suppress("UNCHECKED_CAST")
        val actor = context.engine.findEntityOrNull(buffer.readVarULong()) as? Actor
        val mousePosition = if (action == InteractPacket.Action.Mouseover || action == InteractPacket.Action.NpcOpen) buffer.readFloat3() else null
        return InteractPacket(action, actor, mousePosition)
    }
}