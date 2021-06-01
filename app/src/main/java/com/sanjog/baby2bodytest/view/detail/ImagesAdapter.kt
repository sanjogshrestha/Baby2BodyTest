package com.sanjog.baby2bodytest.view.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sanjog.baby2bodytest.databinding.LayoutImageViewBinding

class ImagesAdapter(var listener: OnItemClickListener) : ListAdapter<String,
        ImagesAdapter.ViewHolder>(ImagesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutImageViewBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class ViewHolder(private val binding: LayoutImageViewBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, listener: OnItemClickListener) {
            binding.apply {
                url = item
                binding.root.setOnClickListener {
                    listener.onComicThumbnailClicked(item)
                }
                executePendingBindings()
            }
        }
    }
}

class ImagesDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return (oldItem === newItem)
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return (oldItem == newItem)
    }
}