package kg.automoika.utils

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kg.automoika.data.remote.AccountRemote
import kg.automoika.data.remote.CarWashRemote
import kg.automoika.data.remote.UserRemote
import kotlinx.coroutines.flow.firstOrNull
import org.bson.conversions.Bson

fun MongoCollection<UserRemote>.findUserById(id : String): FindFlow<UserRemote> {
    return find(Filters.eq("_id", id))
}

fun MongoCollection<UserRemote>.findUserByLogin(login : String): FindFlow<UserRemote> {
    return find(Filters.eq("login.phone", login))
}

fun MongoCollection<CarWashRemote>.findCWById(id : String): FindFlow<CarWashRemote> {
    return find(Filters.eq("_id", id))
}

fun MongoCollection<AccountRemote>.findAccById(id : String): FindFlow<AccountRemote> {
    return find(Filters.eq("_id", id))
}

fun MongoCollection<AccountRemote>.findAccByLogin(login : String): FindFlow<AccountRemote> {
    return find(Filters.eq("login.phone", login))
}

suspend fun MongoCollection<AccountRemote>.updateCarWash(id : String, list: List<String>): Boolean {
    val update = Updates.set(AccountRemote::carWash.name, list)
    val query = Filters.eq("_id", id)
    return updateOne(query, update).wasAcknowledged()
}