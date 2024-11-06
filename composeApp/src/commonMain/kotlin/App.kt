import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import auth.login.LoginScreen
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext { Navigator(LoginScreen) } // needs to initialize a splash screen and check if the user is remembered
    }
}