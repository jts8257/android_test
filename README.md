# Unit Test, Integration Test 를 하는 방법을 기록


### Hilt로 의존성을 주입받는 객체를 테스트하는 방법
(1) project 수준에서 main -> java 하위에 debug 디레터리 생성</br>
(2) HiltTestActivity 생성

```kotlin
@AndroidEntryPoint
class HiltTestActivity: AppCompatActivity() {
}
```
(3) debug 용 menifest 를 따로 만들고 activity 추가
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.learprogramming.artbook">

    <application
        android:icon="@mipmap/ic_launcher">
        <activity
            android:name=".HiltTestActivity"
            android:exported="true">
        </activity>
    </application>

</manifest>
```

(4) HiltTestRunner 생성
```kotlin
class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
```
(5) HiltTestRunner 를 app 수준의 빌드 스크립트에 적용

```gradle
android {
    compileSdk 31

    defaultConfig {
        applicationId "com.learprogramming.artbook"
        minSdk 28
        targetSdk 31
        versionCode 1
        versionName "1.0"

//        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner "com.learprogramming.artbook.utils.HiltTestRunner"
    }
```

(6) 필요하다면 의존성 주입을 통제할 Module 클래스 생성</br>
이 경우는 room database 를 memory 에 일시적으로 생성하기 위해 새롭게 의존성 규칙을 설정하였음.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {
    @Provides
    @Named("testDataBase")
    fun injectInMemoryRoom(@ApplicationContext context: Context) = Room
        .inMemoryDatabaseBuilder(context, ArtDataBase::class.java)
        .allowMainThreadQueries()
        .build()
}
```

(7) Test 클래스는 아래와 같이 작성

```kotlin
@MediumTest
@HiltAndroidTest // Hilt를 통해 의존성을 주입받으므로 설정
@ExperimentalCoroutinesApi // 테스트 과정에서 코루틴을 이용하는 경우, viewModel 에서 코루팀을 이용하게 되므로
class ArtDetailsFragmentTest {

    @get:Rule // Hilt를 통해 의존성을 주입받으므로 설정
    var hiltRule = HiltAndroidRule(this)

    @get:Rule // 테스트 과정 비동적으로 처리되는 코드를 동기적으로 처리하도록 함
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    ...
}
```

(8) 테스트 코드 작성 </br>
- 네비게이션 테스트
```kotlin

// 프레그먼트 시나리오를 이용하여 네비에게이션을 설정하고, Espresso 로 특정 view 에 특정 행위를 주입, 그리고 Mosckito 에서 view가 이동되었는지 확인
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
```

- 프레그먼트와 viewModel 의 상호작용 테스트
```kotlin
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
```

-----------
## 추가된 코드

### 라이브 데이터를 일반 데이터로 변환하는 코드
```kotlin
/* Copyright 2019 Google LLC.
   SPDX-License-Identifier: Apache-2.0 */

fun <T> LiveData<T>.getOrAwaitValueTest(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValueTest.removeObserver(this)
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}
```

### Hilt를 통해 fragment factory 에서 의존성을 주입받는 Fragment 를 테스트하기 위한 코드

```kotlin
/**
 * launchFragmentInContainer from the androidx.fragment:fragment-testing library
 * is NOT possible to use right now as it uses a hardcoded Activity under the hood
 * (i.e. [EmptyFragmentActivity]) which is not annotated with @AndroidEntryPoint.
 *
 * As a workaround, use this function that is equivalent. It requires you to add
 * [HiltTestActivity] in the debug folder and include it in the debug AndroidManifest.xml file
 * as can be found in this project.
 */

inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = androidx.fragment.testing.R.style.FragmentScenarioEmptyFragmentActivityTheme,
    factory: FragmentFactory,
    crossinline action: T.() -> Unit = {}
) {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(
        "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
        themeResId
    )

    ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
        activity.supportFragmentManager.fragmentFactory = factory
        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        fragment.arguments = fragmentArgs
        activity.supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, fragment, "")
            .commitNow()

        (fragment as T).action()
    }
}
```

-----------
## 빌드 과정에서의 오류

### - Espress & Trurh : duplicate class error
espresso-contrib 과 truth 사이에 duplicate class 오류가 발생
서로 의존하는 org.checkerframework:checker 의 버전이 달라서 나타난 문제로 truth 의 버전을 1.0.1, espress 의 버전을 3.3.0 으로 낮추거나
둘다 최신 버전 (espresso 3.4.0 , truth 1.1.3) 을 사용할 경우에는 둘중 하나에서 org.checkerframework 를 exclude 하여 사용하면 됨. 

### - Mockito : missing opentest4j exceptions
2019 년에 동일한 오류가 있었고, 해결되었으나 이 프로젝트에서는 왜 발생했는지 알 수 없음.
하위 버전 2.25.0 에서는 해당 문제가 없다고 하지만, maven repository 에서 더이상 제공하지 않기 때문에 직접 opentest4j 의존성을 추가하여서 해결

### - META-INF / win32-... 파일 생성
테스트 빌드시 META-INF, win32... 파일이 생성되어서 빌드 에러가 나타남. 
해당 파일들을 앱 수준의 빌드 스크립트에서 exclude 하는 것으로 해결
