package com.halemaster.tilltheend.management.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.halemaster.tilltheend.management.TillTheEndGame
import com.halemaster.tilltheend.ui.TextRenderer
import ktx.inject.inject

private const val FADE_TIME = 4f
private const val SPLASH_TIME = 15f
private const val LOCATION = 50f

class SplashScreen(override val game: TillTheEndGame) : Screen {
    internal var currentTime = 0f
    internal var batch: SpriteBatch = SpriteBatch()

    override fun render(delta: Float) {
        currentTime += delta

        var alpha = minOf(1f, currentTime / FADE_TIME)
        if(currentTime >= SPLASH_TIME - FADE_TIME) {
            alpha = minOf(1f, (SPLASH_TIME - currentTime) / FADE_TIME)
        }

        batch.begin()
        inject<TextRenderer>().render("Till the End", batch, LOCATION, LOCATION, Color.WHITE, alpha)
        batch.end()

        if(currentTime >= SPLASH_TIME || Gdx.input.isKeyPressed(Input.Keys.ANY_KEY))
            game.setCurrentScreen(MenuScreen(game))
    }

    override fun dispose() {

    }
}
