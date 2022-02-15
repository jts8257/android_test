package com.learprogramming.artbook.presentation

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.learprogramming.artbook.R
import com.learprogramming.artbook.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
class ArtFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var fragmentFactory: ArtFragmentFactory

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testNavigationFromArtToArtDetails() {
//         hilt 를 이용하지 않고 fragment 를 구성하면
//        launchFragmentInContainer {
//
//        }
//        로도 fragment 테스트를 할 수 있지만, 우리는 fragmentFactory 에서 hilt 를 이용하고 있으므로 다른 방법이 필요하다.
//        때문에 구글에서 제공하는 다른 코드(launchFragmentInHiltContainer)를 이용한다.

        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<ArtFragment>(
            factory = fragmentFactory
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        // Espresso 로 fab 를 클릭하고.
        Espresso.onView(ViewMatchers.withId(R.id.fab))
            .perform(ViewActions.click())

        // mockito 에서 해당 프레그먼트로 이동됐는지 체크
        Mockito.verify(navController).navigate(R.id.artToDetail)
    }
}