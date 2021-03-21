package com.halemaster.tilltheend.item

import ktx.inject.inject
import java.util.*

open class Inventory(internal var capacity: Int, internal var dropCallbacks: List<(index: Int) -> Unit> = emptyList()) {
    private val items = mutableListOf<String>()

    fun getItemCount() = items.size
    fun getItem(index: Int): String {
        if(index >= getItemCount()) return ""
        return items[index]
    }

    fun getWeight(): Int {
        val currentWeight = items
                .map { inject<ItemProvider>()[it] }
                .sumBy { it?.weight ?: 0 }

        return currentWeight
    }

    fun removeItem(index: Int): String {
        dropCallbacks.forEach { it(index) }
        return items.removeAt(index)
    }

    fun addItem(id: String): List<String> {
        val newItem = inject<ItemProvider>()[id]
        items.add(newItem!!.id)
        return dropExtra()
    }

    fun dropExtra(): List<String> {
        val dropItems = mutableListOf<String>()
        while(getWeight() > capacity) {
            dropItems.add(removeItem(inject<Random>().nextInt(getItemCount())))
        }
        return dropItems
    }
}