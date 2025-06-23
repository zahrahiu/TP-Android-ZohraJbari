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
fun ProductsList(
    products: List<Product>,
    onNavigateToDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    favoriteProductIds: Set<String>,
    onToggleFavorite: (Product) -> Unit  ,
    onRateProduct: (String, Int) -> Unit
) {

    
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->

            ProductItem(
                product = product,
                isFavorite     = favoriteProductIds.contains(product.id),
                onItemClick    = { onNavigateToDetails(product.id) },
                onFavoriteClick= { onToggleFavorite(product) } ,
                onRateProduct = { newRating ->
                    onRateProduct(product.id, newRating)
                }



            )

        }
    }
}