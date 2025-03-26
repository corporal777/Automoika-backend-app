package kg.automoika.db

import com.google.rpc.Code
import kg.automoika.data.response.CodeModel
import kg.automoika.data.response.TokenResponse
import kg.automoika.extensions.generateCode
import kg.automoika.extensions.generateId
import kg.automoika.extensions.generateToken
import kg.automoika.extensions.suspendTransaction
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object AuthDatabase {


    suspend fun getToken(token: String) = suspendTransaction {
        val local = TokenTable.select { TokenTable.token eq token }.firstOrNull()
        if (local == null) null else TokenTable.toResponse(local)
    }

    suspend fun createToken() = suspendTransaction {
        val token = TokenTable.insert {
            it[id] = generateId().toInt()
            it[token] = generateToken()
            it[createdAt] = System.currentTimeMillis().toString()
        }.resultedValues?.firstOrNull()

        if (token != null) TokenTable.toResponse(token) else null
    }

    suspend fun createCode(login: String) = suspendTransaction {
        CodesTable.deleteWhere { phone eq login }
        val token = CodesTable.insert {
            it[id] = generateId().toInt()
            it[code] = generateCode()
            it[phone] = login
        }.resultedValues?.firstOrNull()

        if (token != null) CodesTable.toResponse(token).code else "998877"
    }

    suspend fun checkCode(code: String, phone: String) = suspendTransaction {
        val local = CodesTable.select { CodesTable.phone eq phone }.firstOrNull() ?: return@suspendTransaction false
        val result = CodesTable.toResponse(local)
        if (result.code == code) CodesTable.deleteWhere { CodesTable.phone eq phone } > 0
        else false
    }


}