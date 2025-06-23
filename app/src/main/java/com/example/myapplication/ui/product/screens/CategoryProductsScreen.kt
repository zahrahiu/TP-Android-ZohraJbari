package com.example.myapplication.ui.product.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.ProductsList

@Composable
fun CategoryProductsScreen(
    viewModel: ProductViewModel,
    category: String,
    onNavigateToDetails: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val filtered = state.products.filter { it.category.equals(category, ignoreCase = true) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Produits pour: $category", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Spacer(Modifier.height(16.dp))

        ProductsList(
            products = filtered,
            favoriteProductIds = viewModel.favoriteIds.collectAsState().value,
            onNavigateToDetails = onNavigateToDetails,
            onToggleFavorite = viewModel::toggleFavorite,
            onRateProduct = { id, rate -> viewModel.updateProductRating(id, rate) }
        )
    }
}
