package com.halemaster.tilltheend.map

import box2dLight.RayHandler
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.halemaster.tilltheend.entity.Entity
import com.halemaster.tilltheend.ui.GUI
import com.halemaster.tilltheend.ui.TextRenderer
import ktx.inject.inject

class WorldManager : ContactListener {
    internal var debug = false
    internal var accumulator: Float = 0f
    internal var entities = mutableMapOf<String, Entity>()
    internal var batch: SpriteBatch = SpriteBatch()
    internal var camera: OrthographicCamera = OrthographicCamera()
    internal var world: World = World(Vector2(0f, 0f), true)
    internal var map: Map = Map("tilemaps/test.tmx",camera,world)
    internal var rayHandler = RayHandler(world)
    internal var debugRenderer: Box2DDebugRenderer = Box2DDebugRenderer()
    internal var disposeEntities: MutableList<Entity> = mutableListOf()
    internal var disposeWorld: MutableList<World> = mutableListOf()
    internal var disposeMap: MutableList<Map> = mutableListOf()
    internal var disposeRay: MutableList<RayHandler> = mutableListOf()
    internal var hasLighting = false
    internal var uiCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    internal var gui = GUI()
    internal var itemIdCurrent = 0

    fun newWorld(mapName: String, width: Float, height: Float, hasLighting: Boolean) {
        disposeWorld.add(world)
        world = World(Vector2(0f, 0f), true)
        world.setContactListener(this)
        disposeRay.add(rayHandler)
        rayHandler = RayHandler(world)
        camera.setToOrtho(false, width, height)
        disposeMap.add(map)
        map = Map(mapName,camera,world)
        disposeEntities.addAll(entities.values)
        entities = mutableMapOf<String, Entity>()
        this.hasLighting = hasLighting
        itemIdCurrent = 0
    }

    fun render(delta: Float) {
        if(disposeEntities.isNotEmpty()) {
            disposeEntities.forEach(Entity::dispose)
            disposeEntities.clear()
        }
        if(disposeWorld.isNotEmpty()) {
            disposeWorld.forEach(World::dispose)
            disposeWorld.clear()
        }
        if(disposeMap.isNotEmpty()) {
            disposeMap.forEach(Map::dispose)
            disposeMap.clear()
        }
        if(disposeRay.isNotEmpty()) {
            disposeRay.forEach(RayHandler::dispose)
            disposeRay.clear()
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F12)) {
            debug = !debug
        }
        batch.projectionMatrix = camera.combined
        map.render()
        batch.begin()
        entities.values.sortedByDescending(Entity::depth).forEach { entity -> entity.render(batch, delta) }
        batch.end()
        if(hasLighting) {
            rayHandler.setCombinedMatrix(camera)
            rayHandler.updateAndRender()
        }
        gui.render(delta, batch, uiCamera)
        if(debug) {
            debugRenderer.render(world, camera.combined)
            val fps = Gdx.graphics.framesPerSecond
            batch.projectionMatrix = uiCamera.combined
            batch.begin()
            inject<TextRenderer>().render("$fps fps",batch,-uiCamera.viewportWidth/2f, uiCamera.viewportHeight/2.25f,
                    Color.WHITE,1f)
            batch.end()
        }
        doPhysicsStep(delta)
    }

    fun doPhysicsStep(deltaTime: Float) {
        val frameTime = Math.min(deltaTime, 0.25f)
        val timeStep = 1/45f
        val velocityIterations = 6
        val positionIterations = 2
        accumulator += frameTime
        while (accumulator >= timeStep) {
            world.step(timeStep, velocityIterations, positionIterations)
            accumulator -= timeStep
        }
    }

    fun addEntity(entity: Entity) {
        entities.put(entity.id, entity)
    }

    fun getEntity(name: String): Entity? {
        return entities[name]
    }

    fun removeEntity(name: String): Entity? {
        if(getEntity(name) != null) disposeEntities.add(getEntity(name)!!)
        return entities.remove(name)
    }

    fun dispose() {
        batch.dispose()
        map.dispose()
        entities.values.forEach(Entity::dispose)
        world.dispose()
        debugRenderer.dispose()
    }

    override fun endContact(contact: Contact?) {

    }

    override fun beginContact(contact: Contact?) {

    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
        entities.values.filter { entity -> entity == contact?.fixtureA?.userData ||
                entity == contact?.fixtureB?.userData }
                .forEach { entity -> entity.collides(entity, contact) }
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {

    }
}