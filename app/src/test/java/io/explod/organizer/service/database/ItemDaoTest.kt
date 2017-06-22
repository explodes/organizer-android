package io.explod.organizer.service.database

import io.explod.arch.data.ItemDao
import io.explod.organizer.service.database.Category
import io.explod.organizer.service.database.Item
import meta.BaseRoboTest
import meta.await
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ItemDaoTest : BaseRoboTest() {

    lateinit var items: ItemDao

    @Before
    fun setUp() {
        items = db.items()
    }

    @Test
    fun byCategory() {
        // byCategoryAsList should return all Items that
        // belong to a certain Category in createdDate-DESC order
        val all = await {
            val categoryId1 = db.categories().insert(Category.Companion.new("cat1"))
            if (categoryId1 <= 0) Assert.fail("Unable to insert category")
            items.insert(Item.Companion.new(categoryId1, "item11"))
            items.insert(Item.Companion.new(categoryId1, "item12"))

            val categoryId2 = db.categories().insert(Category.Companion.new("cat2"))
            if (categoryId2 <= 0) Assert.fail("Unable to insert category")
            items.insert(Item.Companion.new(categoryId2, "item21"))
            items.insert(Item.Companion.new(categoryId2, "item22"))
            items.insert(Item.Companion.new(categoryId2, "item23"))

            items.byCategory(categoryId2).blockingFirst()
        }

        Assert.assertNotNull(all)
        Assert.assertEquals(3, all!!.size)
        Assert.assertEquals("item23", all[0].name)
        Assert.assertEquals("item22", all[1].name)
        Assert.assertEquals("item21", all[2].name)
    }

}