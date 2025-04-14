package kg.automoika.repository

import kg.automoika.data.remote.StoriesRemote

interface StoriesRepository {
    suspend fun getStories() : List<StoriesRemote>
}