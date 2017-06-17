package io.explod.organizer.service.repo

import io.explod.arch.data.Category
import io.explod.arch.data.CategoryDao
import io.explod.arch.data.Item
import meta.BaseRoboTest
import org.junit.Assert
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CategoryDaoTest : BaseRoboTest() {

    lateinit var categories: CategoryDao

    @Before
    fun setUp() {
        categories = db.categories()
    }

    @Test
    fun loadAllAsList() {
        // loadAllAsList should return all Categories
        // in createdDate-DESC order
        val all = offload {
            categories.insert(Category.new("cat1"))
            categories.insert(Category.new("cat2"))
            categories.insert(Category.new("cat3"))

            categories.loadAllAsList()
        }

        Assert.assertNotNull(all)
        Assert.assertEquals(3, all!!.size)
        Assert.assertEquals("cat3", all[0].name)
        Assert.assertEquals("cat2", all[1].name)
        Assert.assertEquals("cat1", all[2].name)
    }

    @Test
    fun loadAllWithStatsAsList() {
        // loadAllWithStatsAsList should return all Categories
        // in createdDate-DESC order with the correct stats
        val stats = offload {
            categories.insert(Category.new("cat0"))
            val cat1 = categories.insert(Category.new("cat1"))
            val cat2 = categories.insert(Category.new("cat2"))
            val cat3 = categories.insert(Category.new("cat3"))

            db.items().insert(Item.new(cat1, "item11", rating = -1))
            db.items().insert(Item.new(cat1, "item12", rating = 2))
            db.items().insert(Item.new(cat1, "item13", rating = 4))

            db.items().insert(Item.new(cat2, "item21", rating = 3))
            db.items().insert(Item.new(cat2, "item22", rating = -1))
            db.items().insert(Item.new(cat2, "item23", rating = 5))
            db.items().insert(Item.new(cat2, "item24", rating = -1))

            db.items().insert(Item.new(cat3, "item31", rating = 1))
            db.items().insert(Item.new(cat3, "item32", rating = 3))
            db.items().insert(Item.new(cat3, "item33", rating = 5))
            db.items().insert(Item.new(cat3, "item34", rating = 1))
            db.items().insert(Item.new(cat3, "item35", rating = 3))
            db.items().insert(Item.new(cat3, "item36", rating = 5))

            categories.loadAllWithStatsAsList()
        }

        Assert.assertNotNull(stats)
        Assert.assertEquals(4, stats!!.size)
        Assert.assertEquals("cat3", stats[0].category.name)
        Assert.assertEquals(6, stats[0].numRated)
        Assert.assertEquals(18, stats[0].totalRating)
        Assert.assertEquals(3.0f, stats[0].averageRating)
        Assert.assertEquals("cat2", stats[1].category.name)
        Assert.assertEquals(2, stats[1].numRated)
        Assert.assertEquals(8, stats[1].totalRating)
        Assert.assertEquals(4.0f, stats[1].averageRating)
        Assert.assertEquals("cat1", stats[2].category.name)
        Assert.assertEquals(2, stats[2].numRated)
        Assert.assertEquals(6, stats[2].totalRating)
        Assert.assertEquals(3.0f, stats[2].averageRating)
        Assert.assertEquals("cat0", stats[3].category.name)
        Assert.assertEquals(0, stats[3].numRated)
        Assert.assertEquals(0, stats[3].totalRating)
        Assert.assertEquals(0f, stats[3].averageRating)
    }

    @Test
    fun delete_shouldDeleteItems() {
        // delete should also delete any items assigned to this category
        offload {
            val cat1 = categories.insert(Category.new("cat1"))

            val item11 = db.items().insert(Item.new(cat1, "item11", rating = -1))
            val item12 = db.items().insert(Item.new(cat1, "item12", rating = 2))
            val item13 = db.items().insert(Item.new(cat1, "item13", rating = 4))

            categories.delete(cat1)

            assertNull(db.items().byId(item11))
            assertNull(db.items().byId(item12))
            assertNull(db.items().byId(item13))
        }
    }

}