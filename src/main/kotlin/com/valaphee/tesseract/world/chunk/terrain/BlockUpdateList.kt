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

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState

/**
 * @author Kevin Ludwig
 */
class BlockUpdateList(
    center: ChunkBlockUpdateList
) : ReadWriteCartesian {
    internal val chunks = arrayOfNulls<ChunkBlockUpdateList>(9)

    init {
        val (_, _, index) = indexAndOffsetOf(0, 0)
        chunks[index] = center
    }

    /**
     * Gets a block in its already updated state
     *
     * @param x x coordinate relative to the chunk (-16-31)
     * @param y y coordinate relative to the chunk (0-255)
     * @param z z coordinate relative to the chunk (-16-31)
     */
    override fun get(x: Int, y: Int, z: Int): Int {
        val (offsetX, offsetZ, index) = indexAndOffsetOf(x, z)
        return chunks[index]?.get(x + offsetX, y, z + offsetZ) ?: borderId
    }

    /**
     * Schedules a block change for the next cycle
     *
     * @param x x coordinate relative to the chunk (-16-31)
     * @param y y coordinate relative to the chunk (0-255)
     * @param z z coordinate relative to the chunk (-16-31)
     * @param value new value of the block
     */
    override fun set(x: Int, y: Int, z: Int, value: Int) = set(x, y, z, 0b1, value)

    /**
     * Schedules a block change for the next cycle
     *
     * @param x x coordinate relative to the chunk (-16-31)
     * @param y y coordinate relative to the chunk (0-255)
     * @param z z coordinate relative to the chunk (-16-31)
     * @param value new value of the block
     */
    operator fun set(x: Int, y: Int, z: Int, data: Int, value: Int) {
        val (offsetX, offsetZ, index) = indexAndOffsetOf(x, z)
        chunks[index]?.set(x + offsetX, y, z + offsetZ, data, value)
    }

    private fun indexAndOffsetOf(x: Int, z: Int) = when {
        x < 0 -> when {
            z < 0 -> Int3(BlockStorage.XZSize, BlockStorage.XZSize, 4)
            z >= BlockStorage.XZSize -> Int3(BlockStorage.XZSize, -BlockStorage.XZSize, 5)
            else -> Int3(BlockStorage.XZSize, 0, 0)
        }
        x >= BlockStorage.XZSize -> when {
            z < 0 -> Int3(-BlockStorage.XZSize, BlockStorage.XZSize, 6)
            z >= BlockStorage.XZSize -> Int3(-BlockStorage.XZSize, -BlockStorage.XZSize, 7)
            else -> Int3(-BlockStorage.XZSize, 0, 2)
        }
        else -> when {
            z < 0 -> Int3(0, BlockStorage.XZSize, 1)
            z >= BlockStorage.XZSize -> Int3(0, -BlockStorage.XZSize, 3)
            else -> Int3(0, 0, 8)
        }
    }

    companion object {
        private val borderId = BlockState.byKeyWithStates("minecraft:stone").id
    }
}
