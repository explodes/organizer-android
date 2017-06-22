package io.explod.organizer.service.repo

import meta.BaseRoboTest
import meta.await
import meta.getOrNull
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AsyncAppRepoTest : BaseRoboTest() {

    lateinit var repo: AsyncAppRepo

    @Before
    fun setUp() {
        repo = AsyncAppRepo()
    }

    @Test
    fun createCategory() {
        // createCategory should wrap AppRepo.createCategory
        // as a Single<Category>
        val cat = await {
            val cat1 = repo.createCategory("cat1").blockingGet()
            repo.synchronously.getCategoryStatsById(cat1.id).blockingFirst().getOrNull()
        }

        assertNotNull(cat)
        assertEquals("cat1", cat!!.category.name)
    }

    @Test
    fun updateCategory() {
        // updateCategory should wrap AppRepo.updateCategory
        // as a Completable
        val cat = await {
            val cat1 = repo.synchronously.createCategory("cat1")

            cat1.name = "cat2"
            repo.updateCategory(cat1).blockingAwait()

            repo.synchronously.getCategoryStatsById(cat1.id).blockingFirst().getOrNull()
        }

        assertNotNull(cat)
        assertEquals("cat2", cat!!.category.name)
    }

    @Test
    fun deleteCategory() {
        // deleteCategory should wrap AppRepo.deleteCategory
        // as a Completable
        val cat = await {
            val cat1 = repo.synchronously.createCategory("cat1")

            repo.deleteCategory(cat1).blockingAwait()

            repo.synchronously.getCategoryStatsById(cat1.id).blockingFirst().getOrNull()
        }

        assertNull(cat)
    }

    @Test
    fun getItemById() {
        // getItemById should wrap AppRepo.getItemById
        // as a Single<Optional<Category>>
        val item = await {
            val cat1 = repo.synchronously.createCategory("cat1")
            val item1 = repo.synchronously.createItem(cat1.id, "item1")

            repo.getItemById(item1.id).blockingFirst().getOrNull()
        }

        assertNotNull(item)
    }

    @Test
    fun createItem() {
        // createCategory should wrap AppRepo.createCategory
        // as a Single<Category>
        val item = await {
            val cat1 = repo.createCategory("cat1").blockingGet()

            val item1 = repo.createItem(cat1.id, "item1").blockingGet()

            repo.synchronously.getItemById(item1.id).blockingFirst().getOrNull()
        }

        assertNotNull(item)
        assertEquals("item1", item!!.name)
    }

    @Test
    fun updateItem() {
        // updateCategory should wrap AppRepo.updateCategory
        // as a Completable
        val item = await {
            val cat1 = repo.synchronously.createCategory("cat1")
            val item1 = repo.synchronously.createItem(cat1.id, "item1")

            item1.name = "item2"
            repo.updateItem(item1).blockingAwait()

            repo.synchronously.getItemById(item1.id).blockingFirst().getOrNull()
        }

        assertNotNull(item)
        assertEquals("item2", item!!.name)
    }

    @Test
    fun deleteItem() {
        // deleteCategory should wrap AppRepo.deleteCategory
        // as a Completable
        val item = await {
            val cat1 = repo.synchronously.createCategory("cat1")
            val item1 = repo.synchronously.createItem(cat1.id, "item1")

            repo.deleteItem(item1).blockingAwait()

            repo.synchronously.getItemById(item1.id).blockingFirst().getOrNull()
        }

        assertNull(item)
    }

}