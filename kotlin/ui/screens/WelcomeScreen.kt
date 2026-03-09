package com.devcore.uat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcore.uat.ui.theme.UATColors

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        UATColors.AzulPastel,
                        UATColors.NaranjaPastel
                    )
                )
            )
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Placeholder
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(UATColors.Blanco, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎓",
                    fontSize = 60.sp
                )
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // App Name
            Text(
                text = "DevCore",
                fontSize = 36.sp,
                color = UATColors.Blanco,
                style = MaterialTheme.typography.displayLarge
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = "Workshop Discovery Value",
                fontSize = 16.sp,
                color = UATColors.Blanco.copy(alpha = 0.9f)
            )
            
            Spacer(modifier = Modifier.height(50.dp))
            
            Text(
                text = "Tu plataforma universitaria integral",
                fontSize = 14.sp,
                color = UATColors.Blanco.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.width(280.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Buttons
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UATColors.NaranjaPastel
                )
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = UATColors.Blanco
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(UATColors.Blanco, UATColors.Blanco)
                    )
                )
            ) {
                Text(
                    text = "Registrarse",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Footer
            Text(
                text = "© 2026 DevCore Team\nUniversidad Autónoma de Tamaulipas",
                fontSize = 12.sp,
                color = UATColors.Blanco.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
