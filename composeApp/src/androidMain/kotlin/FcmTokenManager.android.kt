import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.tasks.await

actual object FcmTokenManager {
    actual suspend fun getFcmToken(): String {
        return Firebase.messaging.token.await()
    }
}