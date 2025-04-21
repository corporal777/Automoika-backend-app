package kg.automoika.repository

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kg.automoika.data.remote.CarWashRemote
import kg.automoika.data.response.CarWashShortResponse
import kg.automoika.db.CarWashDatabase
import kg.automoika.extensions.ACCOUNTS_COLLECTION
import kg.automoika.extensions.CAR_WASH_COLLECTION
import kg.automoika.extensions.USERS_COLLECTION
import kotlinx.coroutines.flow.toList

import com.twilio.Twilio;
import com.twilio.converter.Promoter;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

class CommonRepositoryImpl(private val remoteDb: MongoDatabase, private val localDb: CarWashDatabase) :
    CommonRepository {

    private val carWashCollection get() = remoteDb.getCollection<CarWashRemote>(CAR_WASH_COLLECTION)

    override suspend fun copyToLocalDbFromRemote(): List<CarWashShortResponse> {
        val localData = localDb.getCarWashListLocal()
        val remoteData = carWashCollection.find().toList()
        return localDb.addCarWashPoints(remoteData)
    }

    override suspend fun checkData(): String {
        val localData = localDb.getCarWashListLocal()
        val remoteData = carWashCollection.find().toList()
        val localDataSize = "Local DB:" + localData.size.toString()
        val remoteDataSize = "Remote DB:" + remoteData.size.toString()
        return localDataSize + "\n" + remoteDataSize
    }

    override suspend fun deleteAllLocal(): String {
        return "Deleted data count: " + localDb.deleteAll().toString()
    }

//    override suspend fun sendMessage(): String {
//        val ACCOUNT_SID = "-";
//        val AUTH_TOKEN = "-";
//
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN)
//        val message = Message.creator(
//            PhoneNumber("whatsapp:+79267806176"),
//            PhoneNumber("whatsapp:+14155238886"),
//            "This is the ship that made the Kessel Run in fourteen parsecs?"
//        ).create();
//
//        println(message.getSid());
//        return message.getSid()
//    }
}