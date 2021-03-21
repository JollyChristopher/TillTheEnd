package com.halemaster.tilltheend.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class TextRenderer() {
    internal var font = BitmapFont(Gdx.files.internal("sprites/tiny_dungeon_font-export.fnt"),
        Gdx.files.internal("sprites/tiny_dungeon_font-export.png"),false)

    fun render(text: String, batch: SpriteBatch, x: Float, y: Float, color: Color, alpha: Float) {
        val prevColor = font.color
        font.color = color.cpy()
        font.color.mul(1f,1f,1f,alpha)
        font.draw(batch, text.toUpperCase(),x,y)
        font.color = prevColor
    }

    fun dispose() {
        font.dispose()
    }
}