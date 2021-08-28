/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class ViewDistancePacket(
    var distance: Int
) : Packet {
    override val id get() = 0x46

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarInt(distance)
    }

    override fun handle(handler: PacketHandler) = handler.viewDistance(this)
}

/**
 * @author Kevin Ludwig
 */
object ViewDistancePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = ViewDistancePacket(buffer.readVarInt())
}