import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import auth.login.LoginScreen
import auth.registration.network.RegistrationClient
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(registrationClient: RegistrationClient) {
    MaterialTheme {
        Navigator(LoginScreen(registrationClient)) // needs to initialize a splash screen and check if the user is remembered
    }
}