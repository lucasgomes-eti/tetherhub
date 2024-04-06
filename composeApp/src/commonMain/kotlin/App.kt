import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import registration.RegistrationScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        Navigator(RegistrationScreen)
    }
}