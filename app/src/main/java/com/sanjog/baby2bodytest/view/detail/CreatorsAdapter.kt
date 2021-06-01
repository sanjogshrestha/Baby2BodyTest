package com.sanjog.baby2bodytest.view.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sanjog.baby2bodytest.data.entity.CreatorsEntity
import com.sanjog.baby2bodytest.databinding.LayoutCreatorViewBinding

class CreatorsAdapter : ListAdapter<CreatorsEntity,
        CreatorsAdapter.ViewHolder>(CreatorsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutCreatorViewBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: LayoutCreatorViewBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CreatorsEntity) {
            binding.apply {
                entity = item
                executePendingBindings()
            }
        }
    }
}

class CreatorsDiffCallback : DiffUtil.ItemCallback<CreatorsEntity>() {
    override fun areItemsTheSame(oldItem: CreatorsEntity, newItem: CreatorsEntity): Boolean {
        return (oldItem === newItem)
    }

    override fun areContentsTheSame(oldItem: CreatorsEntity, newItem: CreatorsEntity): Boolean {
        return (oldItem.id == newItem.id)
                && (oldItem.name == newItem.name)
                && (oldItem.comicID == newItem.comicID)
                && (oldItem.role == newItem.role)
    }
}