package eti.lucasgomes.tetherhub

import App
import DeepLink
import DeepLinkDestination
import NotificationType
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val payload = mutableMapOf<String, String>()
        intent.extras?.keySet()?.forEach {
            payload[it] = intent.extras?.getString(it) ?: ""
        }
        setContent {
            App(
                deepLink = when (payload["type"]) {
                    NotificationType.CHAT.name -> {
                        DeepLink(
                            destination = DeepLinkDestination.CHAT,
                            resourceId = payload["chatId"]!!
                        )
                    }

                    NotificationType.FRIENDS.name -> {
                        DeepLink(
                            destination = DeepLinkDestination.FRIENDS,
                            resourceId = ""
                        )
                    }

                    else -> null
                }
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}