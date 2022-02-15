package com.learprogramming.artbook.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.learprogramming.artbook.R
import com.learprogramming.artbook.framework.repository.data.ArtEntity
import com.learprogramming.artbook.framework.viewmodel.ArtViewModel
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
class ArtDetailsFragmentTest {

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
    fun testNavigationFromArtDetailToImageApi() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<ArtDetailFragment>(factory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }
        Espresso.onView(ViewMatchers.withId(R.id.artImageView)).perform(ViewActions.click())
        Mockito.verify(navController).navigate(
            ArtDetailFragmentDirections.detailToApi()
        )
    }

    @Test
    fun testOnBackPressed() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<ArtDetailFragment>(factory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }
        Espresso.pressBack()
        Mockito.verify(navController).popBackStack()
    }

    // 테스트 버튼을 눌렀을때 viewModel 과 fragment 모두를 테스트하는 integration test 임
    @Test
    fun testSave() {
        val testViewModel = ArtViewModel(FakeRepository())
        val navController = Mockito.mock(NavController::class.java)

        launchFragmentInHiltContainer<ArtDetailFragment>(factory = fragmentFactory) {
            // ArtDetailFragment 안에 lateinit var 로 정의된 viewModel 을 초기화 해줌
            viewModel = testViewModel
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(ViewMatchers.withId(R.id.artName)).perform(ViewActions.replaceText("Mona Lisa"))
        Espresso.onView(ViewMatchers.withId(R.id.artistName)).perform(ViewActions.replaceText("Da Vinci"))
        Espresso.onView(ViewMatchers.withId(R.id.yearText)).perform(ViewActions.replaceText("1700"))
        Espresso.onView(ViewMatchers.withId(R.id.btnSave)).perform(ViewActions.click())

        assertThat(testViewModel.artList.getOrAwaitValue()).contains(
            ArtEntity("Mona Lisa", "Da Vinci", 1700, "")
        )
    }
}