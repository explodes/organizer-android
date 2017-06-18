package io.explod.organizer.service.repo

import io.explod.arch.data.Category
import io.explod.arch.data.CategoryDao
import io.explod.arch.data.Item
import meta.BaseRoboTest
import meta.first
import org.junit.Assert
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class CategoryDaoTest : BaseRoboTest() {

    lateinit var categories: CategoryDao

    @Before
    fun setUp() {
        categories = db.categories()
    }

    @Test
    fun loadAllStatsDirect() {
        // loadAllStatsDirect should return all Categories
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

            categories.loadAllStatsDirect()
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

    @Ignore("(evan) live data problems")
    @Test
    fun delete_shouldDeleteItems() {
        // delete should also delete any items assigned to this category
        offload {
            val cat1 = categories.insert(Category.new("cat1"))
            val stats = db.categories().statsByIdDirect(cat1)

            val item11 = db.items().insert(Item.new(cat1, "item11", rating = -1))
            val item12 = db.items().insert(Item.new(cat1, "item12", rating = 2))
            val item13 = db.items().insert(Item.new(cat1, "item13", rating = 4))

            categories.delete(stats.category)

            offload {
                assertNull(db.items().byId(item11).first())
            }
            offload {
                assertNull(db.items().byId(item12).first())
            }
            offload {
                assertNull(db.items().byId(item13).first())
            }
        }
    }

    @Test
    fun statsByIdDirect() {
        // statsByIdDirect should load a category with stats
        val stats = offload {
            categories.insert(Category.new("fuzz1"))
            val cat1 = categories.insert(Category.new("cat1"))
            val fuzz2 = categories.insert(Category.new("fuzz2"))

            db.items().insert(Item.new(cat1, "item11", rating = -1))
            db.items().insert(Item.new(cat1, "item12", rating = 2))
            db.items().insert(Item.new(cat1, "item13", rating = 4))

            db.items().insert(Item.new(fuzz2, "item21", rating = 3))
            db.items().insert(Item.new(fuzz2, "item22", rating = 5))
            db.items().insert(Item.new(fuzz2, "item23", rating = 2))

            categories.statsByIdDirect(cat1)
        }

        Assert.assertNotNull(stats)
        Assert.assertEquals("cat1", stats!!.category.name)
        Assert.assertEquals(2, stats.numRated)
        Assert.assertEquals(6, stats.totalRating)
        Assert.assertEquals(3.0f, stats.averageRating)
    }

    @Test
    fun statsByIdDirect_withoutItems() {
        // statsByIdDirect should load a category with stats even when it has no items
        val stats = offload {
            val cat1 = categories.insert(Category.new("cat1"))
            val fuzz1 = categories.insert(Category.new("fuzz1"))
            val fuzz2 = categories.insert(Category.new("fuzz2"))

            db.items().insert(Item.new(fuzz1, "item11", rating = -1))
            db.items().insert(Item.new(fuzz1, "item12", rating = 2))
            db.items().insert(Item.new(fuzz1, "item13", rating = 4))

            db.items().insert(Item.new(fuzz2, "item21", rating = 3))
            db.items().insert(Item.new(fuzz2, "item22", rating = 5))
            db.items().insert(Item.new(fuzz2, "item23", rating = 2))


            categories.statsByIdDirect(cat1)
        }

        Assert.assertNotNull(stats)
        Assert.assertEquals("cat1", stats!!.category.name)
        Assert.assertEquals(0, stats.numRated)
        Assert.assertEquals(0, stats.totalRating)
        Assert.assertEquals(0f, stats.averageRating)
    }

}