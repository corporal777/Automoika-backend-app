package kg.automoika.repository

import io.ktor.http.*
import kg.automoika.data.body.CarWashBody
import kg.automoika.data.body.CarWashFreeBoxesBody
import kg.automoika.data.remote.CarWashImageModel
import kg.automoika.data.remote.CarWashRemote
import kg.automoika.data.response.CarWashShortResponse

interface CarWashRepository {
    suspend fun createCarWashPoint(model : CarWashBody, imagesList: List<CarWashImageModel>) : CarWashRemote?
    suspend fun getCarWashList(params : Parameters) : List<CarWashShortResponse>
    suspend fun getCarWashById(id : String) : CarWashRemote?

    suspend fun updateBoxes(model: CarWashFreeBoxesBody) : Boolean

}