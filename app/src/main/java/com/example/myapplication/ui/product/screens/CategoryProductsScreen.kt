package com.example.myapplication.ui.product.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.navigator.Routes
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.ProductsList
import com.example.myapplication.ui.product.component.QuickFilter
import com.example.myapplication.ui.theme.LocalThemeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    lang: LanguageManager.Instance,
    viewModel: ProductViewModel,
    category: String,
    onNavigateToDetails: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigateCart: () -> Unit,
    onNavigateCategories: () -> Unit,
    currentRoute: String = Routes.CategoryProducts
) {
    val themeState = LocalThemeState.current

    val state by viewModel.state.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    var selectedQuickFilter by remember { mutableStateOf<QuickFilter?>(null) }

    val inCategory = state.products.filter { it.category.equals(category, true) }

    val productsToShow = when (selectedQuickFilter) {
        QuickFilter.GIFT -> inCategory.filter { it.category.equals("GIFT", true) }
        QuickFilter.MULTICOLOR -> inCategory.filter { "MULTICOLOR" in it.colors }
        QuickFilter.BASKET -> inCategory.filter {
            it.description.contains("panier", true) ||
                    it.description.contains("arrangement", true)
        }
        null -> inCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🌸 $category",
                        color = MaterialTheme.colorScheme.primary ,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                NavigationBarItem(
                    selected = currentRoute == Routes.Home,
                    onClick = onNavigateHome,
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text(lang.get("home")) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateCategories,
                    icon = { Text("🪷", fontSize = 20.sp) },
                    label = { Text(lang.get("categories")) }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Favorites,
                    onClick = onNavigateFavorites,
                    icon = { Text("❤", fontSize = 20.sp) },
                    label = { Text(lang.get("favorites")) }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Cart,
                    onClick = onNavigateCart,
                    icon = { Text("🛒", fontSize = 20.sp) },
                    label = { Text(lang.get("cart")) }
                )
            }
        }
    ) { padding ->

        if (productsToShow.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    lang.get("no_products"),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                ProductsList(
                    products = productsToShow,
                    favoriteProductIds = favoriteIds,
                    selectedQuickFilter = selectedQuickFilter,
                    onQuickFilterSelected = { selectedQuickFilter = it },
                    onNavigateToDetails = onNavigateToDetails,
                    onToggleFavorite = viewModel::toggleFavorite,
                    lang = lang,
                    onRateProduct = { id, rate ->
                        viewModel.updateProductRating(id, rate)
                    }
                )
            }
        }
    }
}
