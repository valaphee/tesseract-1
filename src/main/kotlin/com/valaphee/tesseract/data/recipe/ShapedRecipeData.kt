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

package com.valaphee.tesseract.data.recipe

import com.valaphee.tesseract.data.DataType
import com.valaphee.tesseract.data.KeyedData
import com.valaphee.tesseract.inventory.item.craft.Recipe
import com.valaphee.tesseract.inventory.item.craft.shapedRecipe
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.pack.recipe.RecipeData
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
@DataType("tesseract:recipe_shaped")
class ShapedRecipeData(
    override val key: String,
    val tags: List<String>,
    val map: Map<Char, Stack>,
    val pattern: List<String>,
    val priority: Int = 0,
    val result: Stack
) : RecipeData, KeyedData() {
    override fun toRecipe(netId: Int): Recipe {
        val height = pattern.size
        var width = 0
        pattern.forEach {
            val patternRowWidth = it.length
            if (patternRowWidth > width) width = patternRowWidth
        }
        val inputs = arrayOfNulls<Stack?>(width * height)
        pattern.forEachIndexed { i, patternRow -> patternRow.forEachIndexed { j, patternColumn -> inputs[i * width + j] = map[patternColumn] } }
        return shapedRecipe(Recipe.Type.Shaped, UUID.nameUUIDFromBytes(key.toByteArray()), key, width, height, inputs.toList(), listOf(result), tags.first(), priority, netId)
    }
}
