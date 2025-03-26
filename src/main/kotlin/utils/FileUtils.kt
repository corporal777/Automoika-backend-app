package kg.automoika.utils

import com.google.firebase.cloud.StorageClient
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import kg.automoika.data.response.FBImageModel
import kg.automoika.extensions.createHttpClient
import kg.automoika.extensions.generateShortId
import kg.automoika.extensions.submitFormWithBinaryData
import java.io.File

object FileUtils {

    //private val fbBucketName = "tam-tam-8b2a7.appspot.com"
    private val fbBucketName = "automoika-kg-android.firebasestorage.app"

    fun deleteLocalFile(path: String){
        File(path).delete()
    }

    fun PartData.FileItem.saveImageLocal(path: String): String? {
        try {
            val fileBytes = streamProvider().readBytes()
            val fileExtension = originalFileName?.takeLastWhile { it != '.' }
            val fileName = originalFileName ?: ("image" + generateShortId() + (".$fileExtension"))

            val folder = File(path)
            if (!folder.parentFile.exists()) folder.parentFile.mkdirs()
            folder.mkdir()
            File("${path}/$fileName").writeBytes(fileBytes)
            return fileName
        } catch (e : Exception){
            return null
        }
    }



    suspend fun uploadImageToFirebase(part: PartData.FileItem, fileName : String): String {
        val bytes = part.streamProvider().readBytes()
        val client = createHttpClient()
        val url = "https://firebasestorage.googleapis.com/v0/b/$fbBucketName/o/$fileName"
        val response: HttpResponse = client.submitFormWithBinaryData(url, bytes, fileName)

        var imageUrl = ""
        if (response.status == HttpStatusCode.OK) {
            val token = response.body<FBImageModel>().downloadTokens
            imageUrl = URLBuilder(url).apply {
                set {
                    parameters.append("alt", "media")
                    parameters.append("token", token)
                }
            }.buildString()
        }
        client.close()
        return imageUrl
    }

    suspend fun deleteImagesFromFirebase(imgName : String): Boolean {
        return StorageClient.getInstance().bucket(fbBucketName)
            .get(imgName).delete()
    }
}