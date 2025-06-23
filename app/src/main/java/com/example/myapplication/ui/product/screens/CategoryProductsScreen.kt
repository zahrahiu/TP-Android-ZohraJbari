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
import com.example.emtyapp.nav.Routes
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.ProductsList
import com.example.myapplication.ui.product.component.QuickFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    viewModel: ProductViewModel,
    category: String,
    onNavigateToDetails: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigateCart: () -> Unit,
    onNavigateCategories: () -> Unit,
    currentRoute: String = Routes.CategoryProducts
) {
    val state by viewModel.state.collectAsState()
    val filtered = state.products.filter { it.category.equals(category, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🌸 $category",
                        color = Color(0xFFDC4C3E),
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF8F0))
            )
        },
        containerColor = Color(0xFFFFFBF7),
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFFFF8F0)) {
                NavigationBarItem(
                    selected = currentRoute == Routes.Home,
                    onClick = onNavigateHome,
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.CategorySelection,
                    onClick = onNavigateCategories,
                    icon = { Text("🪷", fontSize = 20.sp) },
                    label = { Text("Catégories") }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Favorites,
                    onClick = onNavigateFavorites,
                    icon = { Text("❤", fontSize = 20.sp) },
                    label = { Text("Favoris") }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Cart,
                    onClick = onNavigateCart,
                    icon = { Text("🛒", fontSize = 20.sp) },
                    label = { Text("Panier") }
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aucun produit trouvé dans cette catégorie.",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    ProductsList(
                        products = filtered,
                        favoriteProductIds = viewModel.favoriteIds.collectAsState().value,
                        onNavigateToDetails = onNavigateToDetails,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onRateProduct = { id, rate -> viewModel.updateProductRating(id, rate) }
                    )
                }
            }
        }
    )
}
