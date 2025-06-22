package com.example.emtyapp.ui.product.screens

import com.google.accompanist.flowlayout.FlowRow
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.product.ProductIntent
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.ProductsList
import com.example.myapplication.data.Entities.Product

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    viewModel: ProductViewModel = viewModel(),
    onNavigateToDetails: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    val types = listOf("ROSE", "TULIP", "LILY", "ORCHID", "HIBISCUS", "LAVENDER", "DAISY", "PANSY")
    val colors = listOf("RED", "WHITE", "YELLOW", "PINK", "PURPLE", "MULTICOLOR")
    val occasions = listOf("LOVE", "BIRTHDAY", "WEDDING", "ANNIVERSARY", "APOLOGY", "THANKS")

    var tempSelectedType by remember { mutableStateOf<String?>(null) }
    var tempSelectedOccasion by remember { mutableStateOf<String?>(null) }
    var tempSelectedColors by remember { mutableStateOf(setOf<String>()) }
    var tempPriceMin by remember { mutableStateOf("") }
    var tempPriceMax by remember { mutableStateOf("") }

    var appliedType by remember { mutableStateOf<String?>(null) }
    var appliedOccasion by remember { mutableStateOf<String?>(null) }
    var appliedColors by remember { mutableStateOf(setOf<String>()) }
    var appliedPriceRange by remember { mutableStateOf(0f..400f) }

    // *État favoris — IDs des produits favoris*
    var favoriteProductIds by remember { mutableStateOf(setOf<String>()) }

    // Fonction pour ajouter/retirer un produit des favoris
    val toggleFavorite: (Product) -> Unit = { product ->
        favoriteProductIds = if (favoriteProductIds.contains(product.id)) {
            favoriteProductIds - product.id
        } else {
            favoriteProductIds + product.id
        }
    }

    LaunchedEffect(Unit) {
        viewModel.handleIntent(ProductIntent.LoadProducts)
    }

    fun parsePriceRange(minStr: String, maxStr: String): ClosedFloatingPointRange<Float> {
        val min = minStr.toFloatOrNull() ?: 0f
        val max = maxStr.toFloatOrNull() ?: 400f
        return if (min <= max) min..max else max..min
    }

    val filteredProducts = state.products?.filter { product ->
        val matchesSearch = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)

        val matchesType = appliedType == null || product.type.equals(appliedType, ignoreCase = true)
        val matchesOccasion = appliedOccasion == null || product.occasions.any { it.equals(appliedOccasion, ignoreCase = true) }
        val matchesColor = appliedColors.isEmpty() || product.colors.any { appliedColors.contains(it.uppercase()) }

        val price = product.price.replace("DH", "", true)
            .trim()
            .replace(" ", "")
            .replace(",", ".")
            .toFloatOrNull() ?: 0f

        val matchesPrice = price in appliedPriceRange

        matchesSearch && matchesType && matchesOccasion && matchesColor && matchesPrice
    } ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌸 Flora Boutique", color = Color(0xFFDC4C3E), fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF8F0))
            )
        },
        containerColor = Color(0xFFFFFBF7)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("🔍 Rechercher...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
                FilterChip(
                    selected = showFilters,
                    onClick = { showFilters = !showFilters },
                    label = { Text("Filtres") },
                    leadingIcon = if (showFilters) {
                        { Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.rotate(180f)) }
                    } else {
                        { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDC4C3E).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFFDC4C3E)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = showFilters,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    var selectedFilterTab by remember { mutableStateOf<String?>(null) }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Type", "Couleur", "Occasion").forEach { tab ->
                            FilterChip(
                                selected = selectedFilterTab == tab,
                                onClick = {
                                    selectedFilterTab = if (selectedFilterTab == tab) null else tab
                                },
                                label = { Text(tab) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFDC4C3E).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFFDC4C3E)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (selectedFilterTab) {
                        "Type" -> {
                            FlowRow(
                                mainAxisSpacing = 8.dp,
                                crossAxisSpacing = 8.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                types.forEach { type ->
                                    FilterChip(
                                        selected = tempSelectedType == type,
                                        onClick = {
                                            tempSelectedType = if (tempSelectedType == type) null else type
                                        },
                                        label = { Text(type) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFDC4C3E).copy(alpha = 0.2f),
                                            selectedLabelColor = Color(0xFFDC4C3E)
                                        )
                                    )
                                }
                            }
                        }
                        "Couleur" -> {
                            FlowRow(
                                mainAxisSpacing = 8.dp,
                                crossAxisSpacing = 8.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                colors.forEach { color ->
                                    FilterChip(
                                        selected = tempSelectedColors.contains(color),
                                        onClick = {
                                            tempSelectedColors = if (tempSelectedColors.contains(color)) {
                                                tempSelectedColors - color
                                            } else {
                                                tempSelectedColors + color
                                            }
                                        },
                                        label = { Text(color) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFDC4C3E).copy(alpha = 0.2f),
                                            selectedLabelColor = Color(0xFFDC4C3E)
                                        )
                                    )
                                }
                            }
                        }
                        "Occasion" -> {
                            FlowRow(
                                mainAxisSpacing = 8.dp,
                                crossAxisSpacing = 8.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                occasions.forEach { occasion ->
                                    FilterChip(
                                        selected = tempSelectedOccasion == occasion,
                                        onClick = {
                                            tempSelectedOccasion = if (tempSelectedOccasion == occasion) null else occasion
                                        },
                                        label = { Text(occasion) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFDC4C3E).copy(alpha = 0.2f),
                                            selectedLabelColor = Color(0xFFDC4C3E)
                                        )
                                    )
                                }
                            }
                        }
                        null -> {
                            Text(
                                "Sélectionnez un filtre pour afficher les options",
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = tempPriceMin,
                            onValueChange = { newValue -> tempPriceMin = newValue.filter { it.isDigit() || it == '.' } },
                            label = { Text("Prix Min") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tempPriceMax,
                            onValueChange = { newValue -> tempPriceMax = newValue.filter { it.isDigit() || it == '.' } },
                            label = { Text("Prix Max") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = {
                            tempSelectedType = null
                            tempSelectedOccasion = null
                            tempSelectedColors = emptySet()
                            tempPriceMin = ""
                            tempPriceMax = ""
                        }) {
                            Text("Réinitialiser", color = Color(0xFFDC4C3E))
                        }
                        Button(onClick = {
                            appliedType = tempSelectedType
                            appliedOccasion = tempSelectedOccasion
                            appliedColors = tempSelectedColors
                            appliedPriceRange = parsePriceRange(tempPriceMin, tempPriceMax)
                            showFilters = false
                        }) {
                            Text("Appliquer")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                when {
                    state.isLoading -> Center { CircularProgressIndicator(color = Color(0xFFDC4C3E)) }
                    state.error != null -> Center { Text("Erreur: ${state.error}", color = Color.Red) }
                    filteredProducts.isEmpty() -> Center { Text("Aucun produit trouvé") }
                    else -> ProductsList(
                        products = filteredProducts,
                        onNavigateToDetails = onNavigateToDetails,
                        favoriteProductIds = favoriteProductIds,
                        onToggleFavorite = toggleFavorite
                    )
                }
            }
        }
    }
}

@Composable
fun Center(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = content
    )
}