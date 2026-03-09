package com.devcore.uat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun HomeScreen(
    onBusTrackingClick: () -> Unit,
    onMarketplaceClick: () -> Unit,
    onProfileClick: () -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "¡Hola, Estudiante! 👋",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            text = "Bienvenido a DevCore",
                            style = MaterialTheme.typography.bodyMedium,
                            color = UATColors.Blanco.copy(alpha = 0.9f)
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
            NavigationBar(
                containerColor = UATColors.Blanco
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = { Icon(Icons.Default.Home, "Inicio") },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = UATColors.NaranjaPastel,
                        selectedTextColor = UATColors.NaranjaPastel
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { 
                        onTabSelected(1)
                        onBusTrackingClick()
                    },
                    icon = { Text("🚌", fontSize = 20.sp) },
                    label = { Text("Bus") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = UATColors.NaranjaPastel,
                        selectedTextColor = UATColors.NaranjaPastel
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { 
                        onTabSelected(2)
                        onMarketplaceClick()
                    },
                    icon = { Text("🛍️", fontSize = 20.sp) },
                    label = { Text("Market") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = UATColors.NaranjaPastel,
                        selectedTextColor = UATColors.NaranjaPastel
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { 
                        onTabSelected(3)
                        onProfileClick()
                    },
                    icon = { Icon(Icons.Default.Person, "Perfil") },
                    label = { Text("Perfil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = UATColors.NaranjaPastel,
                        selectedTextColor = UATColors.NaranjaPastel
                    )
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
            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = UATColors.AzulPastel
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "85",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = UATColors.Blanco
                        )
                        Text(
                            text = "Nivel de Confianza",
                            fontSize = 12.sp,
                            color = UATColors.Blanco
                        )
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = UATColors.NaranjaPastel
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "7",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = UATColors.Blanco
                        )
                        Text(
                            text = "Racha Días",
                            fontSize = 12.sp,
                            color = UATColors.Blanco
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Modules Section
            Text(
                text = "Módulos Principales",
                style = MaterialTheme.typography.titleLarge,
                color = UATColors.AzulPastel
            )
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Bus Tracking Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onBusTrackingClick,
                colors = CardDefaults.cardColors(
                    containerColor = UATColors.Blanco
                )
            ) {
                Row(
                    modifier = Modifier.padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        UATColors.AzulPastel,
                                        UATColors.NaranjaPastel
                                    )
                                ),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🚌", fontSize = 30.sp)
                    }
                    
                    Spacer(modifier = Modifier.width(15.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bus Tracking",
                            style = MaterialTheme.typography.titleLarge,
                            color = UATColors.AzulPastel
                        )
                        Text(
                            text = "Reporta el estado del camión en tiempo real",
                            style = MaterialTheme.typography.bodySmall,
                            color = UATColors.TextoSecundario
                        )
                    }
                    
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = UATColors.NaranjaPastel
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Marketplace Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onMarketplaceClick,
                colors = CardDefaults.cardColors(
                    containerColor = UATColors.Blanco
                )
            ) {
                Row(
                    modifier = Modifier.padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        UATColors.NaranjaPastel,
                                        UATColors.AmarilloPastel
                                    )
                                ),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛍️", fontSize = 30.sp)
                    }
                    
                    Spacer(modifier = Modifier.width(15.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Marketplace",
                            style = MaterialTheme.typography.titleLarge,
                            color = UATColors.AzulPastel
                        )
                        Text(
                            text = "Compra y vende artículos universitarios",
                            style = MaterialTheme.typography.bodySmall,
                            color = UATColors.TextoSecundario
                        )
                    }
                    
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = UATColors.NaranjaPastel
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onProfileClick,
                colors = CardDefaults.cardColors(
                    containerColor = UATColors.Blanco
                )
            ) {
                Row(
                    modifier = Modifier.padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        UATColors.VerdePastel,
                                        Color(0xFFAED581)
                                    )
                                ),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏆", fontSize = 30.sp)
                    }
                    
                    Spacer(modifier = Modifier.width(15.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mi Perfil",
                            style = MaterialTheme.typography.titleLarge,
                            color = UATColors.AzulPastel
                        )
                        Text(
                            text = "Ver reputación, rachas y logros",
                            style = MaterialTheme.typography.bodySmall,
                            color = UATColors.TextoSecundario
                        )
                    }
                    
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = UATColors.NaranjaPastel
                    )
                }
            }
        }
    }
}
