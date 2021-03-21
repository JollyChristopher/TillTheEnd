package com.halemaster.tilltheend.management.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.halemaster.tilltheend.management.TillTheEndGame
import com.halemaster.tilltheend.ui.TextRenderer
import ktx.inject.inject

private const val FADE_TIME = 2f
private const val TITLE_LOCATION_X = 50f
private const val TITLE_LOCATION_Y = 550f

class MenuScreen(override val game: TillTheEndGame) : Screen {
    internal var currentTime = 0f
    internal val batch: SpriteBatch = SpriteBatch()
    internal val skin = Skin(Gdx.files.internal("sprites/uiskin.json"))
    internal val stage: Stage = Stage(ScreenViewport())
    internal val userContent: Table = Table(skin)
    internal val userScroll: ScrollPane = ScrollPane(userContent, skin)
    internal val container = Container<ScrollPane>()

    init {
        Gdx.input.inputProcessor = stage
        stage.addActor(container)

        container.actor = userScroll
        container.setSize(stage.width - 100f,256f)
        container.x = TITLE_LOCATION_X
        container.y = TITLE_LOCATION_Y - 256f
        container.fill()

        userContent.top().left()

        userScroll.setForceScroll(false, true)

        repeat(10, {
            val button = TextButton("VALUEEEEE", skin)
            button.labelCell.padTop(40f)
            userContent.add(button).height(16f).width(480f)
            userContent.row()
        })
    }

    override fun render(delta: Float) {
        currentTime += delta

        batch.begin()
        inject<TextRenderer>().render("Till the End", batch, TITLE_LOCATION_X, TITLE_LOCATION_Y, Color.WHITE,
                minOf(1f, currentTime / FADE_TIME))
        batch.end()
        stage.act(delta)
        stage.draw()

        //game.setCurrentScreen(WorldScreen(game))
    }

    override fun dispose() {
        stage.dispose()
    }
}