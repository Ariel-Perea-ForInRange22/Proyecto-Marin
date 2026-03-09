package com.devcore.uat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcore.uat.ui.theme.UATColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UATColors.GrisClaro)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Estado de Cuenta",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(
                        text = "Verificación en proceso",
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
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                UATColors.AmarilloPastel,
                                UATColors.NaranjaPastel
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⏳", fontSize = 60.sp)
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Text(
                text = "Cuenta en Revisión",
                style = MaterialTheme.typography.displayMedium,
                color = UATColors.AzulPastel
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            Text(
                text = "Tu registro ha sido recibido exitosamente. El personal administrativo está verificando tu documentación.",
                textAlign = TextAlign.Center,
                color = UATColors.TextoSecundario
            )
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF8E1)
                )
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "⚠️ Acceso Restringido",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF9C7A3C)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "No podrás acceder a las funciones de la plataforma hasta que tu cuenta sea validada.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9C7A3C)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            LinearProgressIndicator(
                progress = 0.5f,
                modifier = Modifier.fillMaxWidth(),
                color = UATColors.NaranjaPastel
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = "Progreso: 50%",
                style = MaterialTheme.typography.bodySmall,
                color = UATColors.TextoSecundario
            )
        }
    }
}
