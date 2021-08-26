/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.inject.ProvidedBy
import java.net.InetSocketAddress

/**
 * @author Kevin Ludwig
 */
@ProvidedBy(Config.Provider::class)
data class Config(
    @get:JsonProperty("address") var address: InetSocketAddress,
    @get:JsonProperty("timeout") var timeout: Int,
    @get:JsonProperty("maximumPlayers") var maximumPlayers: Int,
) {
    class Provider : com.google.inject.Provider<Config> {
        override fun get() = Config(InetSocketAddress("127.0.0.1", 19132), 5_000, 20)
    }
}
