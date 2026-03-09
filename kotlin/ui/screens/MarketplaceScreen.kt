package com.devcore.uat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcore.uat.ui.theme.UATColors

data class Product(
    val emoji: String,
    val name: String,
    val price: String,
    val isFavorite: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    onBackClick: () -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val products = listOf(
        Product("📚", "Cálculo Diferencial", "$250"),
        Product("💻", "Calculadora TI-84", "$800"),
        Product("📖", "Física II", "$180", true),
        Product("🎒", "Mochila UAT", "$350"),
        Product("📐", "Juego Geométrico", "$50"),
        Product("🖊️", "Set de Plumas", "$80")
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Marketplace 🛍️",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            text = "Compra y vende",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = UATColors.NaranjaPastel
            ) {
                Icon(Icons.Default.Add, "Agregar producto", tint = UATColors.Blanco)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(UATColors.GrisClaro)
                .padding(15.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar productos...") },
                trailingIcon = {
                    Icon(Icons.Default.Search, "Buscar")
                },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Info Alert
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
                    Text(text = "💰", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Comisión del sistema: 5-10% por transacción",
                        style = MaterialTheme.typography.bodySmall,
                        color = UATColors.Negro
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Product Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(products) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(UATColors.GrisClaro),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = product.emoji, fontSize = 48.sp)
                                if (product.isFavorite) {
                                    Icon(
                                        Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = UATColors.NaranjaPastel,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = UATColors.AzulPastel
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = product.price,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UATColors.NaranjaPastel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
