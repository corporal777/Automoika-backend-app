package kg.automoika.db

import kg.automoika.data.response.CodeModel
import kg.automoika.db.TokenTable.autoIncrement
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object CodesTable : Table() {
    val id = integer("id").autoIncrement()
    val phone = varchar("phone", 255)
    val code = varchar("code", 255)

    override val primaryKey: PrimaryKey = PrimaryKey(id)

    fun toResponse(row: ResultRow): CodeModel {
        return CodeModel(row[CodesTable.code], row[CodesTable.phone])
    }
}