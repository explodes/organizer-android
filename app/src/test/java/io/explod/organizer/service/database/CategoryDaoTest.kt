package io.explod.organizer.service.database

import org.junit.Assert.assertNull

class CategoryDaoTest : meta.BaseRoboTest() {

    lateinit var categories: io.explod.arch.data.CategoryDao

    @org.junit.Before
    fun setUp() {
        categories = db.categories()
    }


    @org.junit.Test
    fun delete_shouldDeleteItems() {
        // delete should also delete any items assigned to this category
        meta.BaseRoboTest.Companion.offload {
            val cat1 = categories.insert(Category.new("cat1"))
            val stats = db.categories().statsById(cat1).blockingFirst().first()

            val item11 = db.items().insert(Item.new(cat1, "item11", rating = -1))
            val item12 = db.items().insert(Item.new(cat1, "item12", rating = 2))
            val item13 = db.items().insert(Item.new(cat1, "item13", rating = 4))

            categories.delete(stats.category)

            assertNull(db.items().byId(item11).blockingFirst().firstOrNull())
            assertNull(db.items().byId(item12).blockingFirst().firstOrNull())
            assertNull(db.items().byId(item13).blockingFirst().firstOrNull())
        }
    }

    @org.junit.Test
    fun statsById() {
        // statsByIdDirect should load a category with stats
        val stats = meta.BaseRoboTest.Companion.offload {
            categories.insert(Category.new("fuzz1"))
            val cat1 = categories.insert(Category.new("cat1"))
            val fuzz2 = categories.insert(Category.new("fuzz2"))

            db.items().insert(Item.new(cat1, "item11", rating = -1))
            db.items().insert(Item.new(cat1, "item12", rating = 2))
            db.items().insert(Item.new(cat1, "item13", rating = 4))

            db.items().insert(Item.new(fuzz2, "item21", rating = 3))
            db.items().insert(Item.new(fuzz2, "item22", rating = 5))
            db.items().insert(Item.new(fuzz2, "item23", rating = 2))

            categories.statsById(cat1).blockingFirst().first()
        }

        org.junit.Assert.assertNotNull(stats)
        org.junit.Assert.assertEquals("cat1", stats!!.category.name)
        org.junit.Assert.assertEquals(2, stats.numRated)
        org.junit.Assert.assertEquals(6, stats.totalRating)
        org.junit.Assert.assertEquals(3.0f, stats.averageRating)
    }

    @org.junit.Test
    fun statsById_withoutItems() {
        // statsByIdDirect should load a category with stats even when it has no items
        val stats = meta.BaseRoboTest.Companion.offload {
            val cat1 = categories.insert(Category.new("cat1"))
            val fuzz1 = categories.insert(Category.new("fuzz1"))
            val fuzz2 = categories.insert(Category.new("fuzz2"))

            db.items().insert(Item.new(fuzz1, "item11", rating = -1))
            db.items().insert(Item.new(fuzz1, "item12", rating = 2))
            db.items().insert(Item.new(fuzz1, "item13", rating = 4))

            db.items().insert(Item.new(fuzz2, "item21", rating = 3))
            db.items().insert(Item.new(fuzz2, "item22", rating = 5))
            db.items().insert(Item.new(fuzz2, "item23", rating = 2))


            categories.statsById(cat1).blockingFirst().first()
        }

        org.junit.Assert.assertNotNull(stats)
        org.junit.Assert.assertEquals("cat1", stats!!.category.name)
        org.junit.Assert.assertEquals(0, stats.numRated)
        org.junit.Assert.assertEquals(0, stats.totalRating)
        org.junit.Assert.assertEquals(0f, stats.averageRating)
    }

}