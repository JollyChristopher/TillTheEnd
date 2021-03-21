package com.halemaster.tilltheend.management

import ktx.i18n.BundleLine
import ktx.i18n.I18n
import java.util.*

fun reloadLanguage(locale: Locale) {
    I18n.load("lang/Language", locale)
}

enum class Translate : BundleLine {
    slotHands,
    slotLegs,
    slotFeet,
    slotHandLeft,
    slotBody,
    slotHandRight,
    slotAccessory,
    slotHead,

    itemPotion,
    itemPotionDesc,
    itemDagger,
    itemDaggerDesc,

    statHp,
    statMp,
    statStr,
    statAgl,
    statInt,
    statCarry,
    statAcc,
    statDodge,
    statArmor,
    statCrit,
    statHaste,

    typePhysicalDamage,
    typeFireDamage,
    typeIceDamage,
    typeThunderDamage,

    typePhysicalResistance,
    typeFireResistance,
    typeIceResistance,
    typeThunderResistance,

    guiCarry,
    guiUsable,
    guiDamage,
}