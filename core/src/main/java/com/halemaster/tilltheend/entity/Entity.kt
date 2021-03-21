package com.halemaster.tilltheend.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.*
import com.halemaster.tilltheend.map.WorldManager
import ktx.inject.inject

const val WALL: Short = 1
const val BASIC: Short = 2
const val PLAYER: Short = 4
const val ENEMY: Short = 8
const val ITEM: Short = 16

open class Entity(internal var id: String, spriteSheet: Texture, spriteArray: List<List<Pair<Int, Int>>>,
                  x: Float, y: Float, isStatic: Boolean,
                  internal var speed: Float,
                  internal var size: Float,
                  internal var depth: Float,
                  internal var collides: (Entity, Contact?) -> Unit,
                  type: Short = WALL, mask: Short = 0xFFF) {
    internal var worldManager = inject<WorldManager>()
    internal var regions: List<List<TextureRegion>>
    internal var animationFrame: Float = 0f
    internal var currentAnimation: Int = 0
    internal var texture: Texture = spriteSheet
    internal val bodyDef: BodyDef
    internal val circle: CircleShape
    internal val body: Body
    internal val fixtureDef: FixtureDef
    internal val fixture: Fixture

    init {
        regions = List(spriteArray.size, { anim ->
            List(spriteArray[anim].size, { frame ->
                TextureRegion(texture, spriteArray[anim][frame].first*16, spriteArray[anim][frame].second*16, 16, 16)
            })
        })

        bodyDef = BodyDef()
        if(!isStatic) bodyDef.type = BodyDef.BodyType.DynamicBody
        else bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.position.set(x, y)
        body = worldManager.world.createBody(bodyDef)
        body.linearDamping = 0.9f
        circle = CircleShape()
        circle.radius = size
        fixtureDef = FixtureDef()
        fixtureDef.shape = circle
        if(!isStatic) fixtureDef.density = 0.9f
        else fixtureDef.density = 0f
        fixtureDef.friction = 0.5f
        fixtureDef.restitution = 0.2f
        fixture = body.createFixture(fixtureDef)
        fixture.userData = this
        val filter = Filter()
        filter.categoryBits = type
        filter.maskBits = mask
        fixture.filterData = filter
    }

    open fun render(batch: SpriteBatch, delta: Float) {
        update(delta)
        if(regions.isNotEmpty()) {
            animationFrame += delta * speed
            if (animationFrame >= regions[currentAnimation].size) animationFrame = 0f
            batch.draw(regions[currentAnimation][Math.floor(animationFrame.toDouble()).toInt()], body.position.x-size,
                    body.position.y-size,size*2f,size*2f)
        }
    }

    open fun update(delta: Float) {

    }

    fun changeBackingAnimation(spriteSheet: String, spriteArray: List<List<Pair<Int, Int>>>) {
        currentAnimation = 0
        animationFrame = 0f
        texture = Texture(Gdx.files.internal(spriteSheet))
        regions = List(spriteArray.size, { anim ->
            List(spriteArray[anim].size, { frame ->
                TextureRegion(texture, spriteArray[anim][frame].first*16, spriteArray[anim][frame].second*16, 16, 16)
            })
        })
    }

    open fun dispose() {
        circle.dispose()
        body.world.destroyBody(body)
    }
}
