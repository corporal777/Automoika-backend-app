package kg.automoika.db

import org.jetbrains.exposed.sql.Table

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
}
