package com.learprogramming.artbook.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.learprogramming.artbook.databinding.ItemSearchResultBinding
import javax.inject.Inject

class ImageSearchListAdapter @Inject constructor(
    val glide: RequestManager
    ): RecyclerView.Adapter<ImageSearchListAdapter.ImageSearchHolder>() {

    private var onItemClickListener: ((String) -> Unit)? = null

    private val diffUtil = object: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var searchResultList: List<String>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun getItemCount(): Int = searchResultList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSearchHolder =
        ImageSearchHolder(ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context),parent, false))

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ImageSearchHolder, position: Int) {
        holder.bind(searchResultList[position])
    }

    inner class ImageSearchHolder(val binding: ItemSearchResultBinding): RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.singleArtImageView

        fun bind(item: String) {
            glide.load(item).into(imageView)
            binding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(item)
                }
            }
        }
    }
}