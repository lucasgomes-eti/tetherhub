import androidx.compose.ui.window.ComposeUIViewController
import auth.registration.network.RegistrationClient
import io.ktor.client.engine.darwin.Darwin

fun MainViewController() =
    ComposeUIViewController { App(registrationClient = RegistrationClient(createHttpClient(Darwin.create()))) }