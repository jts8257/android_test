package com.learprogramming.artbook.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.learprogramming.artbook.databinding.FragmentArtDetailBinding
import com.learprogramming.artbook.framework.common.uils.Status
import com.learprogramming.artbook.framework.viewmodel.ArtViewModel
import javax.inject.Inject

class ArtDetailFragment @Inject constructor(
    val glide: RequestManager
): Fragment() {

    private var _binding: FragmentArtDetailBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: ArtViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ArtViewModel::class.java]

        binding.artImageView.setOnClickListener {
            val action = ArtDetailFragmentDirections.detailToApi()
            findNavController().navigate(action)
        }

        subscribeToObservers()

        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        binding.btnSave.setOnClickListener {
            viewModel.makeArt(binding.artName.text.toString(),
                binding.artistName.text.toString(),
                binding.yearText.text.toString())
        }
    }

    private fun subscribeToObservers() {
        viewModel.selectedImageUrl.observe(viewLifecycleOwner) { url ->
            glide.load(url).into(binding.artImageView)
        }

        viewModel.insertArtMsg.observe(viewLifecycleOwner) {
            when(it.status) {
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                    viewModel.resetInsertArtMsg()
                }

                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message ?: "Error", Toast.LENGTH_LONG).show()
                    viewModel.resetInsertArtMsg()
                }

                Status.LOADING -> {
                    viewModel.resetInsertArtMsg()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}