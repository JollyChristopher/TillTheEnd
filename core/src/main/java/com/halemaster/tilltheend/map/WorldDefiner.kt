package com.halemaster.tilltheend.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.halemaster.tilltheend.entity.*
import ktx.inject.inject

val SPRITE_MONSTER = Texture(Gdx.files.internal("sprites/tiny_dungeon_monsters.png"))
val SPRITE_ITEM = Texture(Gdx.files.internal("sprites/tiny_dungeon_items.png"))
val SPRITE_WORLD = Texture(Gdx.files.internal("sprites/tiny_dungeon_world.png"))
val SPRITE_INTERFACE = Texture(Gdx.files.internal("sprites/tiny_dungeon_interface.png"))

class WorldDefiner {
    internal var worldManager = inject<WorldManager>()

    fun travel(world: String, playerX: Float, playerY: Float) {
        if("test" == world) {
            createTest(playerX, playerY)
        }
        else if("dungeon" == world) {
            createDungeon(playerX, playerY)
        }
    }

    private fun createTest(playerX: Float, playerY: Float) {
        worldManager.newWorld("tilemaps/test.tmx", 32f, 32f,false)
        worldManager.addEntity(EntityPlayer(SPRITE_MONSTER,List(4,{ anim ->
            List(2,{frame -> Pair(anim,frame)})
        }),playerX,playerY,2f,0.5f,0f))
        worldManager.addEntity(EntityTeleport(ID_TELEPORT+0,28.5f,28.5f,0.5f,"dungeon",23f,22f))
        worldManager.addEntity(EntityLiving("monster1", SPRITE_MONSTER,List(4,{ anim ->
            List(2,{frame -> Pair(anim+4,frame+6)})
        }),15f,15f,5f,2f,10f,{ _, _ ->}, ENEMY, (WALL + BASIC + PLAYER).toShort()))
    }

    fun createDungeon(playerX: Float, playerY: Float) {
        worldManager.newWorld("tilemaps/dungeon.tmx",32f,32f,true)
        worldManager.addEntity(EntityPlayer(SPRITE_MONSTER,List(4,{ anim ->
            List(2,{frame -> Pair(anim,frame)})
        }),playerX,playerY,2f,0.5f,0f))
        (worldManager.getEntity(ID_PLAYER) as EntityPlayer).changeLightDistance(20f)
        worldManager.addEntity(EntityTeleport(ID_TELEPORT+0,25.5f,21.5f,0.5f,"test",26f,28f))
        // doors
        var door = 0
        worldManager.addEntity(EntityDoor(ID_DOOR+door++, SPRITE_WORLD,List(2,{ anim ->
            List(1,{Pair(2+anim,10)})
        }),22.7f,23.7f,0f,0.7f,20f))
        worldManager.addEntity(EntityDoor(ID_DOOR+door++, SPRITE_WORLD,List(2,{ anim ->
            List(1,{Pair(2+anim,10)})
        }),14.7f,25.7f,0f,0.7f,20f))
        worldManager.addEntity(EntityDoor(ID_DOOR+door++, SPRITE_WORLD,List(2,{ anim ->
            List(1,{Pair(2+anim,10)})
        }),25.3f,14.7f,0f,0.7f,20f))
        worldManager.addEntity(EntityDoor(ID_DOOR+door++, SPRITE_WORLD,List(2,{ anim ->
            List(1,{Pair(2+anim,10)})
        }),6.7f,14.7f,0f,0.7f,20f))
        worldManager.addEntity(EntityDoor(ID_DOOR+door++, SPRITE_WORLD,List(2,{ anim ->
            List(1,{Pair(0+anim*4,10)})
        }),13.5f,28.5f,0f,0.5f,20f))
        worldManager.addEntity(EntityDoor(ID_DOOR+door++, SPRITE_WORLD,List(2,{ anim ->
            List(1,{Pair(0+anim*4,10)})
        }),8.5f,20.5f,0f,0.5f,20f))
        worldManager.addEntity(EntityDoor(ID_DOOR+door++, SPRITE_WORLD,List(2,{ anim ->
            List(1,{Pair(0+anim*4,10)})
        }),15.5f,18.5f,0f,0.5f,20f))
        worldManager.addEntity(EntityDoor(ID_DOOR+door++, SPRITE_WORLD,List(2,{ anim ->
            List(1,{Pair(0+anim*4,10)})
        }),5.5f,9.5f,0f,0.5f,20f))
        worldManager.addEntity(EntityDoor(ID_DOOR+door, SPRITE_WORLD,List(2,{ anim ->
            List(1,{Pair(0+anim*4,10)})
        }),26.5f,12.5f,0f,0.5f,20f))
    }
}