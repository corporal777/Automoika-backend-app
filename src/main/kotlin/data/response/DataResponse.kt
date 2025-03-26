package kg.automoika.data.response

import kotlinx.serialization.Serializable


@Serializable
class DataResponse<T>(
    val data: List<T>,
    val totalCount: Int
){
    companion object {
        fun <T>fromList(list: List<T>) : DataResponse<T>{
            return DataResponse(list, list.size)
        }
    }
}