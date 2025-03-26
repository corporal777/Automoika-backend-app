package kg.automoika.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import kg.automoika.data.body.CarWashBody
import kg.automoika.data.body.CarWashFreeBoxesBody
import kg.automoika.data.remote.*
import kg.automoika.data.response.CarWashShortResponse
import kg.automoika.db.CarWashDatabase
import kg.automoika.extensions.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class CarWashRepositoryImpl(private val database: MongoDatabase, private val localDatabase: CarWashDatabase) :
    CarWashRepository {

    private val carWashCollection get() = database.getCollection<CarWashRemote>(CAR_WASH_COLLECTION)
    private val usersCollection get() = database.getCollection<UserRemote>(USERS_COLLECTION)


    override suspend fun createCarWashPoint(model: CarWashBody, imagesList: List<CarWashImageModel>): CarWashRemote? {
        if (model.userId.isNullOrEmpty()){
            val remote = UserRemote.createFromCarWash(model)
            usersCollection.insertOne(remote)
        } else {
            val account = UserAccountModel(TYPE_CAR_WASH_OWNER, listOf(model.id))
            val updates = Updates.set(UserRemote::account.name, account)
            val query = Filters.eq("_id", model.userId)
            usersCollection.updateOne(query, updates)
        }
        val remoteData = model.createRemote(imagesList)

        val resultLocal = localDatabase.addCarWashPoint(remoteData)
        if (!resultLocal) return null

        val result = carWashCollection.insertOne(remoteData)
        return if (result.wasAcknowledged()) remoteData else null
    }


    override suspend fun updateBoxes(model: CarWashFreeBoxesBody): Boolean {
        return localDatabase.updateCarWashBoxes(model)
    }

    override suspend fun getCarWashById(id: String): CarWashRemote? {
        val local = localDatabase.getCarWashById(id)
        val remoteData = carWashCollection.find(Filters.eq("_id", id)).firstOrNull()
        if (local?.boxes != null) remoteData?.boxes = local.boxes
        return remoteData
    }

    override suspend fun getCarWashList(params: Parameters): List<CarWashShortResponse> {
        val localData = localDatabase.getCarWashListLocal()
        return if (localData.isNotEmpty()) localData.executeFilters(params)
        else carWashCollection.find().toList().map { CarWashShortResponse.fromRemote(it) }.executeFilters(params)
    }






    private suspend fun List<CarWashShortResponse>.executeFilters(params: Parameters): List<CarWashShortResponse> {
        val search = params["search"]

        val status = params["status"]
        val limit = params["limit"]
        val offset = params["offset"]

        return withContext(Dispatchers.IO) {
            this@executeFilters
                .let { list ->
                    //search filter
                    if (search.isNullOrEmpty()) list
                    else list.filter { it.name.hasText(search) || it.address.street.hasText(search) }
                }
                .let { if (!status.isNullOrEmpty()) it.filter { it.name.contains(status) } else it }
        }
    }


}