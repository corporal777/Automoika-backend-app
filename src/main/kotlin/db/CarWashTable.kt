package kg.automoika.db

import kg.automoika.data.remote.CarWashBoxesModel
import kg.automoika.data.response.CarWashShortLocationModel
import kg.automoika.data.response.CarWashShortResponse
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update

object CarWashTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 20)
    val date = varchar("date", 30)

    val city = varchar("city", 20)
    val street = varchar("street", 100)
    val district = varchar("district", 100)
    val lat = varchar("lat", 50)
    val lon = varchar("lon", 50)

    val image = varchar("image", 300)
    val boxesCount = varchar("boxesCount", 20)
    val freeBoxes = varchar("freeBoxes", 20)

    val favourites = array<String>("favourites")
    val type = varchar("type", 20)
    val owner = varchar("owner", 20)

    override val primaryKey: PrimaryKey = PrimaryKey(id)

    fun carWashTableToResponse(row: ResultRow): CarWashShortResponse {
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
