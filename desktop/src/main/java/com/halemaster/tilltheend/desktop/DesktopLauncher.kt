package com.halemaster.tilltheend.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.halemaster.tilltheend.management.TillTheEndGame
import com.halemaster.tilltheend.management.VIEWPORT_SIZE

object DesktopLauncher {
    @JvmStatic fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.width = VIEWPORT_SIZE.toInt()
        config.height = VIEWPORT_SIZE.toInt()
        LwjglApplication(TillTheEndGame(), config)
    }
}
