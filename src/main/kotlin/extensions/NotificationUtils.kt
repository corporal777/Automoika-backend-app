package kg.automoika.extensions

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification

object NotificationUtils {

    fun sendVerificationCode(token : String, code : String): String? {
        val message = Message.builder()
            .setToken(token)
            .setNotification(
                Notification.builder()
                    .setTitle("Подтверждение номера")
                    .setBody("Код подтверждения: $code")
                    .build()
            )
            .build()

        return FirebaseMessaging.getInstance(FirebaseApp.getInstance("ktor-app")).send(message)
    }
}