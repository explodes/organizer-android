package io.explod.organizer.service.repo

import meta.BaseRoboTest
import meta.first
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class AsyncAppRepoTest : BaseRoboTest() {

    lateinit var repo: AsyncAppRepo

    @Before
    fun setUp() {
        repo = AsyncAppRepo()
    }

    @Ignore("(evan) live data problems")
    @Test
    fun createCategory() {
        // createCategory should wrap AppRepo.createCategory
        // as a Single<Category>
        val data = offload {
            val cat1 = repo.createCategory("cat1").blockingGet()
            repo.synchronously.getCategoryStatsById(cat1.id)
        }!!

        val cat = data.first { it != null }

        assertNotNull(cat)
        assertEquals("cat1", cat!!.category.name)
    }

    @Ignore("(evan) live data problems")
    @Test
    fun updateCategory() {
        // updateCategory should wrap AppRepo.updateCategory
        // as a Completable
        val data = offload {
            val cat1 = repo.synchronously.createCategory("cat1")

            cat1.name = "cat2"
            repo.updateCategory(cat1).blockingAwait()

            repo.synchronously.getCategoryStatsById(cat1.id)
        }!!

        val cat = data.first { it != null }

        assertNotNull(cat)
        assertEquals("cat2", cat!!.category.name)
    }

    @Ignore("(evan) live data problems")
    @Test
    fun deleteCategory() {
        // deleteCategory should wrap AppRepo.deleteCategory
        // as a Completable
        val data = offload {
            val cat1 = repo.synchronously.createCategory("cat1")

            repo.deleteCategory(cat1).blockingAwait()

            repo.synchronously.getCategoryStatsById(cat1.id)
        }

        val cat = data!!.first()

        assertNull(cat)
    }

    @Ignore("(evan) live data problems")
    @Test
    fun getItemById() {
        // getItemById should wrap AppRepo.getItemById
        // as a Single<Optional<Category>>
        val data = offload {
            val cat1 = repo.synchronously.createCategory("cat1")
            val item1 = repo.synchronously.createItem(cat1.id, "item1")

            repo.getItemById(item1.id)
        }

        val item = data!!.first { it != null }

        assertNotNull(item)
    }

    @Ignore("(evan) live data problems")
    @Test
    fun createItem() {
        // createCategory should wrap AppRepo.createCategory
        // as a Single<Category>
        val data = offload {
            val cat1 = repo.createCategory("cat1").blockingGet()

            val item1 = repo.createItem(cat1.id, "item1").blockingGet()

            repo.synchronously.getItemById(item1.id)
        }

        val item = data!!.first { it != null }

        assertNotNull(item)
        assertEquals("item1", item!!.name)
    }

    @Ignore("(evan) live data problems")
    @Test
    fun updateItem() {
        // updateCategory should wrap AppRepo.updateCategory
        // as a Completable
        val data = offload {
            val cat1 = repo.synchronously.createCategory("cat1")
            val item1 = repo.synchronously.createItem(cat1.id, "item1")

            item1.name = "item2"
            repo.updateItem(item1).blockingAwait()

            repo.synchronously.getItemById(item1.id)
        }

        val item = data!!.first { it != null }

        assertNotNull(item)
        assertEquals("item2", item!!.name)
    }

    @Ignore("(evan) live data problems")
    @Test
    fun deleteItem() {
        // deleteCategory should wrap AppRepo.deleteCategory
        // as a Completable
        val data = offload {
            val cat1 = repo.synchronously.createCategory("cat1")
            val item1 = repo.synchronously.createItem(cat1.id, "item1")

            repo.deleteItem(item1).blockingAwait()

            repo.synchronously.getItemById(item1.id)
        }

        val item = data!!.first { it != null }

        assertNull(item)
    }

}