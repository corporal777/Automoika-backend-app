package kg.automoika.db

import kg.automoika.data.response.TokenResponse
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object TokenTable : Table() {
    val id = integer("id").autoIncrement()
    val token = varchar("token", 255)
    val createdAt = varchar("createdAt", 255)

    override val primaryKey: PrimaryKey = PrimaryKey(id)

    fun toResponse(row: ResultRow): TokenResponse {
        return TokenResponse(row[token], row[createdAt])
    }
}