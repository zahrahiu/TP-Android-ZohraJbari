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
    val state          by viewModel.state.collectAsState()
    val favoriteIds    by viewModel.favoriteIds.collectAsState()

    /* ❶ Quick-filter state */
    var selectedQuickFilter by remember { mutableStateOf<QuickFilter?>(null) }

    /* ❷ produits de نفس الـ catégorie */
    val inCategory = state.products
        .filter { it.category.equals(category, true) }

    /* ❸ appliquer Quick-filter (نفس القاعدة اللي فـ HomeScreen) */
    val productsToShow = when (selectedQuickFilter) {
        QuickFilter.GIFT       -> inCategory.filter { it.category.equals("GIFT", true) }
        QuickFilter.MULTICOLOR -> inCategory.filter { "MULTICOLOR" in it.colors }
        QuickFilter.BASKET     -> inCategory.filter {
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
                        color = Color(0xFFDC4C3E),
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF8F0))
            )
        },
        containerColor = Color(0xFFFFFBF7),
        bottomBar = { /* … نفس الـ NavigationBar ديالك … */ }
    ) { padding ->

        if (productsToShow.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Aucun produit trouvé dans cette catégorie.",
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
                /* ❹ ProductsList الجديد مع البراميطرين الجداد */
                ProductsList(
                    products              = productsToShow,
                    favoriteProductIds    = favoriteIds,
                    selectedQuickFilter   = selectedQuickFilter,
                    onQuickFilterSelected = { selectedQuickFilter = it },
                    onNavigateToDetails   = onNavigateToDetails,
                    onToggleFavorite      = viewModel::toggleFavorite,
                    onRateProduct         = { id, rate ->
                        viewModel.updateProductRating(id, rate)
                    }
                )
            }
        }
    }
}
