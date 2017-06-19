package io.explod.organizer.service.repo

import io.explod.organizer.service.database.Category
import io.explod.organizer.service.database.Item
import meta.BaseRoboTest
import meta.getOrNull
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AppRepoImplTest : BaseRoboTest() {

    lateinit var repo: AppRepoImpl

    @Before
    fun setUp() {
        repo = AppRepoImpl()
    }

    @Test
    fun getAllCategoriesWithStats() {
        // getAllCategoryStats should return all Categories
        // in createdDate-DESC order
        val categories = offload {
            val cat1 = repo.db.categories().insert(Category.new("cat1"))
            val cat2 = repo.db.categories().insert(Category.new("cat2"))
            val cat3 = repo.db.categories().insert(Category.new("cat3"))

            repo.db.items().insert(Item.new(cat1, "item11", rating = -1))
            repo.db.items().insert(Item.new(cat1, "item12", rating = 2))
            repo.db.items().insert(Item.new(cat1, "item13", rating = 4))

            repo.db.items().insert(Item.new(cat2, "item21", rating = 3))
            repo.db.items().insert(Item.new(cat2, "item22", rating = -1))
            repo.db.items().insert(Item.new(cat2, "item23", rating = 5))
            repo.db.items().insert(Item.new(cat2, "item24", rating = -1))

            repo.db.items().insert(Item.new(cat3, "item31", rating = 1))
            repo.db.items().insert(Item.new(cat3, "item32", rating = 3))
            repo.db.items().insert(Item.new(cat3, "item33", rating = 5))
            repo.db.items().insert(Item.new(cat3, "item34", rating = 1))
            repo.db.items().insert(Item.new(cat3, "item35", rating = 3))
            repo.db.items().insert(Item.new(cat3, "item36", rating = 5))

            repo.getAllCategoryStats().blockingFirst()

        }

        assertNotNull(categories)
        assertEquals(3, categories!!.size)
        assertEquals("cat3", categories[0].category.name)
        assertEquals(6, categories[0].numRated)
        assertEquals(18, categories[0].totalRating)
        assertEquals(3.0f, categories[0].averageRating)
        assertEquals("cat2", categories[1].category.name)
        assertEquals(2, categories[1].numRated)
        assertEquals(8, categories[1].totalRating)
        assertEquals(4.0f, categories[1].averageRating)
        assertEquals("cat1", categories[2].category.name)
        assertEquals(2, categories[2].numRated)
        assertEquals(6, categories[2].totalRating)
        assertEquals(3.0f, categories[2].averageRating)
    }

    @Test
    fun getCategoryById_exists() {
        // getCategoryStatsById should give us a Category if it exists
        val existing = offload {
            val id = repo.db.categories().insert(Category.new("exists"))
            repo.getCategoryStatsById(id)
        }

        assertNotNull(existing)
    }

    @Test
    fun getCategoryById_doesntExist() {
        // getCategoryStatsById should not give us back a Category
        // if it doesnt exist
        val doesNotExist = offload {
            repo.getCategoryStatsById(Long.MIN_VALUE).blockingFirst().getOrNull()
        }

        assertNull(doesNotExist)
    }

    @Test
    fun createCategory() {
        // createCategory should create a Category and
        // have an assigned ID
        val category = offload {
            repo.createCategory("cat1")
        }

        assertNotNull(category)
        assertNotEquals(0, category!!.id)

        val existing = offload {
            repo.getCategoryStatsById(category.id).blockingFirst().getOrNull()
        }

        assertNotNull(existing)
    }

    @Test
    fun updateCategory() {
        // updateCategory should persist changes to a Category
        val category = offload {
            val new = repo.createCategory("cat1")
            new.name = "cat2"
            repo.updateCategory(new)
            new
        }

        assertNotNull(category)
        assertEquals("cat2", category!!.name)
    }

    @Test
    fun deleteCategory() {
        // deleteCategory should remove a Category from the database
        val category = offload {
            val new = repo.createCategory("cat1")
            val stats = repo.getCategoryStatsById(new.id).blockingFirst().get()
            repo.deleteCategory(stats.category)
            repo.getCategoryStatsById(new.id).blockingFirst().getOrNull()
        }

        assertNull(category)
    }


    @Test
    fun getAllItemsForCategory() {
        // getAllItemsForCategory should return all Items that
        // belong to a certain Category
        val items = offload {
            val categoryId1 = repo.db.categories().insert(Category.new("cat1"))
            if (categoryId1 <= 0) fail("Unable to insert category")
            repo.db.items().insert(Item.new(categoryId1, "item11"))
            repo.db.items().insert(Item.new(categoryId1, "item12"))

            val categoryId2 = repo.db.categories().insert(Category.new("cat2"))
            if (categoryId2 <= 0) fail("Unable to insert category")
            repo.db.items().insert(Item.new(categoryId2, "item21"))
            repo.db.items().insert(Item.new(categoryId2, "item22"))
            repo.db.items().insert(Item.new(categoryId2, "item23"))

            repo.getAllItemsForCategory(categoryId2).blockingFirst()
        }

        assertNotNull(items)
        assertEquals(3, items!!.size)
        assertEquals("item23", items[0].name)
        assertEquals("item22", items[1].name)
        assertEquals("item21", items[2].name)
    }

    @Test
    fun getItemById_exists() {
        // getItemById should return an Item if it exists
        val exists = offload {
            val catId = repo.db.categories().insert(Category.new("exists"))

            val id = repo.db.items().insert(Item.new(catId, "exists"))
            repo.getItemById(id)
        }

        assertNotNull(exists)
    }

    @Test
    fun getItemById_doesntExist() {
        // getItemById should return null if it does not exist
        val doesNotExist = offload {
            repo.getItemById(Long.MIN_VALUE).blockingFirst().getOrNull()
        }

        assertNull(doesNotExist)
    }

    @Test
    fun createItem() {
        // createItem should persist an item in the database
        // and should set the ID of the input Item
        val item = offload {
            val categoryId = repo.db.categories().insert(Category.new("cat1"))
            if (categoryId <= 0) fail("Unable to insert category")

            repo.createItem(categoryId, "item1")
        }

        assertNotNull(item)
        assertNotEquals(0, item!!.id)

        val existing = offload {
            repo.getItemById(item.id)
        }

        assertNotNull(existing)
    }

    @Test
    fun updateItem() {
        // updateItem should persist changes to an Item
        val item = offload {
            val categoryId = repo.db.categories().insert(Category.new("cat1"))
            if (categoryId <= 0) fail("Unable to insert category")

            val new = repo.createItem(categoryId, "item1")
            new.name = "item2"
            repo.updateItem(new)
            new
        }

        assertNotNull(item)
        assertEquals("item2", item!!.name)
    }

    @Test
    fun deleteItem() {
        // deleteItem should remove an Item from the database
        val item = offload {
            val categoryId = repo.db.categories().insert(Category.new("cat1"))
            if (categoryId <= 0) fail("Unable to insert category")

            val new = repo.createItem(categoryId, "item1")
            val item = repo.getItemById(new.id).blockingFirst().get()
            repo.deleteItem(item)
            repo.getItemById(new.id).blockingFirst().getOrNull()
        }

        assertNull(item)
    }

}