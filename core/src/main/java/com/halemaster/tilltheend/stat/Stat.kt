package com.halemaster.tilltheend.stat

import com.halemaster.tilltheend.management.Translate

enum class Stat(internal var display: Translate) {
    HP(Translate.statHp),
    MP(Translate.statMp),
    STR(Translate.statStr),
    AGL(Translate.statAgl),
    INT(Translate.statInt),
    CARRY(Translate.statCarry),
    ACC(Translate.statAcc),
    DODGE(Translate.statDodge),
    ARMOR(Translate.statArmor),
    CRIT(Translate.statCrit),
    HASTE(Translate.statHaste);

    fun localize(arg: Int): String {
        return display(arg)
    }
}