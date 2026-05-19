package com.example.universitynewsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.universitynewsapp.R
import com.example.universitynewsapp.model.User
import kotlin.math.abs

class UserAdapter(
    private val onUserClick: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view, onUserClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserViewHolder(
        itemView: android.view.View,
        private val onUserClick: (User) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(user: User) {
            val initials = generateInitials(user.name)
            val userInitialsTv = itemView.findViewById<TextView>(R.id.userInitials)
            userInitialsTv.text = initials
            userInitialsTv.setBackgroundColor(getColorForUser(user.id))

            itemView.findViewById<TextView>(R.id.userName).text = user.name
            itemView.findViewById<TextView>(R.id.userUsername).text = "@${user.username}"
            itemView.findViewById<TextView>(R.id.userEmail).text = user.email

            itemView.setOnClickListener { onUserClick(user) }
        }

        private fun generateInitials(name: String): String {
            val parts = name.split(" ")
            return if (parts.size >= 2) {
                "${parts[0].first()}${parts[1].first()}".uppercase()
            } else {
                parts[0].take(2).uppercase()
            }
        }

        private fun getColorForUser(userId: Int): Int {
            val colors = intArrayOf(
                0xFFE57373.toInt(),
                0xFFF06292.toInt(),
                0xFFBA68C8.toInt(),
                0xFF9575CD.toInt(),
                0xFF7986CB.toInt(),
                0xFF64B5F6.toInt(),
                0xFF4FC3F7.toInt(),
                0xFF4DD0E1.toInt(),
                0xFF4DB6AC.toInt(),
                0xFF81C784.toInt()
            )
            return colors[(abs(userId) % colors.size)]
        }
    }

    private class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }
}



