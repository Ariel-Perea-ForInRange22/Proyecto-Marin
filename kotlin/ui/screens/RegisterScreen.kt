package com.devcore.uat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcore.uat.ui.theme.UATColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UATColors.GrisClaro)
    ) {
        // Header
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Crear Cuenta",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(
                        text = "Únete a la comunidad UAT",
                        style = MaterialTheme.typography.bodyMedium,
                        color = UATColors.Blanco.copy(alpha = 0.9f)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = UATColors.Blanco
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = UATColors.AzulPastel,
                titleContentColor = UATColors.Blanco
            )
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Alert Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = UATColors.AzulClaro
                )
            ) {
                Row(
                    modifier = Modifier.padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📋", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Validación Institucional",
                            style = MaterialTheme.typography.labelLarge,
                            color = UATColors.Negro
                        )
                        Text(
                            text = "Tu cuenta será validada por el personal administrativo en menos de 24 horas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = UATColors.TextoSecundario
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Form Fields
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                placeholder = { Text("Ingresa tu nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            OutlinedTextField(
                value = matricula,
                onValueChange = { matricula = it },
                label = { Text("Matrícula") },
                placeholder = { Text("Ej: 2021370001") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Institucional (@uat.edu.mx)") },
                placeholder = { Text("ejemplo@uat.edu.mx") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                placeholder = { Text("Mínimo 8 caracteres") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                placeholder = { Text("Repite tu contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // File Upload Placeholder
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = UATColors.GrisClaro
                )
            ) {
                Column(
                    modifier = Modifier.padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📤", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Clic para subir tu ficha de pago",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "PDF o imagen (max 5MB)",
                        style = MaterialTheme.typography.bodySmall,
                        color = UATColors.TextoSecundario
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Terms Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it }
                )
                Text(
                    text = "Acepto los términos y condiciones",
                    style = MaterialTheme.typography.bodyMedium,
                    color = UATColors.TextoSecundario
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Register Button
            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UATColors.NaranjaPastel
                )
            ) {
                Text(text = "Registrarse", fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Login Button
            OutlinedButton(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = UATColors.AzulPastel
                )
            ) {
                Text(text = "Ya tengo cuenta", fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
