package com.example.emtyapp.ui.product.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.product.ProductIntent
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.ProductsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ProductViewModel = viewModel(),
    onNavigateToDetails: (String) -> Unit
) {
    // State from ViewModel
    val state by viewModel.state.collectAsState()

    // Search state
    var searchQuery by remember { mutableStateOf("") }

    // Filter states
    var priceFilter by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var availabilityFilter by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }

    // Load products on first launch
    LaunchedEffect(Unit) {
        viewModel.handleIntent(ProductIntent.LoadProducts)
    }

    // Filter products based on search and filters
    val filteredProducts = state.products.filter { product ->
        // Search filter
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)

        // Price filter
        val matchesPrice = priceFilter?.let { (min, max) ->
            val price = product.price.removeSuffix(" DH").toIntOrNull() ?: 0
            price in min..max
        } ?: true

        // Availability filter
        val matchesAvailability = if (availabilityFilter) {
            product.quantity.toIntOrNull() ?: 0 > 0
        } else true

        matchesSearch && matchesPrice && matchesAvailability
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🌸 Flora Boutique",
                        color = Color(0xFFDC4C3E),
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF8F0)
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("🏡", fontSize = 22.sp) },
                    label = { Text("Accueil") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("❤️", fontSize = 22.sp) },
                    label = { Text("Favoris") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("🛒", fontSize = 22.sp) },
                    label = { Text("Panier") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("👩", fontSize = 22.sp) },
                    label = { Text("Profil") }
                )
            }
        },
        containerColor = Color(0xFFFFFBF7),
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Welcome section
                Text(
                    text = "Bienvenue, 🌷",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037)
                )

                Text(
                    text = "Trouve ta fleur préférée !",
                    fontSize = 16.sp,
                    color = Color(0xFF8D6E63),
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("🔍 Rechercher une fleur...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFDC4C3E),
                        focusedBorderColor = Color(0xFFDC4C3E),
                        cursorColor = Color(0xFFDC4C3E)
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                // Filters button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { showFilters = !showFilters },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDC4C3E),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(if (showFilters) "Masquer filtres" else "Afficher filtres")
                    }
                }

                // Filters section
                if (showFilters) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Filtrer par:",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Price filter
                        Text("Prix:", fontWeight = FontWeight.Medium)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            listOf(0 to 200, 201 to 300, 301 to 400).forEach { range ->
                                FilterChip(
                                    selected = priceFilter == range,
                                    onClick = {
                                        priceFilter = if (priceFilter == range) null else range
                                    },
                                    modifier = Modifier.padding(end = 8.dp),
                                    label = { Text("${range.first}-${range.second} DH") }
                                )
                            }
                        }

                        // Availability filter
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text("Disponibilité:", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = availabilityFilter,
                                onClick = { availabilityFilter = !availabilityFilter },
                                label = { Text("En stock seulement") }
                            )
                        }

                        // Clear filters button
                        if (priceFilter != null || availabilityFilter) {
                            TextButton(
                                onClick = {
                                    priceFilter = null
                                    availabilityFilter = false
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Réinitialiser les filtres", color = Color(0xFFDC4C3E))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Products list or loading/error states
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFDC4C3E))
                        }
                    }

                    state.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Erreur : ${state.error}",
                                color = Color.Red
                            )
                        }
                    }

                    else -> {
                        ProductsList(
                            products = filteredProducts,
                            onNavigateToDetails = onNavigateToDetails
                        )

                        if (filteredProducts.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Aucun produit trouvé",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF5D4037),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        "Essayez de modifier vos critères de recherche",
                                        color = Color(0xFF8D6E63)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}