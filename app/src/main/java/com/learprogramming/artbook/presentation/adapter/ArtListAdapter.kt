package com.learprogramming.artbook.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.learprogramming.artbook.databinding.ItemArtBinding
import com.learprogramming.artbook.framework.repository.data.ArtEntity
import javax.inject.Inject

class ArtListAdapter @Inject constructor(
    val glide: RequestManager
    ): RecyclerView.Adapter<ArtListAdapter.ArtListHolder>() {

    private val diffUtil = object: DiffUtil.ItemCallback<ArtEntity>() {
        override fun areItemsTheSame(oldItem: ArtEntity, newItem: ArtEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ArtEntity, newItem: ArtEntity): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var artList: List<ArtEntity>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun getItemCount(): Int = artList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtListHolder =
        ArtListHolder(ItemArtBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ArtListHolder, position: Int) {
        holder.bind(artList[position])
    }

    inner class ArtListHolder(val binding: ItemArtBinding): RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.artRowImageView
        val nameText = binding.artRowNameText
        val artistNameText = binding.artRowArtistName
        val yearText = binding.artRowYearText

        fun bind(item: ArtEntity) {
            nameText.text = "Name ${item.name}"
            artistNameText.text = "Artist name: ${item.artistname}"
            yearText.text = "Year ${item.year}"
            glide.load(item.imageUrl).into(imageView)
        }
    }
}