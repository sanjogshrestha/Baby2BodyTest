package com.sanjog.baby2bodytest.view.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sanjog.baby2bodytest.data.entity.CharactersEntity
import com.sanjog.baby2bodytest.databinding.LayoutImageViewBinding

class CharacterAdapter(var listener: OnItemClickListener) : ListAdapter<CharactersEntity,
        CharacterAdapter.ViewHolder>(CharactersDiffCallback()) {

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
        fun bind(item: CharactersEntity, listener: OnItemClickListener) {
            binding.apply {
                url = item.thumbnail
                binding.root.setOnClickListener {
                    listener.onCharacterThumbnailClicked(item)
                }
                executePendingBindings()
            }
        }
    }
}

class CharactersDiffCallback : DiffUtil.ItemCallback<CharactersEntity>() {
    override fun areItemsTheSame(oldItem: CharactersEntity, newItem: CharactersEntity): Boolean {
        return (oldItem === newItem)
    }

    override fun areContentsTheSame(oldItem: CharactersEntity, newItem: CharactersEntity): Boolean {
        return (oldItem.id == newItem.id)
                && (oldItem.name == newItem.name)
                && (oldItem.comicID == newItem.comicID)
                && (oldItem.thumbnail == newItem.thumbnail)
    }
}