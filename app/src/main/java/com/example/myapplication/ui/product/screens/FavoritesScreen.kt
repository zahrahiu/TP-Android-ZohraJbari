/*  ────────────────────────  FavoritesScreen.kt  ───────────────────────── */
package com.example.myapplication.ui.product.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.navigator.Routes
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.ProductsList
import com.example.myapplication.ui.product.component.QuickFilter
import com.example.myapplication.ui.theme.LocalThemeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FavoritesScreen(
    viewModel: ProductViewModel = viewModel(),
    lang: LanguageManager.Instance,
    onNavigateToDetails: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigateCategory: () -> Unit,
    onNavigateCart: () -> Unit,
    currentRoute: String = Routes.Favorites
) {
    val themeState = LocalThemeState.current

    /*  VM state  */
    val state       by viewModel.state.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    /*  UI state  */
    var selectedQuickFilter by remember { mutableStateOf<QuickFilter?>(null) }

    /*  products list  */
    val favProducts = state.products.filter { it.id in favoriteIds }
    val productsToShow = when (selectedQuickFilter) {
        QuickFilter.GIFT       -> favProducts.filter { it.category.equals("GIFT", true) }
        QuickFilter.MULTICOLOR -> favProducts.filter { "MULTICOLOR" in it.colors }
        QuickFilter.BASKET     -> favProducts.filter {
            it.description.contains("panier", true) ||
                    it.description.contains("arrangement", true)
        }
        null -> favProducts
    }

    /* ───────────────────────────── UI ───────────────────────────── */
    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("❤  ${lang.get("favorites")}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                NavigationBarItem(
                    selected = currentRoute == Routes.Home,
                    onClick  = onNavigateHome,
                    icon     = { Text("🏠", fontSize = 20.sp) },
                    label    = { Text(lang.get("home")) }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.CategorySelection,
                    onClick  = onNavigateCategory,
                    icon     = { Text("🪷", fontSize = 20.sp) },
                    label    = { Text(lang.get("categories")) }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Favorites,
                    onClick  = onNavigateFavorites,
                    icon     = { Text("❤", fontSize = 20.sp) },
                    label    = { Text(lang.get("favorites")) }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Cart,
                    onClick  = onNavigateCart,
                    icon     = { Text("🛒", fontSize = 20.sp) },
                    label    = { Text(lang.get("cart")) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            when {
                state.isLoading -> Center {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                favProducts.isEmpty() -> Center {
                    Text(lang.get("no_products"))
                }
                productsToShow.isEmpty() -> Center {
                    Text(lang.get("select_filter"))
                }
                else -> ProductsList(
                    products              = productsToShow,
                    favoriteProductIds    = favoriteIds,
                    selectedQuickFilter   = selectedQuickFilter,
                    onQuickFilterSelected = { selectedQuickFilter = it },
                    onNavigateToDetails   = onNavigateToDetails,
                    onToggleFavorite      = viewModel::toggleFavorite,
                    lang = lang,

                    onRateProduct         = { id, rating ->
                        viewModel.updateProductRating(id, rating)
                    }
                )
            }
        }
    }
}


@Composable
fun Center(content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center, content = content)
}
