package kg.automoika.data.remote

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class StoriesRemote(
    @BsonId
    val id : String,
    val title : String,
    val image : String,
    val stories : List<StoryModel>
)

@Serializable
data class StoryModel(
    val title: String,
    val message: String,
    val image: String,
    val link : String? = null
)