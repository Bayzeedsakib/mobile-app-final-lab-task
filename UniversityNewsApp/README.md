# University News App - Complete Implementation

## Overview
This Android app demonstrates a complete REST API integration using **Retrofit2**, **Kotlin Coroutines**, and **JSONPlaceholder API**. The app allows users to browse posts, view post details with comments, explore user profiles, and search for posts by title.

## Project Structure
```
UniversityNewsApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/universitynewsapp/
│   │   │   │   ├── MainActivity.kt               (Post List Screen)
│   │   │   │   ├── PostDetailActivity.kt        (Post Detail Screen)
│   │   │   │   ├── UsersActivity.kt             (Users List Screen)
│   │   │   │   ├── UserProfileActivity.kt       (User Profile Screen)
│   │   │   │   ├── adapter/
│   │   │   │   │   ├── PostAdapter.kt
│   │   │   │   │   ├── CommentAdapter.kt
│   │   │   │   │   └── UserAdapter.kt
│   │   │   │   ├── model/
│   │   │   │   │   ├── Post.kt
│   │   │   │   │   ├── User.kt
│   │   │   │   │   └── Comment.kt
│   │   │   │   ├── network/
│   │   │   │   │   ├── ApiService.kt            (Retrofit Interface)
│   │   │   │   │   └── RetrofitClient.kt       (Singleton Retrofit Instance)
│   │   │   │   └── repository/
│   │   │   │       └── PostRepository.kt        (Network Layer Abstraction)
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_post_detail.xml
│   │   │   │   │   ├── activity_users.xml
│   │   │   │   │   ├── activity_user_profile.xml
│   │   │   │   │   ├── item_post.xml
│   │   │   │   │   ├── item_comment.xml
│   │   │   │   │   └── item_user.xml
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── badge_background.xml
│   │   │   │   │   └── circle_background.xml
│   │   │   │   └── values/
│   │   │   │       └── colors.xml
│   │   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
└── gradle.properties
```

## Features Implemented

### 1. **Post List Screen (MainActivity)**
- Displays all posts from the API
- Search functionality to filter posts by title (local filtering)
- Pull-to-refresh to reload posts
- SwipeRefreshLayout integration
- Loading, success, and error states
- Tap post card to view post details
- RecyclerView with CardView items

### 2. **Post Detail Screen (PostDetailActivity)**
- Displays full post title and body
- Shows author information (name, email, company)
- Displays all comments for the post
- Click author card to navigate to user profile
- Nested RecyclerView for comments
- Separate loading indicators for post, author, and comments

### 3. **Users List Screen (UsersActivity)**
- Displays all users from the API
- Avatar circles with user initials
- Shows name, username (@handle), and email
- Color-coded avatars for different users
- Tap user card to navigate to user profile

### 4. **User Profile Screen (UserProfileActivity)**
- Large avatar with user initials
- Complete user information (name, username, email, phone, website)
- Company name and catch phrase
- List of posts by the user
- Click posts to navigate to post detail
- Colorful avatar backgrounds

## Architecture

### Model Layer
- **Post.kt** - Data class for post objects
- **User.kt** - Data class for user objects with nested Company data
- **Comment.kt** - Data class for comment objects

### Network Layer
- **ApiService.kt** - Retrofit interface with all API endpoints
- **RetrofitClient.kt** - Singleton Retrofit instance with OkHttp logging

### Repository Layer
- **PostRepository.kt** - Abstracts all API calls for cleaner code

### UI Layer
- **Activities** - MainActivity, PostDetailActivity, UsersActivity, UserProfileActivity
- **Adapters** - PostAdapter, CommentAdapter, UserAdapter (ListAdapter pattern with DiffUtil)
- **Layouts** - Material Design CardView layouts

## API Endpoints Used

All endpoints are from JSONPlaceholder (free REST API):
- `GET /posts` - Fetch all posts
- `GET /posts/{id}` - Fetch single post
- `GET /posts/{id}/comments` - Fetch comments for post
- `GET /users` - Fetch all users
- `GET /users/{id}` - Fetch single user
- `GET /users/{id}/posts` - Fetch posts by user

## Dependencies Included

```gradle
// Retrofit + Gson
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// OkHttp logging
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'

// UI Components
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.cardview:cardview:1.0.0'
implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
implementation 'com.github.bumptech.glide:glide:4.16.0'
```

## Key Implementation Details

### Retrofit Setup
```kotlin
object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
```

### Coroutines Usage
```kotlin
lifecycleScope.launch {
    try {
        val posts = repository.getAllPosts()
        // Update UI
        adapter.submitList(posts)
    } catch (e: HttpException) {
        showError("Server error: ${e.code()}")
    } catch (e: IOException) {
        showError("Network error")
    }
}
```

### ListAdapter Pattern
All adapters use the modern ListAdapter with DiffUtil for efficient list updates:
```kotlin
class PostAdapter(onPostClick: (Post) -> Unit) : 
    ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback())
```

## Running the App

1. Clone/Extract the project
2. Open in Android Studio
3. Build via Gradle: `./gradlew build`
4. Run on emulator or device: `./gradlew installDebug`
5. Open the app to see the post list

## Error Handling

The app includes comprehensive error handling:
- **Network errors** - "Check your connection" message
- **Server errors** - Shows HTTP error code
- **Parse errors** - Generic error message with details
- **Retry button** - Allows retry after error

## UI States

All screens implement three UI states:
- **Loading** - ProgressBar displayed
- **Success** - RecyclerView/content displayed
- **Error** - Error message with retry button

## Search Implementation

Post search is implemented locally after fetching:
```kotlin
private fun filterPosts(query: String) {
    val filteredPosts = if (query.isEmpty()) {
        allPosts
    } else {
        allPosts.filter { post ->
            post.title.contains(query, ignoreCase = true)
        }
    }
    adapter.submitList(filteredPosts)
}
```

## Future Enhancements

Potential improvements:
- Pagination for large datasets
- Offline caching with Room database
- Image loading with Glide
- User authentication
- Favorites/bookmarks
- Dark mode support
- Animation transitions between screens

## Permissions

The app requires INTERNET permission (already added to AndroidManifest.xml):
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Notes

- Target API: 36
- Minimum API: 24 (Android 7.0)
- Language: Kotlin
- All network calls use suspend functions for clean async code
- Lifecycle-aware coroutines prevent memory leaks

