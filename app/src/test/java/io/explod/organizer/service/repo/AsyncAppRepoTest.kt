package io.explod.organizer.service.repo

import meta.BaseRoboTest
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
    fun getCategoryById() {
        // getCategoryById should wrap AppRepo.getCategoryById
        // as a Single<Optional<Category>>
        val cat = offload {
            val cat1 = repo.synchronously.createCategory("cat1")
            repo.getCategoryById(cat1.id).blockingGet().get()
        }

        assertNotNull(cat)
    }

    @Test
    fun createCategory() {
        // createCategory should wrap AppRepo.createCategory
        // as a Single<Category>
        val cat = offload {
            val cat1 = repo.createCategory("cat1").blockingGet()
            repo.synchronously.getCategoryById(cat1.id)
        }

        assertNotNull(cat)
       assertEquals("cat1", cat!!.name)
    }

    @Test
    fun updateCategory() {
        // updateCategory should wrap AppRepo.updateCategory
        // as a Completable
        val cat = offload {
            val cat1 = repo.synchronously.createCategory("cat1")

            cat1.name = "cat2"
            repo.updateCategory(cat1).blockingAwait()

            repo.synchronously.getCategoryById(cat1.id)
        }

        assertNotNull(cat)
       assertEquals("cat2", cat!!.name)
    }

    @Test
    fun deleteCategory() {
        // deleteCategory should wrap AppRepo.deleteCategory
        // as a Completable
        val cat = offload {
            val cat1 = repo.synchronously.createCategory("cat1")

            repo.deleteCategory(cat1.id).blockingAwait()

            repo.synchronously.getCategoryById(cat1.id)
        }

        assertNull(cat)
    }

    @Test
    fun getItemById() {
        // getItemById should wrap AppRepo.getItemById
        // as a Single<Optional<Category>>
        val item = offload {
            val cat1 = repo.synchronously.createCategory("cat1")
            val item1 = repo.synchronously.createItem(cat1.id, "item1")

            repo.getItemById(item1.id).blockingGet().get()
        }

        assertNotNull(item)
    }

    @Test
    fun createItem() {
        // createCategory should wrap AppRepo.createCategory
        // as a Single<Category>
        val item = offload {
            val cat1 = repo.createCategory("cat1").blockingGet()

            val item1 = repo.createItem(cat1.id, "item1").blockingGet()

            repo.synchronously.getItemById(item1.id)
        }

        assertNotNull(item)
       assertEquals("item1", item!!.name)
    }

    @Test
    fun updateItem() {
        // updateCategory should wrap AppRepo.updateCategory
        // as a Completable
        val item = offload {
            val cat1 = repo.synchronously.createCategory("cat1")
            val item1 = repo.synchronously.createItem(cat1.id, "item1")

            item1.name = "item2"
            repo.updateItem(item1).blockingAwait()

            repo.synchronously.getItemById(item1.id)
        }

        assertNotNull(item)
       assertEquals("item2", item!!.name)
    }

    @Test
    fun deleteItem() {
        // deleteCategory should wrap AppRepo.deleteCategory
        // as a Completable
        val item = offload {
            val cat1 = repo.synchronously.createCategory("cat1")
            val item1 = repo.synchronously.createItem(cat1.id, "item1")

            repo.deleteItem(item1.id).blockingAwait()

            repo.synchronously.getItemById(item1.id)
        }

        assertNull(item)
    }

}