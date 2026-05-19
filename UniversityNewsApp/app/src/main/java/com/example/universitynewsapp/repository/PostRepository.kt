package com.example.universitynewsapp.repository

import com.example.universitynewsapp.model.Comment
import com.example.universitynewsapp.model.Post
import com.example.universitynewsapp.model.User
import com.example.universitynewsapp.network.RetrofitClient

class PostRepository {
    private val apiService = RetrofitClient.instance

    suspend fun getAllPosts(): List<Post> = apiService.getAllPosts()

    suspend fun getPostById(id: Int): Post = apiService.getPostById(id)

    suspend fun getCommentsByPost(postId: Int): List<Comment> =
        apiService.getCommentsByPost(postId)

    suspend fun getAllUsers(): List<User> = apiService.getAllUsers()

    suspend fun getUserById(id: Int): User = apiService.getUserById(id)

    suspend fun getPostsByUser(userId: Int): List<Post> =
        apiService.getPostsByUser(userId)
}

