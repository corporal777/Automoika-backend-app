package kg.automoika.db

import io.ktor.http.*
import kg.automoika.data.body.CarWashFreeBoxesBody
import kg.automoika.data.remote.CarWashBoxesModel
import kg.automoika.data.remote.CarWashRemote
import kg.automoika.data.response.CarWashShortLocationModel
import kg.automoika.data.response.CarWashShortResponse
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
        CarWashTable.batchInsert(list) {
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
        }.map { resultRowToCarWashResponse(it) }
    }


    suspend fun getCarWashById(id : String) = suspendTransaction {
        val local = CarWashTable.select { CarWashTable.id eq id.toInt() }.firstOrNull()
        if (local == null) null else resultRowToCarWashResponse(local)
    }


    suspend fun getCarWashListLocal() = suspendTransaction {
        try {
            CarWashTable.selectAll().map { resultRowToCarWashResponse(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchCarWashData(params : Parameters) = suspendTransaction {
        val search = params["search"]
        val boxes = params["boxes"]
        val limit = params["limit"]?.toInt() ?: 30
        val offset = params["offset"]?.toLong() ?: 0

        val filters = arrayListOf<Op<Boolean>>().apply {
            if (!search.isNullOrEmpty()){
                add(CarWashTable.name eq search or (CarWashTable.street like "%${search}%"))
            }
            if (boxes.toBoolean()){
                add(CarWashTable.freeBoxes neq "0")
            }
        }

        CarWashTable.select {
            if (filters.isEmpty()) CarWashTable.id neq -1
            else filters.compoundAnd()
        }.limit(limit, offset).map { resultRowToCarWashResponse(it) }
    }



    private fun resultRowToCarWashResponse(row: ResultRow): CarWashShortResponse {
        return CarWashShortResponse(
            id = row[CarWashTable.id].toString(),
            name = row[CarWashTable.name],
            backgroundImage = row[CarWashTable.image],
            address = CarWashShortLocationModel(
                street = row[CarWashTable.street],
                city = row[CarWashTable.city],
                lat = row[CarWashTable.lat],
                lon = row[CarWashTable.lon],
                district = row[CarWashTable.district]
            ),
            boxes = CarWashBoxesModel(row[CarWashTable.boxesCount], row[CarWashTable.freeBoxes]),
            favourites = row[CarWashTable.favourites],
            owner = row[CarWashTable.owner],
            type = row[CarWashTable.type],
        )
    }
}