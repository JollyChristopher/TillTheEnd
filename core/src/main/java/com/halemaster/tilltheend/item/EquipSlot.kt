package com.halemaster.tilltheend.item

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.halemaster.tilltheend.management.Translate
import com.halemaster.tilltheend.map.SPRITE_ITEM

enum class EquipSlot(internal val displayKey: Translate, internal val texture: TextureRegion) {
    HANDS(Translate.slotHands,TextureRegion(SPRITE_ITEM,7*16,2*16,16,16)),
    LEGS(Translate.slotLegs,TextureRegion(SPRITE_ITEM,8*16,2*16,16,16)),
    FEET(Translate.slotFeet,TextureRegion(SPRITE_ITEM,9*16,2*16,16,16)),
    LEFT_HAND(Translate.slotHandLeft,TextureRegion(SPRITE_ITEM,0*16,4*16,16,16)),
    BODY(Translate.slotBody,TextureRegion(SPRITE_ITEM,6*16,2*16,16,16)),
    RIGHT_HAND(Translate.slotHandRight,TextureRegion(SPRITE_ITEM,12*16,4*16,16,16)),
    ACCESSORY_1(Translate.slotAccessory,TextureRegion(SPRITE_ITEM,6*16,3*16,16,16)),
    HEAD(Translate.slotHead,TextureRegion(SPRITE_ITEM,5*16,2*16,16,16)),
    ACCESSORY_2(Translate.slotAccessory,TextureRegion(SPRITE_ITEM,3*16,5*16,16,16));

    fun localize(): String {
        return displayKey()
    }
}