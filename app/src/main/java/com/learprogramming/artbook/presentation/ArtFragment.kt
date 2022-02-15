package com.learprogramming.artbook.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.learprogramming.artbook.R
import com.learprogramming.artbook.databinding.FragmentArtBinding
import com.learprogramming.artbook.framework.viewmodel.ArtViewModel
import com.learprogramming.artbook.presentation.adapter.ArtListAdapter
import javax.inject.Inject

class ArtFragment @Inject constructor(
    private val artListAdapter: ArtListAdapter
): Fragment() {

    private var _binding: FragmentArtBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: ArtViewModel

    private val swipeCallBack = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val layoutPosition = viewHolder.layoutPosition
            val selectedArt = artListAdapter.artList[layoutPosition]
            viewModel.deleteArt(selectedArt)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ArtViewModel::class.java]
        subscribeToObservers()
        binding.recyclerViewArt.adapter = artListAdapter
        binding.recyclerViewArt.layoutManager = LinearLayoutManager(requireContext())

        ItemTouchHelper(swipeCallBack).attachToRecyclerView(binding.recyclerViewArt)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.artToDetail)
        }
    }

    private fun subscribeToObservers() {
        viewModel.artList.observe(viewLifecycleOwner) {
            artListAdapter.artList = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}