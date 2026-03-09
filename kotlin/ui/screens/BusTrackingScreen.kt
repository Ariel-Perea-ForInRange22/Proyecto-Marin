package com.devcore.uat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcore.uat.ui.theme.UATColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusTrackingScreen(
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
                            text = "Bus Tracking 🚌",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            text = "Estado en tiempo real",
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
            // Map Placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🗺️", fontSize = 60.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Mapa de rutas UAT", color = UATColors.TextoSecundario)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Reportar Estado",
                style = MaterialTheme.typography.titleLarge,
                color = UATColors.AzulPastel
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UATColors.VerdePastel
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "✅", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Ya Pasó")
                    }
                }
                
                Button(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UATColors.RojoPastel
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "❌", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Ha Pasado")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Text(
                text = "Reportes Recientes",
                style = MaterialTheme.typography.titleLarge,
                color = UATColors.AzulPastel
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✅", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Ruta 1 - Ya pasó",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = UATColors.AzulPastel
                                )
                                Text(
                                    text = "Hace 2 minutos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = UATColors.TextoSecundario
                                )
                            }
                        }
                        AssistChip(
                            onClick = { },
                            label = { Text("12") }
                        )
                    }
                }
            }
        }
    }
}
