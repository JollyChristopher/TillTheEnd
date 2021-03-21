package com.halemaster.tilltheend.entity

import com.halemaster.tilltheend.map.SPRITE_WORLD
import com.halemaster.tilltheend.map.WorldDefiner
import ktx.inject.inject

const val ID_TELEPORT = "teleport"

class EntityTeleport(id: String, x: Float, y: Float, size: Float,
                 location: String, teleportX: Float, teleportY: Float) :
        Entity(id, SPRITE_WORLD, List(0,{List(0,{Pair(0,0)})}), x, y, true, 0f, size, 0f,
                { _, contact ->
                    if(contact?.fixtureA?.userData is EntityPlayer || contact?.fixtureB?.userData is EntityPlayer) {
                        inject<WorldDefiner>().travel(location,teleportX,teleportY)
                    }
                },WALL, PLAYER)