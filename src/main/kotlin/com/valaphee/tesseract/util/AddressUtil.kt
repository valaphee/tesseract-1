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

package com.valaphee.tesseract.util

import java.net.InetSocketAddress
import java.util.regex.Pattern

fun address(string: CharSequence, defaultPort: Int): InetSocketAddress? {
    converters.forEach { it.to(string, defaultPort)?.let { return it } }
    return null
}

/**
 * @author Kevin Ludwig
 */
private interface Converter {
    fun to(addressWithPort: CharSequence, defaultPort: Int): InetSocketAddress?
}

private fun parse(string: CharSequence, defaultPort: Int, pattern: Pattern, patternHostIndex: Int, patternPortIndex: Int): InetSocketAddress? {
    val matcher = pattern.matcher(string)
    if (matcher.matches() && matcher.reset().find()) {
        val portString = matcher.group(patternPortIndex)
        var port = defaultPort
        try {
            if (portString != null && portString.isNotEmpty()) port = portString.toInt()
        } catch (_: NumberFormatException) {
        }
        return InetSocketAddress(matcher.group(patternHostIndex), port)
    }
    return null
}

private val hostnamePattern = Pattern.compile("([a-zA-Z][\\w-]*[\\w]*(\\.[a-zA-Z][\\w-]*[\\w]*)*)(:(6553[0-5]|6(55[012]|(5[0-4]|[0-4]\\d)\\d)\\d|[1-5]?\\d{1,4}))?")
private val v4AddressPattern = Pattern.compile("(((25[0-5]|(2[0-4]|1\\d|[1-9]?)\\d)(\\.|\\b)){4}(?<!\\.))(:(6553[0-5]|6(55[012]|(5[0-4]|[0-4]\\d)\\d)\\d|[1-5]?\\d{1,4}))?")
private val v6AddressPattern = Pattern.compile("((((?=(?>.*?::)(?!.*::)))(::)?([0-9a-f]{1,4}::?){0,5}|([0-9a-f]{1,4}:){6})(((25[0-5]|(2[0-4]|1[0-9]|[1-9])?[0-9])(\\.|\\b)){4}|\\3([0-9a-f]{1,4}(::?|\\b)){0,2}|[0-9a-f]{1,4}:[0-9a-f]{1,4})(?<![^:]:)(?<!\\.))(?:([#.])(6553[0-5]|6(55[012]|(5[0-4]|[0-4]\\d)\\d)\\d|[1-5]?\\d{1,4}))?$")
private val bracketV6AddressPattern = Pattern.compile("\\[((((?=(?>.*?::)(?!.*::)))(::)?([0-9a-f]{1,4}::?){0,5}|([0-9a-f]{1,4}:){6})(((25[0-5]|(2[0-4]|1[0-9]|[1-9])?[0-9])(\\.|\\b)){4}|\\3([0-9a-f]{1,4}(::?|\\b)){0,2}|[0-9a-f]{1,4}:[0-9a-f]{1,4})(?<![^:]:)(?<!\\.))](:(6553[0-5]|6(55[012]|(5[0-4]|[0-4]\\d)\\d)\\d|[1-5]?\\d{1,4}))?$")
private val converters: List<Converter> = listOf(
    object : Converter {
        override fun to(addressWithPort: CharSequence, defaultPort: Int) = parse(addressWithPort, defaultPort, hostnamePattern, 1, 5)
    },
    object : Converter {
        override fun to(addressWithPort: CharSequence, defaultPort: Int) = parse(addressWithPort, defaultPort, v4AddressPattern, 1, 7)
    },
    object : Converter {
        override fun to(addressWithPort: CharSequence, defaultPort: Int) = parse(addressWithPort, defaultPort, v6AddressPattern, 1, 15)
    },
    object : Converter {
        override fun to(addressWithPort: CharSequence, defaultPort: Int) = parse(addressWithPort, defaultPort, bracketV6AddressPattern, 1, 15)
    }
)
