package com.halemaster.tilltheend.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.physics.box2d.Filter

const val ID_DOOR = "door"

class EntityDoor(id: String, spriteSheet: Texture, spriteArray: List<List<Pair<Int, Int>>>, x: Float, y: Float,
                 speed: Float, size: Float, depth: Float) :
        Entity(id, spriteSheet, spriteArray, x, y, true, speed, size, depth,
                {entity, contact ->
                    if(contact?.fixtureA?.userData is EntityPlayer || contact?.fixtureB?.userData is EntityPlayer) {
                        entity.fixture.isSensor = true
                        val filter = Filter()
                        filter.categoryBits = BASIC
                        entity.fixture.filterData = filter
                        entity.currentAnimation = 1
                    }
                },WALL)