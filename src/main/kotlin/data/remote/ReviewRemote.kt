package kg.automoika.data.remote

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class ReviewRemote(
    @BsonId
    val id : String,
    var users : List<ReviewSenderModel>
)

@Serializable
data class ReviewSenderModel(
    val userId : String,
    val text : String
)