package com.halemaster.tilltheend.management

import com.halemaster.tilltheend.item.ItemProvider
import com.halemaster.tilltheend.map.WorldDefiner
import ktx.app.KotlinApplication
import com.halemaster.tilltheend.map.WorldManager
import ktx.inject.inject
import ktx.inject.register
import java.util.*
import com.halemaster.tilltheend.ui.TextRenderer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.halemaster.tilltheend.management.screen.Screen
import com.halemaster.tilltheend.management.screen.SplashScreen

const val VIEWPORT_SIZE = 640f

class TillTheEndGame : KotlinApplication() {
    internal var viewport = Rectangle(0f,0f,VIEWPORT_SIZE,VIEWPORT_SIZE)
    internal lateinit var currentScreen: Screen

    fun setCurrentScreen(screen: Screen) {
        currentScreen.dispose()
        currentScreen = screen
    }

    override fun create() {
        register {
            bindSingleton(WorldManager())
            bindSingleton(WorldDefiner())
            bindSingleton(ItemProvider())
            bindSingleton(Random())
            bindSingleton(TextRenderer())
        }
        reloadLanguage(Locale.getDefault())
        currentScreen = SplashScreen(this)
    }

    override fun render(delta: Float) {
        Gdx.gl.glViewport(viewport.x.toInt(), viewport.y.toInt(),
                viewport.width.toInt(), viewport.height.toInt())
        currentScreen.render(delta)
    }

    override fun pause() {
        // save here
    }

    override fun dispose() {
        currentScreen.dispose()
        inject<TextRenderer>().dispose()
    }


    override fun resize(width: Int, height: Int)
    {
        viewport = Rectangle((width - VIEWPORT_SIZE)  / 2, (height - VIEWPORT_SIZE) / 2, VIEWPORT_SIZE, VIEWPORT_SIZE)
    }
}
