package com.learprogramming.artbook.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.bumptech.glide.RequestManager
import com.learprogramming.artbook.presentation.adapter.ArtListAdapter
import com.learprogramming.artbook.presentation.adapter.ImageSearchListAdapter
import javax.inject.Inject

class ArtFragmentFactory @Inject constructor(
    private val artListAdapter: ArtListAdapter,
    private val imageSearchListAdapter: ImageSearchListAdapter,
    private val glide: RequestManager
): FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        return when(className) {
            ArtFragment::class.java.name -> ArtFragment(artListAdapter)
            ImageApiFragment::class.java.name -> ImageApiFragment(imageSearchListAdapter)
            ArtDetailFragment::class.java.name -> ArtDetailFragment(glide)
            else -> return super.instantiate(classLoader, className)
        }
    }
}