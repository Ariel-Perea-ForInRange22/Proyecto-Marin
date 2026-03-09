package com.devcore.uat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcore.uat.ui.theme.UATColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mi Perfil 👤",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            text = "Reputación y logros",
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
        },
        bottomBar = {
            NavigationBar(containerColor = UATColors.Blanco) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = { Icon(Icons.Default.Home, "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = { Text("🚌", fontSize = 20.sp) },
                    label = { Text("Bus") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = { Text("🛍️", fontSize = 20.sp) },
                    label = { Text("Market") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = { Icon(Icons.Default.Person, "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(UATColors.GrisClaro)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Profile Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = UATColors.AzulPastel
                )
            ) {
                Column(
                    modifier = Modifier.padding(30.dp, 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(UATColors.Blanco, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎓", fontSize = 50.sp)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "Juan Pérez García",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = UATColors.Blanco
                    )
                    Text(
                        text = "Mat: 2021370001",
                        color = UATColors.Blanco.copy(alpha = 0.9f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(25.dp))
            
            Text(
                text = "Nivel de Confianza",
                style = MaterialTheme.typography.titleLarge,
                color = UATColors.AzulPastel
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "⭐", fontSize = 60.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "85",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = UATColors.NaranjaPastel
                    )
                    Text(
                        text = "Usuario Confiable",
                        color = UATColors.TextoSecundario
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    LinearProgressIndicator(
                        progress = 0.85f,
                        modifier = Modifier.fillMaxWidth(),
                        color = UATColors.NaranjaPastel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "85 de 100 puntos",
                        style = MaterialTheme.typography.bodySmall,
                        color = UATColors.TextoSecundario
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(25.dp))
            
            Text(
                text = "Rachas de Engagement",
                style = MaterialTheme.typography.titleLarge,
                color = UATColors.AzulPastel
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔥", fontSize = 60.sp)
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            Text(
                                text = "7 días",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = UATColors.NaranjaPastel
                            )
                            Text(
                                text = "Racha actual",
                                color = UATColors.TextoSecundario
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(25.dp))
            
            Text(
                text = "Logros Desbloqueados",
                style = MaterialTheme.typography.titleLarge,
                color = UATColors.AzulPastel
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🏅", fontSize = 40.sp)
                        Text(
                            text = "Primer\nReporte",
                            style = MaterialTheme.typography.labelSmall,
                            color = UATColors.TextoSecundario
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔥", fontSize = 40.sp)
                        Text(
                            text = "Racha\n7 días",
                            style = MaterialTheme.typography.labelSmall,
                            color = UATColors.TextoSecundario
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⭐", fontSize = 40.sp)
                        Text(
                            text = "Nivel\n80+",
                            style = MaterialTheme.typography.labelSmall,
                            color = UATColors.TextoSecundario
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Settings, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Configuración")
            }
        }
    }
}
