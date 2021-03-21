package com.halemaster.tilltheend.entity

import com.halemaster.tilltheend.item.ItemProvider
import com.halemaster.tilltheend.map.SPRITE_ITEM
import com.halemaster.tilltheend.map.WorldManager
import ktx.inject.inject

const val ID_ITEM = "item"

class EntityItem(id: String, x: Float, y: Float, internal var item: String):
        Entity(id, SPRITE_ITEM, listOf(listOf(Pair(inject<ItemProvider>()[item]!!.texX,
                inject<ItemProvider>()[item]!!.texY))), x, y, false, 0f, 0.5f, 10f,
                { entity, contact ->
                    if((contact?.fixtureA?.userData is EntityPlayer || contact?.fixtureB?.userData is EntityPlayer) &&
                            (entity as EntityItem).timer >= 2f){
                        // delete this and add to player
                        val player = if(contact.fixtureA?.userData is EntityPlayer)
                            contact.fixtureA?.userData as EntityPlayer else contact.fixtureB?.userData as EntityPlayer
                        player.inventory.addItem(entity.item).forEach {
                            inject<WorldManager>().addEntity(EntityItem(ID_ITEM + inject<WorldManager>().itemIdCurrent++,
                                    player.body.position.x, player.body.position.y, it))
                        }
                        inject<WorldManager>().removeEntity(entity.id)
                        entity.timer = -100f
                    }
                }, ITEM) {
    internal var timer = 0f

    override fun update(delta: Float) {
        super.update(delta)
        timer += delta
    }
}