package com.example.universitynewsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.universitynewsapp.model.Post

class PostAdapter(
    private val onPostClick: (Post) -> Unit
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(com.example.universitynewsapp.R.layout.item_post, parent, false)
        return PostViewHolder(view, onPostClick)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(
        itemView: android.view.View,
        private val onPostClick: (Post) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(post: Post) {
            itemView.findViewById<android.widget.TextView>(com.example.universitynewsapp.R.id.titleText).text = post.title
            itemView.findViewById<android.widget.TextView>(com.example.universitynewsapp.R.id.bodyText).text = post.body
            itemView.findViewById<android.widget.TextView>(com.example.universitynewsapp.R.id.userIdBadge).text = "User ${post.userId}"
            itemView.findViewById<android.widget.TextView>(com.example.universitynewsapp.R.id.postIdText).text = "Post #${post.id}"
            itemView.setOnClickListener { onPostClick(post) }
        }
    }

    private class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
    }
}



