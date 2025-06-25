package com.example.myapplication.ui.product.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Entities.Product
import com.example.myapplication.ui.product.screens.LanguageManager

@Composable
fun ProductsList(
    products: List<Product>,
    favoriteProductIds: Set<String>,
    selectedQuickFilter: QuickFilter?,
    onQuickFilterSelected: (QuickFilter?) -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onToggleFavorite: (Product) -> Unit,
    onRateProduct: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
    lang: LanguageManager.Instance,    // هنا

) {
    val rows = products.chunked(2)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {

        item {
            LazyRow(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(QuickFilter.values().toList(), key = { it.name }) { filter ->
                    QuickFilterImage(
                        filter      = filter,
                        isSelected  = selectedQuickFilter == filter,
                        onClick     = {
                            onQuickFilterSelected(
                                if (selectedQuickFilter == filter) null else filter
                            )
                        }
                    )
                }
            }
        }

        items(rows) { row ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { product ->
                    ProductItem(
                        product          = product,
                        isFavorite       = product.id in favoriteProductIds,
                        onItemClick      = { onNavigateToDetails(product.id) },
                        onFavoriteClick  = { onToggleFavorite(product) },
                        onRateProduct    = { rating -> onRateProduct(product.id, rating) },
                        lang = lang,
                        modifier         = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
