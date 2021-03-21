package com.halemaster.tilltheend.stat

import com.badlogic.gdx.graphics.Color
import com.halemaster.tilltheend.management.Translate

enum class Type(internal val damageKey: Translate, internal val resistanceKey: Translate, internal val color: Color) {
    PHYSICAL(Translate.typePhysicalDamage, Translate.typePhysicalResistance, Color.DARK_GRAY),
    FIRE(Translate.typeFireDamage, Translate.typeFireResistance, Color.ORANGE),
    ICE(Translate.typeIceDamage, Translate.typeIceResistance, Color.CYAN),
    THUNDER(Translate.typeThunderDamage, Translate.typeThunderResistance, Color.YELLOW);

    fun localizeDamage(arg: Int): String {
        return damageKey(arg)
    }

    fun localizeResistance(arg: Int): String {
        return resistanceKey(arg)
    }
}