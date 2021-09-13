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

package com.valaphee.tesseract.data.block

import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.util.math.Direction
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.terrain.PropagationBlockUpdateList
import org.graalvm.polyglot.Value

/**
 * @author Kevin Ludwig
 */
@Component("tesseract:block")
class BlockWrapper(
    polyglot: Value
) : Block {
    override val key: String = polyglot.getMember("key").asString()
    private val onUse = polyglot.getMember("on_use")
    private val onUpdate = polyglot.getMember("on_update")

    override fun onUse(context: WorldContext, player: Player, blockUpdates: PropagationBlockUpdateList, x: Int, y: Int, z: Int, direction: Direction, clickPosition: Float3) = onUse?.executeVoid(context, player, blockUpdates, x, y, z, direction, clickPosition)?.run { true } ?: false

    override fun onUpdate(blockUpdates: PropagationBlockUpdateList, x: Int, y: Int, z: Int, blockState: BlockState) {
        onUpdate?.executeVoid(blockUpdates, x, y, z, blockState)
    }
}
