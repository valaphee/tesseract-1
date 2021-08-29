/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

/**
 * @author Kevin Ludwig
 */
data class AppearanceAnimation constructor(
    val image: AppearanceImage,
    val type: Type,
    val frames: Float,
    val expression: Expression = Expression.Linear
) {
    enum class Type {
        None, Head, Body32, Body128
    }

    enum class Expression {
        Linear, Blinking
    }
}
