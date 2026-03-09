package com.devcore.uat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcore.uat.ui.theme.UATColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
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
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(
                        text = "Accede a tu cuenta",
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
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            
            // Logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                UATColors.AzulPastel,
                                UATColors.NaranjaPastel
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎓", fontSize = 50.sp)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Institucional") },
                placeholder = { Text("ejemplo@uat.edu.mx") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                placeholder = { Text("Ingresa tu contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Forgot Password
            TextButton(
                onClick = { },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = UATColors.NaranjaPastel
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Login Button
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UATColors.NaranjaPastel
                )
            ) {
                Text(text = "Entrar", fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "o",
                color = UATColors.GrisPastel,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Register Button
            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = UATColors.AzulPastel
                )
            ) {
                Text(text = "Crear Cuenta Nueva", fontSize = 16.sp)
            }
        }
    }
}
