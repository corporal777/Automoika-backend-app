package kg.automoika.repository

import kg.automoika.data.response.CarWashShortResponse

interface CommonRepository {

    suspend fun checkData() : String
    suspend fun copyToLocalDbFromRemote() : List<CarWashShortResponse>
}