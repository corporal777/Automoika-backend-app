package kg.automoika.data.response

import kotlinx.serialization.Serializable

@Serializable
class CodeModel(
    val code: String,
    val phone: String
)