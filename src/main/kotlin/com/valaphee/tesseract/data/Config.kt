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

package com.valaphee.tesseract.data

import java.net.InetSocketAddress
import java.util.regex.Pattern

/**
 * @author Kevin Ludwig
 */
@DataType("tesseract:config")
class Config(
    val listener: Listener = Listener(),
    val maximumPlayers: Int = 10,
    val maximumViewDistance: Int = 32
) : Data {
    class Listener(
        val address: InetSocketAddress = InetSocketAddress("0.0.0.0", 19132),
        val maximumQueuedBytes: Int = 8 * 1024 * 1024,
        val serverName: String = "Tesseract",
        val timeout: Int = 30_000,
        val compressionLevel: Int = 7,
        val verification: Boolean = true,
        val userNamePattern: Pattern = Pattern.compile("^[a-zA-Z0-9_-]{3,16}\$"),
        val encryption: Boolean = true,
        val caching: Boolean = false
    )
}
