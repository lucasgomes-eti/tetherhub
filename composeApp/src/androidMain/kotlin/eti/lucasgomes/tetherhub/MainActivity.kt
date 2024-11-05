package eti.lucasgomes.tetherhub

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import auth.registration.network.RegistrationClient
import createHttpClient
import io.ktor.client.engine.okhttp.OkHttp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(registrationClient = RegistrationClient(createHttpClient(OkHttp.create())))
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    //App()
}