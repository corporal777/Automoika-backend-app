package kg.automoika.data.body

import kotlinx.serialization.Serializable

@Serializable
data class CarWashFreeBoxesBody(
    val id : String,
    val freeBoxes : String
)