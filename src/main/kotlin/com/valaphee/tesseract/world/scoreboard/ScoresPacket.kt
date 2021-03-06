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

package com.valaphee.tesseract.world.scoreboard

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
class ScoresPacket(
    val action: Action,
    val scores: List<Score>
) : Packet() {
    enum class Action {
        Set, Remove
    }

    override val id get() = 0x6C

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(action.ordinal)
        buffer.writeVarUInt(scores.size)
        scores.forEach {
            buffer.writeVarLong(it.scoreboardId)
            buffer.writeString(it.objectiveName)
            buffer.writeIntLE(it.value)
            if (action == Action.Set) {
                buffer.writeByte(it.type.ordinal)
                @Suppress("NON_EXHAUSTIVE_WHEN")
                when (it.type) {
                    Score.ScorerType.Entity, Score.ScorerType.Player -> buffer.writeVarLong(it.entityId)
                    Score.ScorerType.Fake -> buffer.writeString(it.name!!)
                }
            }
        }
    }

    override fun handle(handler: PacketHandler) = handler.scores(this)

    override fun toString() = "ScoresPacket(action=$action, scores=$scores)"
}

/**
 * @author Kevin Ludwig
 */
object ScoresPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): ScoresPacket {
        val action = ScoresPacket.Action.values()[buffer.readUnsignedByte().toInt()]
        val entries = safeList(buffer.readVarUInt()) {
            val scoreboardId = buffer.readVarLong()
            val objectiveId = buffer.readString()
            val score = buffer.readIntLE()
            if (action == ScoresPacket.Action.Set) {
                when (val scorerType = Score.ScorerType.values()[buffer.readUnsignedByte().toInt()]) {
                    Score.ScorerType.Entity, Score.ScorerType.Player -> return@safeList Score(scoreboardId, objectiveId, score, scorerType, buffer.readVarLong())
                    Score.ScorerType.Fake -> return@safeList Score(scoreboardId, objectiveId, score, buffer.readString())
                }
            }
            Score(scoreboardId, objectiveId, score)
        }
        return ScoresPacket(action, entries)
    }
}
