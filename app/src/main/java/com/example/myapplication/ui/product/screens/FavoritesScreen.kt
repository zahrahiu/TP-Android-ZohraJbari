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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.ProductsList
import com.example.myapplication.ui.product.component.QuickFilter          // ✅
import com.example.myapplication.navigator.Routes

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
    /* ------- State from ViewModel ------- */
    val state       by viewModel.state.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    /* ------- UI local state ------- */
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedQuickFilter by remember { mutableStateOf<QuickFilter?>(null) }   // ✅ جديد

    /* ------- Filters data ------- */
    val types     = listOf("ROSE","TULIP","LILY","ORCHID","HIBISCUS","LAVENDER","DAISY","PANSY")
    val colors    = listOf("RED","WHITE","YELLOW","PINK","PURPLE","MULTICOLOR")
    val occasions = listOf("LOVE","BIRTHDAY","WEDDING","ANNIVERSARY","APOLOGY","THANKS")

    var tType     by remember { mutableStateOf<String?>(null) }
    var tOccasion by remember { mutableStateOf<String?>(null) }
    var tColors   by remember { mutableStateOf(setOf<String>()) }
    var pMin      by remember { mutableStateOf("") }
    var pMax      by remember { mutableStateOf("") }

    var aType       by remember { mutableStateOf<String?>(null) }
    var aOccasion   by remember { mutableStateOf<String?>(null) }
    var aColors     by remember { mutableStateOf(setOf<String>()) }
    var aPriceRange by remember { mutableStateOf(0f..400f) }

    fun parsePrice(min: String, max: String): ClosedFloatingPointRange<Float> {
        val mn = min.toFloatOrNull() ?: 0f
        val mx = max.toFloatOrNull() ?: 400f
        return if (mn <= mx) mn..mx else mx..mn
    }

    /* ------- Produit favoris ------- */
    val favProducts = state.products.filter { it.id in favoriteIds }

    /* ------- Search + gros filtres ------- */
    val filtered = favProducts.filter { p ->
        val matchSearch = searchQuery.isBlank() ||
                p.name.contains(searchQuery, true) ||
                p.description.contains(searchQuery, true)

        val matchType  = aType == null || p.type.equals(aType, true)
        val matchOcc   = aOccasion == null || p.occasions.any { it.equals(aOccasion, true) }
        val matchColor = aColors.isEmpty() || p.colors.any { it.uppercase() in aColors }

        val price = p.price
            .replace("DH","",true)
            .replace(" ","")
            .replace(",",".")
            .toFloatOrNull() ?: 0f
        val matchPrice = price in aPriceRange

        matchSearch && matchType && matchOcc && matchColor && matchPrice
    }

    /* ------- QuickFilter spécifique ------- */
    val productsToShow = when (selectedQuickFilter) {
        QuickFilter.GIFT       -> filtered.filter { it.category.equals("GIFT", true) }
        QuickFilter.MULTICOLOR -> filtered.filter { "MULTICOLOR" in it.colors }
        QuickFilter.BASKET     -> filtered.filter {
            it.description.contains("panier", true) ||
                    it.description.contains("arrangement", true)
        }
        null -> filtered
    }

    /* ******************************************************************** */
    /* ******************************* UI ********************************* */
    /* ******************************************************************** */

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("❤️ Mes Favoris", color = Color(0xFFDC4C3E), fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF8F0))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFFFF8F0)) {
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
        containerColor = Color(0xFFFFFBF7)
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            /* --------- Search bar + toggle filters --------- */
            // (نفس الكود ديالك، ما تبدّلش)

            // … الكود الخاص بالـ Search + AnimatedVisibility خاص بالفلاتر يبقى كما هو …

            Spacer(Modifier.height(8.dp))

            /* ------------- Produits ------------- */
            Box(Modifier.weight(1f)) {
                when {
                    state.isLoading       -> Center { CircularProgressIndicator(color = Color(0xFFDC4C3E)) }
                    favProducts.isEmpty() -> Center { Text("Aucun produit favori pour le moment.") }
                    productsToShow.isEmpty() -> Center { Text("Filtre vide") }
                    else -> ProductsList(
                        products              = productsToShow,
                        favoriteProductIds    = favoriteIds,
                        selectedQuickFilter   = selectedQuickFilter,      // ✅
                        onQuickFilterSelected = { selectedQuickFilter = it }, // ✅
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
}

/* Helper */
@Composable
fun Center(content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center, content = content)
}
