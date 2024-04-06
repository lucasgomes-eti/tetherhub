package login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import registration.RegistrationScreen

@Composable
fun Login(
    loginUiState: LoginUiState,
    onLoginAction: (LoginAction) -> Unit
) {

    val navigator = LocalNavigator.currentOrThrow
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    when (loginUiState.event) {
        LoginEvent.NONE -> Unit
        LoginEvent.SUCCESS -> TODO("Navigate to home")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Login",
                style = typography.displaySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = loginUiState.email,
                onValueChange = { onLoginAction(LoginAction.EmailChanged(it)) },
                isError = loginUiState.errorMsg.isNotEmpty(),
                singleLine = true,
                label = { Text("email") },
                placeholder = { Text("example@provider.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = loginUiState.password,
                onValueChange = { onLoginAction(LoginAction.PasswordChanged(it)) },
                isError = loginUiState.errorMsg.isNotEmpty(),
                singleLine = true,
                label = { Text("password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val visibilityIcon = if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordHidden) "Show password" else "Hide password"
                        Icon(imageVector = visibilityIcon, contentDescription = description)
                    }
                },
                keyboardActions = KeyboardActions(onDone = {
                    onLoginAction(LoginAction.Login)
                    defaultKeyboardAction(ImeAction.Done)
                })
            )
            AnimatedVisibility(visible = loginUiState.errorMsg.isNotEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = loginUiState.errorMsg,
                    textAlign = TextAlign.Left,
                    style = typography.bodyMedium,
                    color = colorScheme.error
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !loginUiState.isLoading,
                onClick = { onLoginAction(LoginAction.Login) }) {
                if (loginUiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(with(LocalDensity.current) { typography.bodyMedium.fontSize.toDp() })
                    )
                } else {
                    Text("Login", style = typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(48.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Don't have an account?", style = typography.bodyLarge)
                OutlinedButton(onClick = { navigator.push(RegistrationScreen) }) {
                    Text("Create an account", style = typography.bodyMedium)
                }
            }
        }
    }
}