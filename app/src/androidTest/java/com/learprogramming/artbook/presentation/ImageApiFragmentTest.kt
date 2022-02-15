package com.learprogramming.artbook.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.learprogramming.artbook.R
import com.learprogramming.artbook.framework.viewmodel.ArtViewModel
import com.learprogramming.artbook.presentation.adapter.ImageSearchListAdapter
import com.learprogramming.artbook.repository.FakeRepository
import com.learprogramming.artbook.utils.getOrAwaitValue
import com.learprogramming.artbook.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class ImageApiFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: ArtFragmentFactory

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun selectImage() {
        val navController = Mockito.mock(NavController::class.java)
        val selectedImageUrl = "hello.com"
        val testViewModel = ArtViewModel(FakeRepository())

        launchFragmentInHiltContainer<ImageApiFragment>(factory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = testViewModel
            imageSearchListAdapter.searchResultList = listOf(selectedImageUrl)
        }

        Espresso.onView(ViewMatchers.withId(R.id.imageRecyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ImageSearchListAdapter.ImageSearchHolder>(
                0,ViewActions.click()
            )
        )

        Mockito.verify(navController).popBackStack()
        assertThat(testViewModel.selectedImageUrl.getOrAwaitValue()).isEqualTo(selectedImageUrl)
    }
}