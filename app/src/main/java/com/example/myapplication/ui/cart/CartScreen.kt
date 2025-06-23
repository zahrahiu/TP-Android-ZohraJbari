package com.example.myapplication.ui.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import kotlinx.coroutines.launch

val RougeFlora = Color(0xFFDC4C3E)      // Red-like color from HomeScreen
val BeigeFlora = Color(0xFFFFF8F0)      // Light beige background
val GrisPetitGris = Color(0xFF8E8E93)   // Gray calm color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val items = viewModel.items.collectAsState().value
    val snackHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun parsePrice(priceStr: String): Float {
        return priceStr.replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0f
    }

    val totalPrice = items.sumOf { ci ->
        val productPrice = parsePrice(ci.product.price).toDouble()
        val addonsPrice = ci.addons.sumOf { it.addon.price.toDouble() * it.quantity }
        productPrice * ci.quantity + addonsPrice
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mon Panier", color = RougeFlora, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BeigeFlora)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = BeigeFlora) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToFavorites,
                    icon = { Text("❤", fontSize = 20.sp) },
                    label = { Text("Favoris") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = onNavigateToCart,
                    icon = { Text("🛒", fontSize = 20.sp) },
                    label = { Text("Panier") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackHostState) },
        containerColor = BeigeFlora
    ) { pad ->

        if (items.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(pad),
                contentAlignment = Alignment.Center
            ) {
                Text("Panier vide", color = GrisPetitGris, fontWeight = FontWeight.Medium)
            }
            return@Scaffold
        }

        // Main Column with gradient background
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BeigeFlora,
                            RougeFlora.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(items) { ci ->
                    val maxStock = ci.product.quantity.toIntOrNull() ?: Int.MAX_VALUE
                    val outOfStock = ci.quantity >= maxStock

                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            // Produit principal
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painterResource(R.drawable.img1),
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp)
                                )
                                Column(
                                    Modifier
                                        .weight(1f)
                                        .padding(start = 12.dp)
                                ) {
                                    Text(ci.product.name, color = GrisPetitGris, fontWeight = FontWeight.SemiBold)
                                    Text(ci.product.price, color = GrisPetitGris)
                                    if (outOfStock)
                                        Text(
                                            "Stock insuffisant",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { viewModel.dec(ci) }) {
                                        Icon(Icons.Filled.Remove, contentDescription = "Décrementer")
                                    }
                                    Text("${ci.quantity}", color = GrisPetitGris, modifier = Modifier.width(24.dp), fontWeight = FontWeight.Bold)
                                    IconButton(
                                        onClick = {
                                            val ok = viewModel.inc(ci)
                                            if (!ok) scope.launch {
                                                snackHostState.showSnackbar("Stock insuffisant pour ${ci.product.name}")
                                            }
                                        },
                                        enabled = !outOfStock
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = "Incrémenter")
                                    }
                                }
                            }

                            // Addons
                            ci.addons.forEach { aq ->
                                Row(
                                    Modifier
                                        .padding(start = 40.dp, top = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painterResource(aq.addon.imageRes),
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        aq.addon.name,
                                        Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp),
                                        color = GrisPetitGris
                                    )
                                    IconButton(onClick = { viewModel.decAddon(ci, aq) }) {
                                        Icon(Icons.Filled.Remove, contentDescription = "Décrementer addon")
                                    }
                                    Text("${aq.quantity}", color = GrisPetitGris, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { viewModel.incAddon(ci, aq) }) {
                                        Icon(Icons.Filled.Add, contentDescription = "Incrémenter addon")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Prix total with red-ish card background
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = RougeFlora.copy(alpha = 0.8f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Prix Total", fontWeight = FontWeight.Bold, color = BeigeFlora, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                    Text(String.format("%.2f DH", totalPrice), fontWeight = FontWeight.Bold, color = BeigeFlora, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                }
            }
        }
    }
}
