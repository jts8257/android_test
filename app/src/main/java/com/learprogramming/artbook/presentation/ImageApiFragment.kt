package com.learprogramming.artbook.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.learprogramming.artbook.databinding.FragmentImageApiBinding
import com.learprogramming.artbook.framework.common.uils.Status
import com.learprogramming.artbook.framework.viewmodel.ArtViewModel
import com.learprogramming.artbook.presentation.adapter.ImageSearchListAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImageApiFragment @Inject constructor(
    val imageSearchListAdapter: ImageSearchListAdapter
): Fragment() {

    private var _binding: FragmentImageApiBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: ArtViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageApiBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity())[ArtViewModel::class.java]
        subscribeToObservers()
        binding.imageRecyclerView.adapter = imageSearchListAdapter
        binding.imageRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        imageSearchListAdapter.setOnItemClickListener {
            findNavController().popBackStack()
            viewModel.setSelectedImage(it)
        }

        var job: Job? = null

        binding.searchText.addTextChangedListener {
            job?.cancel()
            job = lifecycleScope.launch {
                delay(500)
                it?.let {
                    if (it.toString().isNotEmpty())
                        viewModel.searchForImage(it.toString())
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun subscribeToObservers() {
        viewModel.images.observe(viewLifecycleOwner) {
            when(it.status) {
                Status.SUCCESS -> {
                    val urls = it.data?.hits?.map { imageResult ->
                        imageResult.previewURL
                    }

                    imageSearchListAdapter.searchResultList = urls ?: listOf()
                    binding.progressBar.visibility = View.GONE
                    viewModel.resetImages()
                }

                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message ?: "Error", Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                    viewModel.resetImages()
                }

                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}