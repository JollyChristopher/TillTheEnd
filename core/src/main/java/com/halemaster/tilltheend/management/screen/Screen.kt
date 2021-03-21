package com.halemaster.tilltheend.management.screen

import com.halemaster.tilltheend.management.TillTheEndGame

interface Screen {
    val game: TillTheEndGame

    fun render(delta: Float)
    fun dispose()
}