package kg.automoika.repository

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kg.automoika.data.remote.CarWashRemote
import kg.automoika.data.remote.StoriesRemote
import kg.automoika.extensions.CAR_WASH_COLLECTION
import kg.automoika.extensions.STORIES_COLLECTION
import kotlinx.coroutines.flow.toList

class StoriesRepositoryImpl(private val remoteDb: MongoDatabase) : StoriesRepository {

    private val storiesCollection get() = remoteDb.getCollection<StoriesRemote>(STORIES_COLLECTION)

    override suspend fun getStories(): List<StoriesRemote> {
        val remoteData = storiesCollection.find().toList()
        return remoteData
    }
}