package com.learprogramming.artbook.framework.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.learprogramming.artbook.framework.common.uils.Status
import com.learprogramming.artbook.framework.repository.FakeRepository
import com.learprogramming.artbook.utils.MainCoroutineRule
import com.learprogramming.artbook.utils.getOrAwaitValueTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// 안드로이드 테스트가 아닌, 일반 테스트이기 때문에 메인 쓰레드, 백그라운드 쓰레드 등의 개념이 없다.
// 그러한 이유료 MainCoroutineRule() 을 적용해야 한다. 마치 이 폴더의 테스트가 메인 쓰레드에서 돌아가는 것 처럼 동작한다.
class ArtViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel : ArtViewModel
    @Before
    fun setup() {
        viewModel = ArtViewModel(FakeRepository())
    }

    // 라이브 데이터는 본질적으로 비동기적이기 때문에 테스트에서는 라이브 데이터를 이용하지 않는게 좋다.
    // 이러한 이유로 라이브 데이터를 일반적인 데이터로 바꾸는 코드를 구글에서 제공한다.
    @Test
    fun `insert art without year returns error`() {
        viewModel.makeArt("Mona Lisa", "Da Vinci", "")
        val value = viewModel.insertArtMsg.getOrAwaitValueTest()

        assertThat(value.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert art without name returns error`() {
        viewModel.makeArt("", "Da Vinci", "1000")
        val value = viewModel.insertArtMsg.getOrAwaitValueTest()
        assertThat(value.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert art without artist returns error`() {
        viewModel.makeArt("Mona Lisa", "", "1000")
        val value = viewModel.insertArtMsg.getOrAwaitValueTest()
        assertThat(value.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert art whit not number year returns error`() {
        viewModel.makeArt("Mona Lisa", "Da Vinci", "abc")
        val value = viewModel.insertArtMsg.getOrAwaitValueTest()
        assertThat(value.status).isEqualTo(Status.ERROR)
    }
}