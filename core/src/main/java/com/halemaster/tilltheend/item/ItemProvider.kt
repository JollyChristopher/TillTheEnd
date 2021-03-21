package com.halemaster.tilltheend.item

import com.halemaster.tilltheend.management.Translate
import com.halemaster.tilltheend.stat.Stat
import com.halemaster.tilltheend.stat.Type

class ItemProvider {
    private val items = mutableMapOf<String, Item>()

    operator fun get(id: String): Item? {
        return items[id]
    }

    fun removeItem(id: String): Item? {
        return items.remove(id)
    }

    fun addItem(item: Item) {
        items.put(item.id, item)
    }

    init {
        addItem(Item("potion",0, Translate.itemPotion,1,1, Translate.itemPotionDesc))
        addItem(Item("dagger",1, Translate.itemDagger,0,4, Translate.itemDaggerDesc,equipmentSlots =
            listOf(EquipSlot.LEFT_HAND,EquipSlot.RIGHT_HAND),equipmentStats = mapOf(Pair(Stat.AGL,1)),
                damage = Pair(1,4)))
    }
}