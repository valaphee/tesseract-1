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

import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.util.nbt.ListTag
import com.valaphee.tesseract.util.nbt.NbtException
import com.valaphee.tesseract.util.nbt.compoundTag
import com.valaphee.tesseract.util.nbt.listTag
import com.valaphee.tesseract.util.nbt.ofBool

fun CompoundTag.getBoolOrNull(key: String): Boolean? {
    val tag = this[key] ?: return null
    if (tag.isNumber) return tag.asNumberTag()!!.toByte() != 0.toByte()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getBool(key: String): Boolean {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a number")
    if (tag.isNumber) return tag.asNumberTag()!!.toByte() != 0.toByte()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.setBool(key: String, value: Boolean) = set(key, ofBool(value))

fun CompoundTag.getByteOrNull(key: String): Byte? {
    val tag = this[key] ?: return null
    if (tag.isNumber) return tag.asNumberTag()!!.toByte()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getByte(key: String): Byte {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a number")
    if (tag.isNumber) return tag.asNumberTag()!!.toByte()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getShortOrNull(key: String): Short? {
    val tag = this[key] ?: return null
    if (tag.isNumber) return tag.asNumberTag()!!.toShort()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getShort(key: String): Short {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a number")
    if (tag.isNumber) return tag.asNumberTag()!!.toShort()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getIntOrNull(key: String): Int? {
    val tag = this[key] ?: return null
    if (tag.isNumber) return tag.asNumberTag()!!.toInt()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getInt(key: String): Int {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a number")
    if (tag.isNumber) return tag.asNumberTag()!!.toInt()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getLongOrNull(key: String): Long? {
    val tag = this[key] ?: return null
    if (tag.isNumber) return tag.asNumberTag()!!.toLong()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getLong(key: String): Long {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a number")
    if (tag.isNumber) return tag.asNumberTag()!!.toLong()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getFloatOrNull(key: String): Float? {
    val tag = this[key] ?: return null
    if (tag.isNumber) return tag.asNumberTag()!!.toFloat()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getFloat(key: String): Float {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a number")
    if (tag.isNumber) return tag.asNumberTag()!!.toFloat()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getDoubleOrNull(key: String): Double? {
    val tag = this[key] ?: return null
    if (tag.isNumber) return tag.asNumberTag()!!.toDouble()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getDouble(key: String): Double {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a number")
    if (tag.isNumber) return tag.asNumberTag()!!.toDouble()
    throw NbtException("Expected $key to be a number")
}

fun CompoundTag.getByteArrayOrNull(key: String): ByteArray? {
    val tag = this[key] ?: return null
    if (tag.isArray) return tag.asArrayTag()!!.toByteArray()
    throw NbtException("Expected $key to be an array")
}

fun CompoundTag.getByteArray(key: String): ByteArray {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a array")
    if (tag.isArray) return tag.asArrayTag()!!.toByteArray()
    throw NbtException("Expected $key to be an array")
}

fun CompoundTag.getIntArrayOrNull(key: String): IntArray? {
    val tag = this[key] ?: return null
    if (tag.isArray) return tag.asArrayTag()!!.toIntArray()
    throw NbtException("Expected $key to be an array")
}

fun CompoundTag.getIntArray(key: String): IntArray {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a array")
    if (tag.isArray) return tag.asArrayTag()!!.toIntArray()
    throw NbtException("Expected $key to be an array")
}

fun CompoundTag.getLongArrayOrNull(key: String): LongArray? {
    val tag = this[key] ?: return null
    if (tag.isArray) return tag.asArrayTag()!!.toLongArray()
    throw NbtException("Expected $key to be an array")
}

fun CompoundTag.getLongArray(key: String): LongArray {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a array")
    if (tag.isArray) return tag.asArrayTag()!!.toLongArray()
    throw NbtException("Expected $key to be an array")
}

fun CompoundTag.getStringOrNull(key: String): String? {
    val tag = this[key] ?: return null
    if (tag.isArray) return tag.asArrayTag()!!.valueToString()
    throw NbtException("Expected $key to be an array")
}

fun CompoundTag.getString(key: String): String {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a array")
    if (tag.isArray) return tag.asArrayTag()!!.valueToString()
    throw NbtException("Expected $key to be an array")
}

fun CompoundTag.getListTagOrNull(key: String): ListTag? {
    val tag = this[key] ?: return null
    if (tag.isList) return tag.asListTag()
    throw NbtException("Expected $key to be a list tag")
}

fun CompoundTag.getListTag(key: String): ListTag {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a list tag")
    if (tag.isList) return tag.asListTag()!!
    throw NbtException("Expected $key to be a list tag")
}

fun CompoundTag.getOrCreateListTag(key: String): ListTag {
    this[key]?.let {
        if (it.isList) return it.asListTag()!!
        throw NbtException("Expected $key to be a list tag")
    }
    val tag = listTag()
    this[key] = tag
    return tag
}

fun CompoundTag.getCompoundTagOrNull(key: String): CompoundTag? {
    val tag = this[key] ?: return null
    if (tag.isCompound) return tag.asCompoundTag()
    throw NbtException("Expected $key to be a compound tag")
}

fun CompoundTag.getCompoundTag(key: String): CompoundTag {
    val tag = this[key] ?: throw NbtException("Missing $key, expected to find a compound tag")
    if (tag.isCompound) return tag.asCompoundTag()!!
    throw NbtException("Expected $key to be a compound tag")
}

fun CompoundTag.getOrCreateCompoundTag(key: String): CompoundTag {
    this[key]?.let {
        if (it.isCompound) return it.asCompoundTag()!!
        throw NbtException("Expected $key to be a compound tag")
    }
    val tag = compoundTag()
    this[key] = tag
    return tag
}
