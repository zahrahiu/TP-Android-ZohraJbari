/* ────────────────────────────  HomeScreen.kt  ─────────────────────────── */
package com.example.myapplication.ui.product.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.navigator.Routes
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.ProductsList
import com.example.myapplication.ui.product.component.QuickFilter
import com.example.myapplication.ui.theme.LocalThemeState
import com.example.myapplication.ui.theme.Mode
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    viewModel: ProductViewModel = viewModel(),
    lang: LanguageManager.Instance,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToCategory: () -> Unit,
    currentRoute: String = Routes.Home,
    onLogout: () -> Unit
) {
    /* ─── language & theme ─────────────────────────────────────────────── */
    val themeState = LocalThemeState.current
    var showMenu   by remember { mutableStateOf(false) }
    var mainMenuExpanded    by remember { mutableStateOf(false) }
    var langSubMenuExpanded by remember { mutableStateOf(false) }
    var themeSubMenuExpanded by remember { mutableStateOf(false) }
    /* ─── VM state ─────────────────────────────────────────────────────── */
    val state       by viewModel.state.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    /* ─── UI state ─────────────────────────────────────────────────────── */
    var searchQuery         by remember { mutableStateOf("") }
    var showFilters         by remember { mutableStateOf(false) }
    var selectedQuickFilter by remember { mutableStateOf<QuickFilter?>(null) }

    /* ─── filter data ──────────────────────────────────────────────────── */
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

    /* ─── filtering ────────────────────────────────────────────────────── */
    val baseFiltered = state.products.filter { p ->
        val okSearch   = searchQuery.isBlank() ||
                p.name.contains(searchQuery, true) ||
                p.description.contains(searchQuery, true)
        val okType     = aType == null      || p.type.equals(aType, true)
        val okOccasion = aOccasion == null  || p.occasions.any { it.equals(aOccasion, true) }
        val okColor    = aColors.isEmpty() || p.colors.any { it.uppercase() in aColors }
        val price      = p.price.replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0f
        val okPrice    = price in aPriceRange
        okSearch && okType && okOccasion && okColor && okPrice
    }
    var logoutDialogVisible by remember { mutableStateOf(false) }

    var deactivateDialogVisible by remember { mutableStateOf(false) }
    val productsToShow = when (selectedQuickFilter) {
        QuickFilter.GIFT       -> baseFiltered.filter { it.category.equals("GIFT", true) }
        QuickFilter.MULTICOLOR -> baseFiltered.filter { "MULTICOLOR" in it.colors }
        QuickFilter.BASKET     -> baseFiltered.filter {
            it.description.contains("panier", true) ||
                    it.description.contains("arrangement", true)
        }
        null -> baseFiltered
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("🌸 Flora Boutique",
                        color      = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    Box {
                        IconButton(onClick = { mainMenuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }

                        /* ── MAIN MENU ── */
                        DropdownMenu(
                            expanded = mainMenuExpanded,
                            onDismissRequest = { mainMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Language") },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, null, Modifier.rotate(-90f))
                                },
                                onClick = {
                                    langSubMenuExpanded = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Theme") },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, null, Modifier.rotate(-90f))
                                },
                                onClick = {
                                    themeSubMenuExpanded = true
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text("Se déconnecter") },
                                onClick = {
                                    logoutDialogVisible = true
                                    mainMenuExpanded   = false
                                }
                            )

                        }

                        DropdownMenu(
                            expanded = langSubMenuExpanded,
                            offset = DpOffset(150.dp, 0.dp),
                            onDismissRequest = { langSubMenuExpanded = false }
                        ) {
                            DropdownMenuItem(text = { Text("Français") },
                                onClick = {
                                    lang.onChange(LanguageManager.Lang.FR)
                                    langSubMenuExpanded = false
                                    mainMenuExpanded = false
                                })
                            DropdownMenuItem(text = { Text("English") },
                                onClick = {
                                    lang.onChange(LanguageManager.Lang.EN)
                                    langSubMenuExpanded = false
                                    mainMenuExpanded = false
                                })
                            DropdownMenuItem(text = { Text("العربية") },
                                onClick = {
                                    lang.onChange(LanguageManager.Lang.AR)
                                    langSubMenuExpanded = false
                                    mainMenuExpanded = false
                                })
                        }

                        /* ── THEME SUB MENU ── */
                        DropdownMenu(
                            expanded = themeSubMenuExpanded,
                            offset = DpOffset(150.dp, 0.dp),
                            onDismissRequest = { themeSubMenuExpanded = false }
                        ) {
                            DropdownMenuItem(text = { Text("Mode Clair") },
                                onClick = {
                                    themeState.mode = Mode.CLAIR
                                    themeSubMenuExpanded = false
                                    mainMenuExpanded = false
                                })
                            DropdownMenuItem(text = { Text("Mode Sombre") },
                                onClick = {
                                    themeState.mode = Mode.SOMBRE
                                    themeSubMenuExpanded = false
                                    mainMenuExpanded = false
                                })
                            DropdownMenuItem(text = { Text("Mode Pastel") },
                                onClick = {
                                    themeState.mode = Mode.PASTEL
                                    themeSubMenuExpanded = false
                                    mainMenuExpanded = false
                                })
                        }
                    }
                }
            )
            /* ─── ALERT: Se déconnecter ─────────────────────────────────────── */
            if (logoutDialogVisible) {
                AlertDialog(
                    onDismissRequest = { logoutDialogVisible = false },
                    title   = { Text("Confirmation") },
                    text    = { Text("Êtes-vous sûr(e) de vouloir vous déconnecter ?") },
                    confirmButton = {
                        TextButton(onClick = {
                            logoutDialogVisible = false
                            onLogout()          // ⇦ ما بقى احتياج لـ navController هنا
                        }) { Text("Oui")  }
                    },
                    dismissButton = {
                        TextButton(onClick = { logoutDialogVisible = false }) {
                            Text("Non")
                        }
                    }
                )
            }



        },


        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                NavigationBarItem(
                    selected = currentRoute == Routes.Home,
                    icon     = { Text("🏠", fontSize = 20.sp) },
                    label    = { Text(lang.get("home")) },
                    onClick  = {}
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.CategorySelection,
                    icon     = { Text("🪷", fontSize = 20.sp) },
                    label    = { Text(lang.get("categories")) },
                    onClick  = onNavigateToCategory
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Favorites,
                    icon     = { Text("❤", fontSize = 20.sp) },
                    label    = { Text(lang.get("favorites")) },
                    onClick  = onNavigateToFavorites
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Cart,
                    icon     = { Text("🛒", fontSize = 20.sp) },
                    label    = { Text(lang.get("cart")) },
                    onClick  = onNavigateToCart
                )
            }
        },

        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            /* ── search + filtre chip ─────────────────────────────────── */
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment  = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder   = { Text(lang.get("search_placeholder")) },
                    leadingIcon   = { Icon(Icons.Default.Search, null) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    colors        = TextFieldDefaults.colors(
                        focusedContainerColor   = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier      = Modifier.weight(1f)
                )

                FilterChip(
                    selected = showFilters,
                    onClick  = { showFilters = !showFilters },
                    label    = { Text(lang.get("filters")) },
                    leadingIcon = {
                        Icon(Icons.Default.ArrowDropDown, null,
                            Modifier.rotate(if (showFilters) 180f else 0f))
                    },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        selectedLabelColor     = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            /* ── panneau de filtres ───────────────────────────────────── */
            AnimatedVisibility(
                visible = showFilters,
                enter   = fadeIn()  + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    var tab by remember { mutableStateOf<String?>(null) }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("Type","Couleur","Occasion").forEach { t ->
                            FilterChip(
                                selected = tab == t,
                                onClick  = { tab = if (tab == t) null else t },
                                label    = { Text(t) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    selectedLabelColor     = MaterialTheme.colorScheme.primary
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
                                    label    = { Text(t) }
                                )
                            }
                        }
                        "Couleur" -> FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                            colors.forEach { c ->
                                FilterChip(
                                    selected = c in tColors,
                                    onClick  = {
                                        tColors = if (c in tColors) tColors - c else tColors + c
                                    },
                                    label    = { Text(c) }
                                )
                            }
                        }
                        "Occasion" -> FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                            occasions.forEach { o ->
                                FilterChip(
                                    selected = tOccasion == o,
                                    onClick  = { tOccasion = if (tOccasion == o) null else o },
                                    label    = { Text(o) }
                                )
                            }
                        }
                        null -> Text(
                            lang.get("select_filter"),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = pMin,
                            onValueChange = { pMin = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text(lang.get("price_min")) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pMax,
                            onValueChange = { pMax = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text(lang.get("price_max")) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TextButton(
                            onClick = {
                                tType = null ; tOccasion = null ; tColors = emptySet()
                                pMin  = ""   ; pMax      = ""
                            }
                        ) { Text(lang.get("reset")) }

                        Button(
                            onClick = {
                                aType       = tType
                                aOccasion   = tOccasion
                                aColors     = tColors
                                aPriceRange = parsePrice(pMin, pMax)
                                showFilters = false
                            }
                        ) { Text(lang.get("apply")) }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            /* ── titre central ────────────────────────────────────────── */
            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                Text(
                    lang.get("find_flower"),
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(8.dp))

            /* ── liste produits / état ─────────────────────────────────── */
            Box(Modifier.weight(1f)) {
                when {
                    state.isLoading -> Center {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                    state.error != null -> Center {
                        Text("${lang.get("error")}: ${state.error}", color = Color.Red)
                    }
                    productsToShow.isEmpty() -> Center {
                        Text(lang.get("no_products"))
                    }
                    else -> ProductsList(
                        products              = productsToShow,
                        favoriteProductIds    = favoriteIds,
                        selectedQuickFilter   = selectedQuickFilter,
                        onQuickFilterSelected = { selectedQuickFilter = it },
                        onNavigateToDetails   = onNavigateToDetails,
                        onToggleFavorite      = viewModel::toggleFavorite,
                        lang = lang,

                        onRateProduct         = { id, rate -> viewModel.updateProductRating(id, rate) }
                    )
                }
            }
        }
    }
}


