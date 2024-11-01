package auth.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registration(
    registrationUiState: RegistrationUiState,
    onRegistrationAction: (RegistrationAction) -> Unit
) {

    val navigator = LocalNavigator.currentOrThrow

    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    var passwordConfirmationHidden by rememberSaveable { mutableStateOf(true) }

    when (registrationUiState.event) {
        RegistrationEvent.NONE -> Unit
        RegistrationEvent.SUCCESS -> Unit
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primaryContainer,
                    titleContentColor = colorScheme.primary,
                ),
                title = { Text("Registration") },
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            "Navigate back"
                        )
                    }
                })
        }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth()
                        .semantics {
                            if (registrationUiState.emailError.isNotEmpty()) error(
                                registrationUiState.emailError
                            )
                        },
                    isError = registrationUiState.emailError.isNotEmpty(),
                    value = registrationUiState.email,
                    onValueChange = { onRegistrationAction(RegistrationAction.EmailChanged(it)) },
                    singleLine = true,
                    label = { Text(if (registrationUiState.emailError.isNotEmpty()) "email*" else "email") },
                    placeholder = { Text("example@provider.com") },
                    supportingText = {
                        if (registrationUiState.emailError.isNotEmpty()) Text(registrationUiState.emailError)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
                TextField(
                    modifier = Modifier.fillMaxWidth()
                        .semantics {
                            if (registrationUiState.usernameError.isNotEmpty()) error(
                                registrationUiState.usernameError
                            )
                        },
                    isError = registrationUiState.usernameError.isNotEmpty(),
                    value = registrationUiState.username,
                    onValueChange = { onRegistrationAction(RegistrationAction.UsernameChanged(it)) },
                    singleLine = true,
                    label = { Text(if (registrationUiState.usernameError.isNotEmpty()) "username*" else "username") },
                    supportingText = {
                        if (registrationUiState.usernameError.isNotEmpty()) Text(registrationUiState.usernameError)
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                TextField(
                    modifier = Modifier.fillMaxWidth()
                        .semantics {
                            if (registrationUiState.passwordError.isNotEmpty()) error(
                                registrationUiState.passwordError
                            )
                        },
                    value = registrationUiState.password,
                    onValueChange = { onRegistrationAction(RegistrationAction.PasswordChanged(it)) },
                    isError = registrationUiState.passwordError.isNotEmpty(),
                    label = { Text(if (registrationUiState.passwordError.isNotEmpty()) "password*" else "password") },
                    supportingText = { Text(registrationUiState.passwordError.ifEmpty { "min length is 8 and should have at least one number and letter" }) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                    trailingIcon = {
                        IconButton(onClick = { passwordHidden = !passwordHidden }) {
                            val visibilityIcon =
                                if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description =
                                if (passwordHidden) "Show password" else "Hide password"
                            Icon(imageVector = visibilityIcon, contentDescription = description)
                        }
                    }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth().semantics {
                        if (registrationUiState.passwordConfirmationError.isNotEmpty()) error(
                            registrationUiState.passwordConfirmationError
                        )
                    },
                    value = registrationUiState.passwordConfirmation,
                    onValueChange = {
                        onRegistrationAction(
                            RegistrationAction.PasswordConfirmationChanged(
                                it
                            )
                        )
                    },
                    isError = registrationUiState.passwordConfirmationError.isNotEmpty(),
                    label = { Text(if (registrationUiState.passwordConfirmationError.isNotEmpty()) "confirm password*" else "confirm password") },
                    supportingText = {
                        if (registrationUiState.passwordConfirmationError.isNotEmpty()) Text(
                            registrationUiState.passwordConfirmationError
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = if (passwordConfirmationHidden) PasswordVisualTransformation() else VisualTransformation.None,
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordConfirmationHidden = !passwordConfirmationHidden
                        }) {
                            val visibilityIcon =
                                if (passwordConfirmationHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description =
                                if (passwordConfirmationHidden) "Show password confirmation" else "Hide password confirmation"
                            Icon(imageVector = visibilityIcon, contentDescription = description)
                        }
                    },
                    keyboardActions = KeyboardActions(onDone = { defaultKeyboardAction(ImeAction.Done) })
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !registrationUiState.isLoading,
                    onClick = { onRegistrationAction(RegistrationAction.CreateAccount) }) {
                    if (registrationUiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(with(LocalDensity.current) { MaterialTheme.typography.bodyMedium.fontSize.toDp() })
                        )
                    } else {
                        Text("Create an account", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}