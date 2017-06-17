package io.explod.organizer.service.repo

import io.explod.arch.data.Category
import io.explod.arch.data.Item
import meta.BaseRoboTest
import meta.first
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class AppRepoImplTest : BaseRoboTest() {

    lateinit var repo: AppRepoImpl

    @Before
    fun setUp() {
        repo = AppRepoImpl()
    }

    @Ignore("(evan): LiveData isn't giving us a 'first' value")
    @Test
    fun getAllCategories() {
        // getAllCategories should return all Categories
        // in createdDate-DESC order
        offloadWork {
            repo.db.categories().insert(Category.new("cat1"))
            repo.db.categories().insert(Category.new("cat2"))
            repo.db.categories().insert(Category.new("cat3"))
        }

        val categories = repo.getAllCategories().first { it != null }

        assertNotNull(categories)
        assertEquals(3, categories!!.size)
        assertEquals("cat3", categories[0].name)
        assertEquals("cat2", categories[1].name)
        assertEquals("cat1", categories[2].name)
    }

    @Test
    fun getCategoryById_exists() {
        // getCategoryById should give us a Category if it exists
        val existing = offload {
            val id = repo.db.categories().insert(Category.new("exists"))
            repo.getCategoryById(id)
        }

        assertNotNull(existing)
    }

    @Test
    fun getCategoryById_doesntExist() {
        // getCategoryById should not give us back a Category
        // if it doesnt exist
        val doesNotExist = offload { repo.getCategoryById(Long.MIN_VALUE) }

        assertNull(doesNotExist)
    }

    @Test
    fun createCategory() {
        // createCategory should create a Category and
        // have an assigned ID
        val category = offload { repo.createCategory("cat1") }!!

        assertNotEquals(0, category.id)

        val existing = offload { repo.getCategoryById(category.id) }

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
            val id = new.id
            repo.deleteCategory(id)
            repo.getCategoryById(id)
        }

        assertNull(category)
    }


    @Ignore("(evan): LiveData isn't giving us a 'first' value")
    @Test
    fun getAllItemsForCategory() {
        // getAllItemsForCategory should return all Items that
        // belong to a certain Category
        val categoryId = offload {
            val categoryId1 = repo.db.categories().insert(Category.new("cat1"))
            if (categoryId1 <= 0) fail("Unable to insert category")
            repo.db.items().insert(Item.new(categoryId1, "item11"))
            repo.db.items().insert(Item.new(categoryId1, "item12"))

            val categoryId2 = repo.db.categories().insert(Category.new("cat2"))
            if (categoryId2 <= 0) fail("Unable to insert category")
            repo.db.items().insert(Item.new(categoryId2, "item21"))
            repo.db.items().insert(Item.new(categoryId2, "item22"))
            repo.db.items().insert(Item.new(categoryId2, "item23"))

            categoryId2
        }!!

        val items = repo.getAllItemsForCategory(categoryId).first { it != null }

        assertNotNull(items)
        assertEquals(3, items!!.size)
        assertEquals("item23", items[0].name)
        assertEquals("item22", items[1].name)
        assertEquals("item21", items[1].name)
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
        val doesNotExist = offload { repo.getItemById(Long.MIN_VALUE) }

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
            val id = new.id
            repo.deleteItem(id)
            repo.getItemById(new.id)
        }

        assertNull(item)
    }

}