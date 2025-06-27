package theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import tetherhub.composeapp.generated.resources.Res
import tetherhub.composeapp.generated.resources.quicksand

@Composable
fun DisplaySmall() = MaterialTheme.typography.displaySmall.copy(
    fontFamily = FontFamily(Font(Res.font.quicksand))
)

@Composable
fun BodySmall() = MaterialTheme.typography.bodySmall.copy(
    fontFamily = FontFamily(Font(Res.font.quicksand))
)

@Composable
fun BodyMedium() = MaterialTheme.typography.bodyMedium.copy(
    fontFamily = FontFamily(Font(Res.font.quicksand))
)

@Composable
fun BodyLarge() = MaterialTheme.typography.bodyLarge.copy(
    fontFamily = FontFamily(Font(Res.font.quicksand))
)

@Composable
fun TitleMedium() = MaterialTheme.typography.titleMedium.copy(
    fontFamily = FontFamily(Font(Res.font.quicksand))
)

@Composable
fun LabelSmall() = MaterialTheme.typography.labelSmall.copy(
    fontFamily = FontFamily(Font(Res.font.quicksand))
)

@Composable
fun LabelMedium() = MaterialTheme.typography.labelMedium.copy(
    fontFamily = FontFamily(Font(Res.font.quicksand))
)


@Composable
fun LabelLarge() = MaterialTheme.typography.labelLarge.copy(
    fontFamily = FontFamily(Font(Res.font.quicksand))
)

