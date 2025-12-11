package com.eventos.pf.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventos.pf.ui.components.LabeledField
import com.eventos.pf.ui.components.PrimaryButton

@Composable
fun LoginScreen(
    onLogin: (String, String, Boolean) -> Unit
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val rememberMe = remember { mutableStateOf(true) }
    val canSubmit = username.value.isNotBlank() && password.value.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(
                text = "Ingresá a tu cuenta",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Ingresá tus credenciales para continuar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LabeledField(
                label = "Usuario",
                value = username.value,
                onValueChange = { username.value = it },
                placeholder = "email o usuario"
            )
            LabeledField(
                label = "Contraseña",
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = "••••••"
            )
            RememberMeRow(rememberMe)
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PrimaryButton(
                text = "Ingresar",
                enabled = canSubmit
            ) {
                onLogin(username.value, password.value, rememberMe.value)
            }
        }
    }
}

@Composable
private fun RememberMeRow(rememberMe: MutableState<Boolean>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = rememberMe.value,
            onCheckedChange = { rememberMe.value = it }
        )
        Text(
            text = "Recordarme",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

