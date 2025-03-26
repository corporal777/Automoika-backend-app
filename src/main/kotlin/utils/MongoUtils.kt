package kg.automoika.utils

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kg.automoika.data.remote.CarWashRemote
import kg.automoika.data.remote.UserRemote

fun MongoCollection<UserRemote>.findUserById(id : String): FindFlow<UserRemote> {
    return find(Filters.eq("_id", id))
}

fun MongoCollection<CarWashRemote>.findCWById(id : String): FindFlow<CarWashRemote> {
    return find(Filters.eq("_id", id))
}