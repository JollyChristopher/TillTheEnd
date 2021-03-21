package com.halemaster.tilltheend.map

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.halemaster.tilltheend.entity.BASIC
import ktx.assets.Assets

class Map(levelName: String, camera: OrthographicCamera, world: World) {
    internal var renderer: OrthogonalTiledMapRenderer
    internal val shapes: MutableList<Shape> = mutableListOf()

    init {
        val assetManager = Assets.manager
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))
        assetManager.load(levelName, TiledMap::class.java)
        assetManager.finishLoading()

        val map: TiledMap = assetManager.get(levelName)
        val unitScale: Float = 1 / 16f
        renderer = OrthogonalTiledMapRenderer(map, unitScale)
        renderer.setView(camera)

        var tiledLayer: TiledMapTileLayer = map.layers[1] as TiledMapTileLayer
        repeat(tiledLayer.width, {x ->
            repeat(tiledLayer.height, {y ->
                val cell = tiledLayer.getCell(x, y)
                if(cell != null) {
                    val groundBodyDef = BodyDef()
                    groundBodyDef.type = BodyDef.BodyType.StaticBody
                    groundBodyDef.position.set(x*1f+0.5f, y*1f+0.73f)
                    val groundBody = world.createBody(groundBodyDef)
                    val groundBox = PolygonShape()
                    groundBox.setAsBox(0.45f, 0.33f)
                    groundBody.createFixture(groundBox, 0.0f)
                    shapes.add(groundBox)
                }
            })
        })
        tiledLayer = map.layers[2] as TiledMapTileLayer
        repeat(tiledLayer.width, {x ->
            repeat(tiledLayer.height, {y ->
                val cell = tiledLayer.getCell(x, y)
                if(cell != null) {
                    val groundBodyDef = BodyDef()
                    groundBodyDef.type = BodyDef.BodyType.StaticBody
                    groundBodyDef.position.set(x*1f+0.5f, y*1f+0.5f)
                    val groundBody = world.createBody(groundBodyDef)
                    val groundBox = PolygonShape()
                    groundBox.setAsBox(0.45f, 0.5f)
                    groundBody.createFixture(groundBox, 0.0f)
                    shapes.add(groundBox)
                }
            })
        })
        tiledLayer = map.layers[3] as TiledMapTileLayer
        repeat(tiledLayer.width, {x ->
            repeat(tiledLayer.height, {y ->
                val cell = tiledLayer.getCell(x, y)
                if(cell != null) {
                    val groundBodyDef = BodyDef()
                    groundBodyDef.type = BodyDef.BodyType.StaticBody
                    groundBodyDef.position.set(x*1f+0.5f, y*1f+0.73f)
                    val groundBody = world.createBody(groundBodyDef)
                    val groundBox = PolygonShape()
                    groundBox.setAsBox(0.45f, 0.33f)
                    val fixture = groundBody.createFixture(groundBox, 0.0f)
                    val filter = Filter()
                    filter.categoryBits = BASIC
                    fixture.filterData = filter
                    shapes.add(groundBox)
                }
            })
        })
        val edges = List(4,{index -> // 0,0=0,32 & 0,0=32,0 & 0,32=32,32 & 32,0=32,32
            val vec1 = Vector2(32.4f * (index / 2)-0.2f,32.4f * (index / 2)-0.2f)
            val vec2 = Vector2(32.4f * (index % 2)-0.2f,32.4f * ((index + 1) % 2)-0.2f)
            Pair(vec1,vec2)
        })
        for (edge in edges) {
            val wallBodyDef = BodyDef()
            wallBodyDef.type = BodyDef.BodyType.StaticBody
            wallBodyDef.position.set(0f, 0f)
            val wallBody = world.createBody(wallBodyDef)
            val wallBox = EdgeShape()
            wallBox.set(edge.first, edge.second)
            wallBody.createFixture(wallBox, 0.0f)
            shapes.add(wallBox)
        }
    }

    fun render() {
        renderer.render()
    }

    fun dispose() {
        renderer.dispose()
        shapes.forEach(Shape::dispose)
    }
}
