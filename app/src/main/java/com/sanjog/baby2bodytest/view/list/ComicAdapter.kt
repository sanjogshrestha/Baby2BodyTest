package com.sanjog.baby2bodytest.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sanjog.baby2bodytest.data.entity.ComicEntity
import com.sanjog.baby2bodytest.databinding.LayoutComicViewBinding

class ComicAdapter(var listener: OnItemClickListener) : ListAdapter<ComicEntity, ComicAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutComicViewBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class ViewHolder(private val binding: LayoutComicViewBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ComicEntity, listener: OnItemClickListener) {
            binding.apply {
                entity = item
                this.root.setOnClickListener {
                    listener.onComicThumbnailClicked(item, this.imageView)
                }
                executePendingBindings()
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<ComicEntity>() {
    override fun areItemsTheSame(oldItem: ComicEntity, newItem: ComicEntity): Boolean {
        return (oldItem.id == newItem.id)
                && (oldItem.digitalId == newItem.digitalId)
                && (oldItem.title == newItem.title)
                && (oldItem.modified == newItem.modified)
                && oldItem.issn == newItem.issn
    }

    override fun areContentsTheSame(oldItem: ComicEntity, newItem: ComicEntity): Boolean {
        return (oldItem.thumbnail == newItem.thumbnail)
                && (oldItem.pageCount == newItem.pageCount)
                && (oldItem.focDate == newItem.focDate)
                && (oldItem.onSaleDate == newItem.onSaleDate)
                && oldItem.images == newItem.images
    }
}