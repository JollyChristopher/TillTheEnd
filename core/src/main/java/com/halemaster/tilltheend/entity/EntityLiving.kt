package com.halemaster.tilltheend.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.halemaster.tilltheend.item.EquipSlot
import com.halemaster.tilltheend.item.Inventory
import com.halemaster.tilltheend.item.Item
import com.halemaster.tilltheend.item.ItemProvider
import com.halemaster.tilltheend.map.SPRITE_INTERFACE
import com.halemaster.tilltheend.map.WorldManager
import com.halemaster.tilltheend.stat.Stat
import com.halemaster.tilltheend.stat.Type
import ktx.inject.inject
import java.util.*

open class EntityLiving(id: String, spriteSheet: Texture, spriteArray: List<List<Pair<Int, Int>>>, x: Float,
                  y: Float, speed: Float, size: Float,
                  depth: Float, collides: (Entity, Contact?) -> Unit,type: Short = WALL, mask: Short = 0xFFF):
        Entity(id, spriteSheet, spriteArray, x, y, false, speed, size, depth, collides, type, mask) {
    internal var hpCurrent = 10
    internal var mpCurrent = 10
    internal val stats = mutableMapOf<Stat,Int>()
    internal val resistance = mutableMapOf<Type, Int>()
    internal val hpBarRegions = listOf(
            listOf(
                    TextureRegion(SPRITE_INTERFACE, 0*16, 10*16, 16, 16),
                    TextureRegion(SPRITE_INTERFACE, 1*16, 10*16, 16, 16),
                    TextureRegion(SPRITE_INTERFACE, 2*16, 10*16, 16, 16)
            ),
            listOf(
                    TextureRegion(SPRITE_INTERFACE, 0*16, 11*16, 16, 16),
                    TextureRegion(SPRITE_INTERFACE, 1*16, 11*16, 16, 16),
                    TextureRegion(SPRITE_INTERFACE, 2*16, 11*16, 16, 16)
            ))
    internal val mpBarRegions = listOf(
            listOf(
                    TextureRegion(SPRITE_INTERFACE, 0*16+48, 10*16, 16, 16),
                    TextureRegion(SPRITE_INTERFACE, 1*16+48, 10*16, 16, 16),
                    TextureRegion(SPRITE_INTERFACE, 2*16+48, 10*16, 16, 16)
            ),
            listOf(
                    TextureRegion(SPRITE_INTERFACE, 0*16+48, 11*16, 16, 16),
                    TextureRegion(SPRITE_INTERFACE, 1*16+48, 11*16, 16, 16),
                    TextureRegion(SPRITE_INTERFACE, 2*16+48, 11*16, 16, 16)
            ))
    internal val inventory = Inventory(0, listOf({index ->
        equipment.map { Pair(it.key, it.value) }.toMap().forEach {
            slot, inBag -> if(inBag >= index) unequipItem(slot) }
    }))
    private val equipment = mutableMapOf<EquipSlot, Int>()
    private var prevX = x
    private var prevY = y

    init {
        stats[Stat.HP] = hpCurrent
        stats[Stat.MP] = mpCurrent
    }

    fun getStat(stat: Stat): Int {
        return stats[stat] ?: 0
    }

    fun getResistance(type: Type): Int {
        return resistance[type] ?: 0
    }

    fun getEquipment(): Map<EquipSlot, Item> {
        return equipment.filter { inject<ItemProvider>()[inventory.getItem(it.value)] != null }
                .mapValues { inject<ItemProvider>()[inventory.getItem(it.value)]!! }
    }

    fun unequipItem(slot: EquipSlot) {
        unequipItems(Arrays.asList(slot))
    }

    fun unequipItem(index: Int) {
        unequipItems(equipment.filter { it.value == index }.map { it.key })
    }

    fun unequipItems(slots: List<EquipSlot>) {
        slots.forEach { equipment.remove(it) }
        inventory.dropExtra().forEach {
            inject<WorldManager>().addEntity(EntityItem(ID_ITEM + inject<WorldManager>().itemIdCurrent++,
                    body.position.x, body.position.y, it))
        }
    }

    fun equipItem(index: Int) {
        val item = inject<ItemProvider>()[inventory.getItem(index)]!!
        val freeSlots = mutableListOf<EquipSlot>()
        if(item.equipmentAnd) {
            // unequip all in slots
            item.equipmentSlots.forEach { unequipItem(it) }
            freeSlots.addAll(item.equipmentSlots)
        } else {
            // check through or slots, unequip any that are "and", if no empty slots, choose a random one and replace
            val equips = getEquipment()
            equips.filter { item.equipmentSlots.contains(it.key) }.filter { it.value.equipmentAnd }
                    .keys.forEach { unequipItem(it) }
            freeSlots.addAll(item.equipmentSlots.filter { !equips.keys.contains(it) })
            if(freeSlots.size == 0) {
                val removed = item.equipmentSlots[inject<Random>().nextInt(item.equipmentSlots.size)]
                unequipItem(removed)
                freeSlots.add(removed)
            }
        }

        equipment.put(freeSlots[inject<Random>().nextInt(freeSlots.size)], index)
    }

    fun isEquipped(index: Int): Boolean {
        return equipment.values.contains(index)
    }

    fun getStatTotal(stat: Stat): Int {
        var value = getStat(stat)
        getEquipment().values.forEach { value += it.equipmentStats[stat] ?: 0 }

        return value
    }

    fun getResistanceTotal(type: Type): Int {
        var value = getResistance(type)
        getEquipment().values.forEach { value += it.equipmentResistance[type] ?: 0 }

        return value
    }

    fun getTotalCarryCapacity(): Int {
        return 6 + getStatTotal(Stat.CARRY) + (getStatTotal(Stat.STR) / 5)
    }

    override fun update(delta: Float) {
        if(inventory.capacity != getTotalCarryCapacity()) {
            inventory.capacity = getTotalCarryCapacity()
        }

        if(hpCurrent < 0) hpCurrent = 0
        if(hpCurrent > getStatTotal(Stat.HP)) hpCurrent = getStatTotal(Stat.HP)

        val direction = Vector2(prevX - body.position.x,prevY - body.position.y)
        if(Math.abs(direction.x) > Math.abs(direction.y)) {
            if(direction.x < 0) {
                currentAnimation = 0
            }
            else {
                currentAnimation = 3
            }
        }
        else if(Math.abs(direction.x) < Math.abs(direction.y)) {
            if(direction.y < 0) {
                currentAnimation = 2
            }
            else {
                currentAnimation = 1
            }
        }
        prevX = body.position.x
        prevY = body.position.y
    }

    override fun render(batch: SpriteBatch, delta: Float) {
        super.render(batch, delta)
        repeat(hpBarRegions[0].size, {
            batch.draw(hpBarRegions[0][it], body.position.x - size * 1.5f + (size*it),body.position.y - size * 2f,
                    size,size)
        })
        val hpPercent = hpCurrent.toFloat() / getStatTotal(Stat.HP).toFloat()
        val barChunks = 1 / hpBarRegions[1].size.toFloat()
        repeat(hpBarRegions[1].size, {
            if(hpPercent > barChunks * it) {
                val chunkPercent = if (hpPercent > barChunks * (it + 1)) 1f
                    else (hpPercent - barChunks * it) / barChunks
                val newRegion = TextureRegion(hpBarRegions[1][it], 0,0,(16 * chunkPercent).toInt(),
                        hpBarRegions[1][it].regionHeight)
                batch.draw(newRegion, body.position.x - size * 1.5f + (size * it), body.position.y - size * 2f,
                        chunkPercent*size, size)
            }
        })
    }

    override fun dispose() {
        super.dispose()
    }
}