package kg.automoika.db

import io.ktor.http.*
import kg.automoika.data.body.CarWashFreeBoxesBody
import kg.automoika.data.remote.CarWashBoxesModel
import kg.automoika.data.remote.CarWashRemote
import kg.automoika.data.response.CarWashShortLocationModel
import kg.automoika.data.response.CarWashShortResponse
import kg.automoika.db.CarWashTable.carWashTableToResponse
import kg.automoika.db.CarWashTable.freeBoxes
import kg.automoika.extensions.suspendTransaction
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object CarWashDatabase {


    suspend fun updateCarWashBoxes(model: CarWashFreeBoxesBody) = suspendTransaction {
        CarWashTable.update({ CarWashTable.id eq model.id.toInt() }) {
            it[freeBoxes] = model.freeBoxes
        } > 0
    }

    suspend fun deleteAll() = suspendTransaction {
        CarWashTable.deleteAll()
    }

    suspend fun addCarWashPoint(model: CarWashRemote) = suspendTransaction {
        CarWashTable.insert {
            it[id] = model.id.toInt()
            it[name] = model.name
            it[date] = model.createdAt

            it[city] = model.address.city
            it[street] = model.address.street
            it[district] = model.address.district
            it[lat] = model.address.lat
            it[lon] = model.address.lon

            it[image] = model.backgroundImage.imageUrl
            it[freeBoxes] = model.boxes.free
            it[boxesCount] = model.boxes.count

            it[favourites] = model.favourites
            it[type] = model.type
            it[owner] = model.owner
        }.insertedCount > 0
    }

    suspend fun addCarWashPoints(list: List<CarWashRemote>) = suspendTransaction {
        CarWashTable.batchInsert(list, true) {
            this[CarWashTable.id] = it.id.toInt()
            this[CarWashTable.name] = it.name
            this[CarWashTable.date] = it.createdAt

            this[CarWashTable.city] = it.address.city
            this[CarWashTable.street] = it.address.street
            this[CarWashTable.district] = it.address.district
            this[CarWashTable.lat] = it.address.lat
            this[CarWashTable.lon] = it.address.lon

            this[CarWashTable.image] = it.backgroundImage.imageUrl
            this[CarWashTable.boxesCount] = it.boxes.count
            this[CarWashTable.freeBoxes] = it.boxes.free

            this[CarWashTable.favourites] = it.favourites
            this[CarWashTable.type] = it.type
            this[CarWashTable.owner] = it.owner
        }.map { carWashTableToResponse(it) }
    }


    suspend fun getCarWashById(id : String) = suspendTransaction {
        val local = CarWashTable.select { CarWashTable.id eq id.toInt() }.firstOrNull()
        if (local == null) null else carWashTableToResponse(local)
    }


    suspend fun getCarWashListLocal() = suspendTransaction {
        CarWashTable.selectAll().map { carWashTableToResponse(it) }
    }




    suspend fun searchCarWashData(params : Parameters) = suspendTransaction {
        val search = params["search"]
        val boxes = params["boxes"]
        val district = params["district"]
        val type = params["type"]
        val limit = params["limit"]?.toInt() ?: 30
        val offset = params["offset"]?.toLong() ?: 0

        val filters = arrayListOf<Op<Boolean>>().apply {
            if (!search.isNullOrEmpty()){
                val regex = "%${search}%"
                add(CarWashTable.name like regex or (CarWashTable.street like regex))
            }
            if (boxes.toBoolean()){
                add(CarWashTable.freeBoxes neq "0")
            }
            if (!district.isNullOrEmpty()){
                add(CarWashTable.district eq district)
            }
            if (!type.isNullOrEmpty()){
                add(CarWashTable.type eq type)
            }
        }

        CarWashTable.select {
            if (filters.isEmpty()) CarWashTable.id neq -1
            else filters.compoundAnd()
        }.limit(limit, offset).map { carWashTableToResponse(it) }
    }
}