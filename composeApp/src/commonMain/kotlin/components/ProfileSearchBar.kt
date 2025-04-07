package components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.ProfileSearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onCancelSearch: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    var isSearchBarFocused by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    SearchBar(
        modifier = Modifier.weight(1f, true)
            .semantics {
                isTraversalGroup = true
                traversalIndex = 0f
            },
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.onFocusChanged { isSearchBarFocused = it.isFocused },
                query = searchQuery,
                onQueryChange = { onSearchQueryChanged(it) },
                onSearch = { onSearch() },
                expanded = false,
                onExpandedChange = { },
                placeholder = { Text("Search username") },
                trailingIcon = {
                    IconButton(onClick = { onSearch() }) {
                        Icon(Icons.Default.Search, null)
                    }
                },
                leadingIcon = {
                    if (isSearchBarFocused) {
                        IconButton(onClick = {
                            onCancelSearch()
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Default.Close, null)
                        }
                    } else if (onNavigateBack != null) {
                        IconButton(onClick = { onNavigateBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }
                },
            )
        },
        expanded = false,
        onExpandedChange = { },
    ) {}
}