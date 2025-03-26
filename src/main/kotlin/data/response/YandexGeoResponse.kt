package kg.automoika.data.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
class YandexGeoModel(
    val response : YandexGeoResponse
)

@Serializable
class YandexGeoResponse(
    val GeoObjectCollection : YandexGeoObjectCollection
)

@Serializable
class YandexGeoObjectCollection(
    val featureMember : List<YandexGeoMember>
)

@Serializable
class YandexGeoMember(
    val GeoObject :  YandexGeoProperty
)

@Serializable
class YandexGeoProperty(
    val metaDataProperty : YandexGeoPropertyData,
    val name : String,

)

@Serializable
class YandexGeoPropertyData(
    val GeocoderMetaData : YandexGeocoderMetaData
)

@Serializable
class YandexGeocoderMetaData(
    val text : String,
    val kind : String,
    val Address : YandexGeoAddress
)

@Serializable
class YandexGeoAddress(
    val country_code : String,
    val formatted : String,
    val Components : List<YandexGeoAddressModel>
)

@Serializable
class YandexGeoAddressModel(
    val kind : String,
    val name : String
)