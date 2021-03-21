package com.halemaster.tilltheend.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.halemaster.tilltheend.management.Translate
import com.halemaster.tilltheend.entity.EntityItem
import com.halemaster.tilltheend.entity.EntityPlayer
import com.halemaster.tilltheend.entity.ID_ITEM
import com.halemaster.tilltheend.entity.ID_PLAYER
import com.halemaster.tilltheend.item.EquipSlot
import com.halemaster.tilltheend.item.ItemProvider
import com.halemaster.tilltheend.map.WorldManager
import com.halemaster.tilltheend.stat.Stat
import com.halemaster.tilltheend.stat.Type
import ktx.inject.inject

class GUI() {
    internal val interfaceTexture = Texture(Gdx.files.internal("sprites/tiny_dungeon_interface.png"))
    internal var isHover = false
    internal var timer = 0f
    internal var itemCursor = 0

    fun render(delta: Float, batch: SpriteBatch, camera: OrthographicCamera) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        // basic player stats
        val player = inject<WorldManager>().getEntity(ID_PLAYER) as EntityPlayer
        val leftStart = -camera.viewportWidth/2+8
        val topStart = camera.viewportHeight/2-32
        batch.draw(player.portrait,leftStart,topStart-8)
        inject<TextRenderer>().render(player.name, batch, leftStart+32,topStart,Color.WHITE,1f)

        renderBar(player.hpBarRegions[0],player.hpBarRegions[1],player.hpCurrent,player.getStatTotal(Stat.HP),batch,
                leftStart+32,topStart-4)

        renderBar(player.mpBarRegions[0],player.mpBarRegions[1],player.mpCurrent,player.getStatTotal(Stat.MP),batch,
                leftStart+32,topStart-20)

        val currentVis = if (isHover) 1f else 0.25f

        // advanced stats
        var statPosition = -camera.viewportHeight/2-8
        repeat(Type.values().size, {
            if(player.getResistanceTotal(Type.values()[it]) > 0) {
                inject<TextRenderer>().render(Type.values()[it].localizeResistance
                    (player.getResistanceTotal(Type.values()[it])), batch,
                        camera.viewportWidth / 2 - 200, statPosition, Type.values()[it].color,currentVis)
                statPosition += 8
            }
        })
        repeat(Stat.values().size, {
            if(player.getStatTotal(Stat.values()[Stat.values().size-1-it]) > 0) {
                inject<TextRenderer>().render(Stat.values()[Stat.values().size-1-it].localize(player.getStatTotal
                    (Stat.values()[Stat.values().size-1-it])), batch,
                        camera.viewportWidth / 2 - 200, statPosition, Color.WHITE,currentVis)
                statPosition += 8
            }
        })

        // items
        inject<TextRenderer>().render(Translate.guiCarry(player.inventory.getWeight(),player.inventory.capacity),
                batch,-camera.viewportWidth/2+8,-camera.viewportHeight/2-8,Color.WHITE,currentVis)
        if(player.inventory.getItemCount() > 0) {
            if(itemCursor >= player.inventory.getItemCount()) itemCursor = 0
            val currentItem = inject<ItemProvider>()[player.inventory.getItem(itemCursor)]!!
            if(player.inventory.getItemCount() > 1) {
                val itemBeforeIndex = if (itemCursor > 0) itemCursor - 1 else player.inventory.getItemCount() - 1
                val itemBefore = inject<ItemProvider>()[player.inventory.getItem(itemBeforeIndex)]!!
                val itemAfterIndex = if (itemCursor < player.inventory.getItemCount() - 1) itemCursor + 1 else 0
                val itemAfter = inject<ItemProvider>()[player.inventory.getItem(itemAfterIndex)]!!
                itemAfter.render(batch, -camera.viewportWidth / 2 + 4, -camera.viewportHeight / 2 + 12,
                        currentVis * 0.5f, 16f)
                inject<TextRenderer>().render(itemAfter.localize(), batch,
                        -camera.viewportWidth / 2 + 20, -camera.viewportHeight / 2 + 4,
                        if(player.isEquipped(itemAfterIndex)) Color.YELLOW else Color.WHITE, currentVis * 0.5f)
                itemBefore.render(batch, -camera.viewportWidth / 2 + 4, -camera.viewportHeight / 2 + 96,
                        currentVis * 0.5f, 16f)
                inject<TextRenderer>().render(itemBefore.localize(), batch,
                        -camera.viewportWidth / 2 + 20, -camera.viewportHeight / 2 + 88,
                        if(player.isEquipped(itemBeforeIndex)) Color.YELLOW else Color.WHITE, currentVis * 0.5f)
            }
            // color is white, unless it is equipped, then it is yellow
            // current item has info about it (takes up more space too)
            currentItem.render(batch, -camera.viewportWidth / 2 + 4, -camera.viewportHeight / 2 + 64,
                    currentVis, 32f)
            inject<TextRenderer>().render(currentItem.localize(), batch,
                    -camera.viewportWidth / 2 + 36, -camera.viewportHeight / 2 + 72, if(player.isEquipped(itemCursor))
                Color.YELLOW else Color.WHITE, currentVis)
            var type = ""
            var color = Color.WHITE
            if(currentItem.equipmentSlots.isNotEmpty()) {
                type = currentItem.equipmentSlots.map { it.localize() }.distinct()
                        .joinToString((if(currentItem.equipmentAnd) " & " else ", "))
                color = if(currentItem.equipmentAnd) Color.ORANGE else Color.MAGENTA
            }
            else if(currentItem.usable) {
                type = Translate.guiUsable()
            }
            inject<TextRenderer>().render(type, batch, -camera.viewportWidth / 2 + 36,
                    -camera.viewportHeight / 2 + 60, color, currentVis)
            var currentTop = 48
            if(currentItem.damage.first != 0 || currentItem.damage.second != 0) {
                inject<TextRenderer>().render(Translate.guiDamage(currentItem.damage.first, currentItem.damage.second),
                        batch, -camera.viewportWidth / 2 + 4, -camera.viewportHeight / 2 + currentTop, Color.WHITE,
                        currentVis)
                currentTop -= 8
            }
            currentItem.equipmentStats.forEach {stat, value ->
                inject<TextRenderer>().render(stat.localize(value),batch, -camera.viewportWidth / 2 + 4,
                        -camera.viewportHeight / 2 + currentTop, Color.LIME, currentVis)
                currentTop -= 8
            }
            currentItem.equipmentResistance.forEach {resistance, value ->
                inject<TextRenderer>().render(resistance.localizeResistance(value),batch, -camera.viewportWidth / 2 + 4,
                        -camera.viewportHeight / 2 + currentTop, resistance.color, currentVis)
                currentTop -= 8
            }
            inject<TextRenderer>().render(currentItem.localizeDescription(),batch, -camera.viewportWidth / 2 + 4,
                    -camera.viewportHeight / 2 + currentTop, Color.WHITE, currentVis)
            currentTop -= 8
        }

        var x = 0
        var y = 0
        val prevColor = batch.color
        batch.color = prevColor.cpy().mul(1f,1f,1f,currentVis)
        EquipSlot.values().forEach {
            batch.draw(TextureRegion(interfaceTexture, 0, 6*16, 16, 16), -camera.viewportWidth / 2 + 256 + x * 20,
                    -camera.viewportHeight / 2 + 20 + y * 20)
            x++
            if(x >= 3) {
                x = 0
                y++
            }
        }

        x = 0
        y = 0
        batch.color = prevColor.cpy().mul(1f,1f,1f,currentVis*0.25f)
        EquipSlot.values().forEach { slot ->
            batch.draw(slot.texture, -camera.viewportWidth / 2 + 256 + x * 20, -camera.viewportHeight / 2 + 20 + y * 20)
            x++
            if(x >= 3) {
                x = 0
                y++
            }
        }
        batch.color = prevColor

        x = 0
        y = 0
        EquipSlot.values().forEach { slot ->
            player.getEquipment()[slot]?.render(batch,-camera.viewportWidth / 2 + 256 + x * 20,
                    -camera.viewportHeight / 2 + 20 + y * 20,currentVis,16f)
            x++
            if(x >= 3) {
                x = 0
                y++
            }
        }

        batch.end()

        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && player.inventory.getItemCount() > 0) {
            if(player.isEquipped(itemCursor)) {
                player.unequipItem(itemCursor)
            }
            else {
                inject<WorldManager>().addEntity(EntityItem(ID_ITEM + inject<WorldManager>().itemIdCurrent++,
                        player.body.position.x, player.body.position.y, player.inventory.removeItem(itemCursor)))
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if(!player.isEquipped(itemCursor) && inject<ItemProvider>()[player.inventory.getItem(itemCursor)]
                    ?.equipmentSlots?.isNotEmpty() ?: false) {
                player.equipItem(itemCursor)
            }
            else if(inject<ItemProvider>()[player.inventory.getItem(itemCursor)]?.usable ?: false) {
                if(inject<ItemProvider>()[player.inventory.getItem(itemCursor)]?.use?.invoke() ?: false) {
                    player.inventory.removeItem(itemCursor)
                }
            }
        }

        if(Gdx.input.y >= camera.viewportHeight-120) {
            isHover = true
            timer = 0f
        }

        if(isHover) {
            timer += delta
            if(timer > 5) {
                isHover = false
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            itemCursor++
            if(itemCursor >= player.inventory.getItemCount()) {
                itemCursor = 0
            }
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            itemCursor--
            if(itemCursor < 0) {
                itemCursor = player.inventory.getItemCount() - 1
            }
        }
    }

    fun renderBar(backRegions: List<TextureRegion>, frontRegions: List<TextureRegion>, current: Int, max: Int,
                  batch: SpriteBatch, x: Float, y: Float) {
        repeat(backRegions.size, {
            batch.draw(backRegions[it], x+16*it,y)
        })
        val percent = current.toFloat() / max.toFloat()
        val barChunks = 1 / frontRegions.size.toFloat()
        repeat(frontRegions.size, {
            if(percent > barChunks * it) {
                val chunkPercent = if (percent > barChunks * (it + 1)) 1f
                else (percent - barChunks * it) / barChunks
                val newRegion = TextureRegion(frontRegions[it], 0,0,(16 * chunkPercent).toInt(),
                        frontRegions[it].regionHeight)
                batch.draw(newRegion,x+16*it,y,chunkPercent*16f, 16f)
            }
        })
        inject<TextRenderer>().render(current.toString() + "/" + max.toString(), batch, x + 4, y - 4, Color.WHITE,1f)
    }
}