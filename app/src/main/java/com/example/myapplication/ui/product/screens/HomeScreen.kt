package com.example.emtyapp.ui.product.screens

import com.google.accompanist.flowlayout.FlowRow
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
import com.example.emtyapp.nav.Routes
import com.example.myapplication.ui.product.ProductIntent
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.ProductsList
import com.example.myapplication.data.Entities.Product

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    viewModel: ProductViewModel = viewModel(),
    onNavigateToDetails: (String) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToCart: () -> Unit,           // ← هنا ضفت callback جديد
    currentRoute: String = Routes.Home      // ← باش نعرفو الصفحة الحالية (اختياري)
) {
    val state       by viewModel.state.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

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

    val filtered = state.products.filter { p ->
        val matchesSearch   = searchQuery.isBlank() ||
                p.name.contains(searchQuery, true) ||
                p.description.contains(searchQuery, true)
        val matchesType     = aType == null || p.type.equals(aType, true)
        val matchesOccasion = aOccasion == null || p.occasions.any { it.equals(aOccasion, true) }
        val matchesColor    = aColors.isEmpty() || p.colors.any { aColors.contains(it.uppercase()) }
        val priceValue      = p.price.replace("DH","",true).replace(" ","").replace(",",".")
            .toFloatOrNull() ?: 0f
        val matchesPrice    = priceValue in aPriceRange
        matchesSearch && matchesType && matchesOccasion && matchesColor && matchesPrice
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌸 Flora Boutique", color = Color(0xFFDC4C3E), fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF8F0))
            )
        },
        containerColor = Color(0xFFFFFBF7),
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFFFF8F0)) {
                NavigationBarItem(
                    selected = currentRoute == Routes.Home,
                    onClick = { },
                    icon  = { Text("🏠", fontSize = 20.sp) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Favorites,
                    onClick = onNavigateToFavorites,
                    icon = { Text("❤", fontSize = 20.sp) },
                    label = { Text("Favoris") }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Cart,
                    onClick = onNavigateToCart,
                    icon = { Text("🛒", fontSize = 20.sp) },
                    label = { Text("Panier") }
                )
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery, onValueChange = { searchQuery = it },
                    placeholder = { Text("🔍 Rechercher...") },
                    leadingIcon = { Icon(Icons.Default.Search,null) },
                    modifier    = Modifier.weight(1f),
                    shape       = RoundedCornerShape(12.dp),
                    colors      = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine  = true
                )
                FilterChip(
                    selected   = showFilters,
                    onClick    = { showFilters = !showFilters },
                    label      = { Text("Filtres") },
                    leadingIcon= {
                        Icon(Icons.Default.ArrowDropDown,null,
                            Modifier.rotate(if(showFilters)180f else 0f))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDC4C3E).copy(.2f),
                        selectedLabelColor     = Color(0xFFDC4C3E)
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(
                visible = showFilters,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Column(
                    Modifier.fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    var tab by remember { mutableStateOf<String?>(null) }

                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                        listOf("Type","Couleur","Occasion").forEach { t ->
                            FilterChip(
                                selected = tab == t,
                                onClick  = { tab = if (tab == t) null else t },
                                label    = { Text(t) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFDC4C3E).copy(.2f),
                                    selectedLabelColor     = Color(0xFFDC4C3E)
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    when (tab) {
                        "Type" -> FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                            types.forEach { t ->
                                FilterChip(
                                    selected = tType == t,
                                    onClick  = { tType = if (tType == t) null else t },
                                    label    = { Text(t) },
                                    colors   = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFDC4C3E).copy(.2f),
                                        selectedLabelColor     = Color(0xFFDC4C3E)
                                    )
                                )
                            }
                        }
                        "Couleur" -> FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                            colors.forEach { c ->
                                FilterChip(
                                    selected = tColors.contains(c),
                                    onClick  = {
                                        tColors = if (tColors.contains(c)) tColors - c else tColors + c
                                    },
                                    label    = { Text(c) },
                                    colors   = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFDC4C3E).copy(.2f),
                                        selectedLabelColor     = Color(0xFFDC4C3E)
                                    )
                                )
                            }
                        }
                        "Occasion" -> FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                            occasions.forEach { o ->
                                FilterChip(
                                    selected = tOccasion == o,
                                    onClick  = { tOccasion = if (tOccasion == o) null else o },
                                    label    = { Text(o) },
                                    colors   = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFDC4C3E).copy(.2f),
                                        selectedLabelColor     = Color(0xFFDC4C3E)
                                    )
                                )
                            }
                        }
                        null -> Text(
                            "Sélectionnez un filtre pour afficher les options",
                            color = Color.Gray, modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = pMin, singleLine = true,
                            onValueChange = { pMin = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Prix Min") }, modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pMax, singleLine = true,
                            onValueChange = { pMax = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Prix Max") }, modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                        TextButton(onClick = {
                            tType=null; tOccasion=null; tColors= emptySet()
                            pMin=""; pMax=""
                        }) { Text("Réinitialiser", color = Color(0xFFDC4C3E)) }

                        Button(onClick = {
                            aType = tType; aOccasion = tOccasion; aColors = tColors
                            aPriceRange = parsePrice(pMin, pMax); showFilters = false
                        }) { Text("Appliquer") }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Box(Modifier.weight(1f)) {
                when {
                    state.isLoading       -> Center { CircularProgressIndicator(color = Color(0xFFDC4C3E)) }
                    state.error != null   -> Center { Text("Erreur: ${state.error}", color = Color.Red) }
                    filtered.isEmpty()    -> Center { Text("Aucun produit trouvé") }
                    else -> ProductsList(
                        products            = filtered,
                        favoriteProductIds  = favoriteIds,            // ✅
                        onNavigateToDetails = onNavigateToDetails,
                        onToggleFavorite    = viewModel::toggleFavorite  ,
                        onRateProduct = { productId, newRating ->
                            viewModel.updateProductRating(productId, newRating)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun Center(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = content
    )
}