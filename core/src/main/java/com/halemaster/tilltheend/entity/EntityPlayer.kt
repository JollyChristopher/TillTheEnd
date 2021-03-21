package com.halemaster.tilltheend.entity

import box2dLight.PointLight
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.Filter

const val ID_PLAYER = "player"

class EntityPlayer(spriteSheet: Texture, spriteArray: List<List<Pair<Int, Int>>>, x: Float, y: Float,
                   speed: Float, size: Float, depth: Float) :
        EntityLiving(ID_PLAYER, spriteSheet, spriteArray, x, y, speed, size, depth, { _, _ ->}, PLAYER,
                (WALL + BASIC + ENEMY + ITEM).toShort()) {
    internal var light: PointLight = PointLight(worldManager.rayHandler, 120, Color(1f, 1f, 1f, 0.75f), 1f, x, y)
    internal var portrait = TextureRegion(Texture(Gdx.files.internal("sprites/tiny_dungeon_portraits.png")),0,0,32,32)
    internal var name = "Jimbo"

    init {
        val filter = Filter()
        filter.categoryBits = 0x0001
        filter.maskBits = 0x0001
        light.setContactFilter(filter)
        inventory.capacity = 6
        inventory.addItem("potion")
        inventory.addItem("dagger")
        inventory.addItem("dagger")
        equipItem(1)
    }

    fun changeLightDistance(distance: Float) {
        light.distance = distance
    }

    override fun update(delta: Float) {
        super.update(delta)

        if(Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            hpCurrent--
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT)) {
            hpCurrent++
        }

        light.setPosition(body.position.x, body.position.y)

        var dir = 0
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            dir = 1
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            if(dir == 1) dir = 0
            else dir = 2
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            if(dir == 1) dir = 5
            else if(dir == 2)  dir = 6
            else dir = 3
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            if(dir == 1) dir = 7
            else if(dir == 2) dir = 8
            else if(dir == 3) dir = 0
            else if(dir == 5) dir = 1
            else if(dir == 6) dir = 2
            else dir = 4
        }
        val moveSpeed = 0.05f
        if(dir > 0) {
            if(dir == 1 || dir == 5 || dir == 7) {
                body.applyLinearImpulse(-moveSpeed, 0f, body.position.x, body.position.y, true)
            }
            if(dir == 2 || dir == 6 || dir == 8) {
                body.applyLinearImpulse(moveSpeed, 0f, body.position.x, body.position.y, true)
            }
            if(dir == 3 || dir == 5 || dir == 6) {
                body.applyLinearImpulse(0f, moveSpeed, body.position.x, body.position.y, true)
            }
            if(dir == 4 || dir == 7 || dir == 8) {
                body.applyLinearImpulse(0f, -moveSpeed, body.position.x, body.position.y, true)
            }
        }
    }
}