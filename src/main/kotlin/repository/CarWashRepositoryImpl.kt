package kg.automoika.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import kg.automoika.data.body.CarWashBody
import kg.automoika.data.body.CarWashFreeBoxesBody
import kg.automoika.data.remote.*
import kg.automoika.data.response.CarWashFullResponse
import kg.automoika.data.response.CarWashShortResponse
import kg.automoika.db.CarWashDatabase
import kg.automoika.extensions.*
import kg.automoika.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class CarWashRepositoryImpl(private val database: MongoDatabase, private val localDatabase: CarWashDatabase) :
    CarWashRepository {

    private val carWashCollection get() = database.getCollection<CarWashRemote>(CAR_WASH_COLLECTION)
    private val usersCollection get() = database.getCollection<UserRemote>(USERS_COLLECTION)
    private val reviewsCollection get() = database.getCollection<ReviewRemote>(REVIEW_COLLECTION)
    private val accCollection get() = database.getCollection<AccountRemote>(ACCOUNTS_COLLECTION)

    override suspend fun createCarWashPoint(model: CarWashBody, imagesList: List<CarWashImageModel>): CarWashRemote? {
        val remoteData = model.createRemote(imagesList)
        val account = accCollection.findAccById(model.userId).firstOrNull() ?: return null
        val list = mutableListOf(remoteData.id).apply { addAll(account.carWash) }
        val updateSuccess = accCollection.updateCarWash(model.userId, list)
        if (!updateSuccess) return null

        val result = carWashCollection.insertOne(remoteData)
        if (result.wasAcknowledged()) {
            localDatabase.addCarWashPoint(remoteData)
            return remoteData
        } else return null
    }


    override suspend fun updateBoxes(model: CarWashFreeBoxesBody): Boolean {
        return localDatabase.updateCarWashBoxes(model)
    }

    override suspend fun getCarWashById(id: String, params: Parameters): CarWashFullResponse? {
        val remote = carWashCollection.findCWById(id).firstOrNull() ?: return null
        val local = localDatabase.getCarWashById(id)
        if (local?.boxes != null) remote.boxes = local.boxes

        val response = CarWashFullResponse.fromRemote(remote)

        val reviewShortParams = params["reviewShort"]
        if (!reviewShortParams.isNullOrEmpty()){
            val users = usersCollection.find().toList()
            val reviews = reviewsCollection.find(Filters.eq("_id", id)).firstOrNull()
            response.binds.review = CarWashUtils.getShortReviews(reviews, users)
        }

        return response
    }

    override suspend fun getCarWashList(params: Parameters): List<CarWashShortResponse> {
        //val localData = localDatabase.getCarWashListLocal()
        //return if (localData.isNotEmpty()) localData.executeFilters(params)
        //else carWashCollection.find().toList().map { CarWashShortResponse.fromRemote(it) }.executeFilters(params)
        val localData = localDatabase.searchCarWashData(params)
        return localData
    }

}