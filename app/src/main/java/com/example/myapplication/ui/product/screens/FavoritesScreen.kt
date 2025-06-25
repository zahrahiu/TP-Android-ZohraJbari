package com.example.myapplication.ui.product.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.ProductsList
import com.example.myapplication.ui.product.component.QuickFilter
import com.example.myapplication.navigator.Routes
import com.example.myapplication.ui.theme.LocalThemeState
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FavoritesScreen(
    viewModel: ProductViewModel = viewModel(),
    onNavigateToDetails: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToCategory: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val themeState = LocalThemeState.current
    val state by viewModel.state.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedQuickFilter by remember { mutableStateOf<QuickFilter?>(null) }

    val types = listOf("ROSE","TULIP","LILY","ORCHID","HIBISCUS","LAVENDER","DAISY","PANSY")
    val colors = listOf("RED","WHITE","YELLOW","PINK","PURPLE","MULTICOLOR")
    val occasions = listOf("LOVE","BIRTHDAY","WEDDING","ANNIVERSARY","APOLOGY","THANKS")

    var tType by remember { mutableStateOf<String?>(null) }
    var tOccasion by remember { mutableStateOf<String?>(null) }
    var tColors by remember { mutableStateOf(setOf<String>()) }
    var pMin by remember { mutableStateOf("") }
    var pMax by remember { mutableStateOf("") }

    var aType by remember { mutableStateOf<String?>(null) }
    var aOccasion by remember { mutableStateOf<String?>(null) }
    var aColors by remember { mutableStateOf(setOf<String>()) }
    var aPriceRange by remember { mutableStateOf(0f..400f) }

    fun parsePrice(min: String, max: String): ClosedFloatingPointRange<Float> {
        val mn = min.toFloatOrNull() ?: 0f
        val mx = max.toFloatOrNull() ?: 400f
        return if (mn <= mx) mn..mx else mx..mn
    }

    val favProducts = state.products.filter { it.id in favoriteIds }

    val filtered = favProducts.filter { p ->
        val matchSearch = searchQuery.isBlank() ||
                p.name.contains(searchQuery, true) ||
                p.description.contains(searchQuery, true)

        val matchType = aType == null || p.type.equals(aType, true)
        val matchOcc = aOccasion == null || p.occasions.any { it.equals(aOccasion, true) }
        val matchColor = aColors.isEmpty() || p.colors.any { it.uppercase() in aColors }

        val price = p.price
            .replace("DH", "", true)
            .replace(" ", "")
            .replace(",", ".")
            .toFloatOrNull() ?: 0f
        val matchPrice = price in aPriceRange

        matchSearch && matchType && matchOcc && matchColor && matchPrice
    }

    val productsToShow = when (selectedQuickFilter) {
        QuickFilter.GIFT -> filtered.filter { it.category.equals("GIFT", true) }
        QuickFilter.MULTICOLOR -> filtered.filter { "MULTICOLOR" in it.colors }
        QuickFilter.BASKET -> filtered.filter {
            it.description.contains("panier", true) ||
                    it.description.contains("arrangement", true)
        }
        null -> filtered
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("❤️ Mes Favoris", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCategory,
                    icon = { Text("🪷", fontSize = 20.sp) },
                    label = { Text("Catégories") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = onNavigateToFavorites,
                    icon = { Text("❤", fontSize = 20.sp) },
                    label = { Text("Favoris") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCart,
                    icon = { Text("🛒", fontSize = 20.sp) },
                    label = { Text("Panier") }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(8.dp))

            Box(Modifier.weight(1f)) {
                when {
                    state.isLoading -> Center { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                    favProducts.isEmpty() -> Center { Text("Aucun produit favori pour le moment.", color = MaterialTheme.colorScheme.onBackground) }
                    productsToShow.isEmpty() -> Center { Text("Filtre vide", color = MaterialTheme.colorScheme.onBackground) }
                    else -> ProductsList(
                        products = productsToShow,
                        favoriteProductIds = favoriteIds,
                        selectedQuickFilter = selectedQuickFilter,
                        onQuickFilterSelected = { selectedQuickFilter = it },
                        onNavigateToDetails = onNavigateToDetails,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onRateProduct = { id, rate ->
                            viewModel.updateProductRating(id, rate)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Center(content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center, content = content)
}
