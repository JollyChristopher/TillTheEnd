package com.halemaster.tilltheend.management.screen

import com.halemaster.tilltheend.management.TillTheEndGame
import com.halemaster.tilltheend.map.WorldDefiner
import com.halemaster.tilltheend.map.WorldManager
import ktx.inject.inject

class WorldScreen(override val game: TillTheEndGame) : Screen {
    internal var worldManager: WorldManager = inject<WorldManager>()

    init {
        inject<WorldDefiner>().travel("test",10f,8f)
    }

    override fun render(delta: Float) {
        worldManager.render(delta)
    }

    override fun dispose() {
        worldManager.dispose()
    }
}