package kg.automoika.repository

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kg.automoika.data.remote.CarWashRemote
import kg.automoika.data.response.CarWashShortResponse
import kg.automoika.db.CarWashDatabase
import kg.automoika.extensions.ACCOUNTS_COLLECTION
import kg.automoika.extensions.CAR_WASH_COLLECTION
import kg.automoika.extensions.USERS_COLLECTION
import kotlinx.coroutines.flow.toList

class CommonRepositoryImpl(private val remoteDb: MongoDatabase, private val localDb: CarWashDatabase) :
    CommonRepository {

    private val carWashCollection get() = remoteDb.getCollection<CarWashRemote>(CAR_WASH_COLLECTION)

    override suspend fun copyToLocalDbFromRemote(): List<CarWashShortResponse> {
        val localData = localDb.getCarWashListLocal()
        if (localData.isEmpty()) {
            val remoteData = carWashCollection.find().toList()
            return localDb.addCarWashPoints(remoteData)
        } else return localData
    }

    override suspend fun checkData(): String {
        val localData = localDb.getCarWashListLocal()
        val remoteData = carWashCollection.find().toList()
        val localDataSize = "Local DB:" + localData.size.toString()
        val remoteDataSize = "Remote DB:" + remoteData.size.toString()
        return localDataSize + "\n" + remoteDataSize
    }
}