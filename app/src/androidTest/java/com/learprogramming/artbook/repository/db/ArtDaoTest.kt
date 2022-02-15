package com.learprogramming.artbook.repository.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.learprogramming.artbook.framework.repository.data.ArtEntity
import com.learprogramming.artbook.framework.repository.db.ArtDao
import com.learprogramming.artbook.framework.repository.db.ArtDataBase
import com.learprogramming.artbook.utils.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@SmallTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class ArtDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("testDataBase")
    lateinit var database: ArtDataBase

    private lateinit var dao: ArtDao

    @Before
    fun setup() {
        hiltRule.inject()
        dao = database.artDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertArtTesting() = runBlockingTest {
        val testData = ArtEntity("Mona Lisa", "Da Vinci", 1700, "test.com", 1)
        dao.insertArt(testData)

        val list = dao.observeArts().getOrAwaitValue()
        assertThat(list).contains(testData)
    }

    @Test
    fun deleteArtTesting() = runBlockingTest {
        val testData = ArtEntity("Mona Lisa", "Da Vinci", 1700, "test.com", 1)
        dao.insertArt(testData)
        dao.deleteArt(testData)

        val list = dao.observeArts().getOrAwaitValue()
        assertThat(list).doesNotContain(testData)
    }
}