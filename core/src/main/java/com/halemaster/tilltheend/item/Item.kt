package com.halemaster.tilltheend.item

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.halemaster.tilltheend.management.Translate
import com.halemaster.tilltheend.map.SPRITE_ITEM
import com.halemaster.tilltheend.stat.Stat
import com.halemaster.tilltheend.stat.Type

open class Item(internal val id: String,
                internal val weight: Int,
                internal val displayKey: Translate,
                internal val texX: Int,
                internal val texY: Int,
                internal val description: Translate,
                internal val usable: Boolean = false,
                internal val use: () -> Boolean = {false},
                internal val equipmentSlots: List<EquipSlot> = listOf(),
                internal val equipmentAnd: Boolean = false,
                internal val equipmentStats: Map<Stat, Int> = mapOf(),
                internal val equipmentResistance: Map<Type, Int> = mapOf(),
                internal val damage: Pair<Int, Int> = Pair(0, 0)) {
    private val region: TextureRegion = TextureRegion(SPRITE_ITEM, texX*16, texY*16, 16, 16)

    fun render(batch: SpriteBatch, x: Float, y: Float, alpha: Float, scale: Float) {
        val prevColor = batch.color
        batch.color = prevColor.cpy().mul(1f,1f,1f,alpha)
        batch.draw(region,x,y,scale,scale)
        batch.color = prevColor
    }

    fun localize(): String {
        return displayKey()
    }

    fun localizeDescription(): String {
        return description()
    }
}