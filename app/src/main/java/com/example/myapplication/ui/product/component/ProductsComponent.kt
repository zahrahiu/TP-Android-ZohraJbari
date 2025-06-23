package com.example.myapplication.ui.product.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Entities.Product

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductsList( // ← هنا زيدي s
    products: List<Product>,
    favoriteProductIds: Set<String>,
    onNavigateToDetails: (String) -> Unit,
    onToggleFavorite: (Product) -> Unit,
    onRateProduct: (String, Int) -> Unit
)  {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = true
    ) {
        items(products) { product ->
            ProductItem(
                product = product,
                isFavorite = favoriteProductIds.contains(product.id),
                onItemClick = { onNavigateToDetails(product.id) },
                onFavoriteClick = { onToggleFavorite(product) },
                onRateProduct = { rating -> onRateProduct(product.id, rating) }
            )
        }
    }
}