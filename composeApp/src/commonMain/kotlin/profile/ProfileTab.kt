package profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object ProfileTab : Tab {
    @Composable
    override fun Content() {
        val profileScreenModel = rememberScreenModel { ProfileScreenModel() }
        val profileUiState by profileScreenModel.uiState.collectAsState()
        Profile(profileUiState)
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Filled.Person)
            val title = "Profile"
            val index: UShort = 2u

            return TabOptions(index, title, icon)
        }
}