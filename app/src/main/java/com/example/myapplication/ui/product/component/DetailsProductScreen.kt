package com.example.myapplication.ui.product.component

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.data.Entities.Addon
import com.example.myapplication.data.Entities.Product
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.screens.LanguageManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DetailsScreen(
    product: Product,
    viewModel: ProductViewModel,
    navController: NavController? = null,
    lang: LanguageManager.Instance,    // LanguageManager Instance injected

    onAddToCart: (List<Pair<Addon, Int>>) -> Boolean = { true }
) {
    val imageRes = when (product.image) {
        "hibiscus.jpg" -> R.drawable.hibiscus
        "lavender.jpg" -> R.drawable.lavender
        "lily.jpg" -> R.drawable.lily
        "pansy.jpg" -> R.drawable.pansy
        "img1.jpg" -> R.drawable.img1
        "img2.jpg" -> R.drawable.img2
        "img3.jpg" -> R.drawable.img3
        "img4.jpg" -> R.drawable.img4
        "img8.jpg" -> R.drawable.img8
        "rosebox.jpg" -> R.drawable.rosebox
        "tulipspanier.jpg" -> R.drawable.tulipspanier
        "orchidbirthday.jpg" -> R.drawable.orchidbirthday
        "lilygift.jpg" -> R.drawable.lilygift
        "pansycolor.jpg" -> R.drawable.pansycolor
        "pinkhibiscus.jpg" -> R.drawable.pinkhibiscus
        "daisyapology.jpg" -> R.drawable.daisyapology
        "romantictulips.jpg" -> R.drawable.romantictulips
        "purelily.jpg" -> R.drawable.purelily
        else -> R.drawable.img1
    }

    val colors = MaterialTheme.colorScheme
    var showSheet by remember { mutableStateOf(false) }
    val snackHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val oldPrice = product.price.replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0f
    val newPrice = product.discountPercent?.let { oldPrice * (100 - it) / 100 }

    var remainingMillis by remember { mutableStateOf(0L) }
    LaunchedEffect(product.offerEndEpochMillis) {
        while (true) {
            val now = System.currentTimeMillis()
            remainingMillis = (product.offerEndEpochMillis ?: 0L) - now
            if (remainingMillis <= 0L) break
            delay(1000)
        }
    }

    val productsState by viewModel.state.collectAsState()
    val similarProducts = productsState.products.filter {
        it.id != product.id &&
                (it.type == product.type || it.colors.any { c -> product.colors.contains(c) })
    }.take(5)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        lang.get("home"), // تعريب العنوان مثلا هنا أو تكتب "Détails Produit" مع تعريب في LanguageManager إذا بغيت
                        color = colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = colors.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
            )
        },
        snackbarHost = { SnackbarHost(snackHost) },
        containerColor = colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                product.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground,
                modifier = Modifier.padding(start = 17.dp, top = 10.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Image(
                        painter = painterResource(imageRes),
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, colors.surfaceVariant, RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    product.discountPercent?.let {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .background(colors.primary, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("-$it%", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (product.discountPercent != null && remainingMillis > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surface.copy(alpha = 0.9f))
                                .border(2.dp, colors.primary, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            val h = TimeUnit.MILLISECONDS.toHours(remainingMillis)
                            val m = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
                            val s = TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % 60
                            Text(
                                text = String.format("⏳ %02d:%02d:%02d", h, m, s),
                                color = colors.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surfaceVariant)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (newPrice != null) {
                            Text(
                                "${oldPrice.toInt()} ${lang.get("currency")}",
                                textDecoration = TextDecoration.LineThrough,
                                color = colors.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                "${newPrice.toInt()} ${lang.get("currency")}",
                                color = colors.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        } else {
                            Text(
                                "${oldPrice.toInt()} ${lang.get("currency")}",
                                color = colors.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(lang.get("quantity") ?: "Quantité : ", color = colors.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(product.quantity, color = colors.onSurface, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }

                    ProductRatingSection(product = product, onRate = {
                        viewModel.updateProductRating(product.id, it)
                    })
                }
            }

            Spacer(Modifier.height(20.dp))

            Column(Modifier.padding(horizontal = 20.dp)) {
                ExpandableSection("📜 ${lang.get("description") ?: "Description"}", colors.onSurfaceVariant, colors.onSurface) {
                    Text(product.description, color = colors.onSurfaceVariant, textAlign = TextAlign.Justify, fontSize = 14.sp)
                }
                Spacer(Modifier.height(18.dp))
                ExpandableSection("ℹ️ ${lang.get("other_info") ?: "Autres informations"}", colors.onSurfaceVariant, colors.onSurface) {
                    DetailRow(lang.get("type") ?: "Type", product.type, colors.onSurfaceVariant, colors.primary)
                    DetailRow(lang.get("colors") ?: "Couleurs", product.colors.joinToString(), colors.onSurfaceVariant, colors.primary)
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { showSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text("🛒 ${lang.get("add_to_cart") ?: "Ajouter au panier"}", fontSize = 16.sp)
            }

            if (similarProducts.isNotEmpty()) {
                Text(
                    lang.get("similar_products") ?: "Produits similaires",
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
                )
                Row(
                    Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    similarProducts.forEach { simProd ->
                        ProductItem(
                            product = simProd,
                            isFavorite = viewModel.favoriteIds.collectAsState().value.contains(simProd.id),
                            onItemClick = { navController?.navigate("details/${simProd.id}") },
                            onFavoriteClick = { viewModel.toggleFavorite(simProd) },
                            onRateProduct = { rating -> viewModel.updateProductRating(simProd.id, rating) },
                            lang = lang,
                            modifier = Modifier
                                .width(160.dp)
                                .padding(end = 10.dp)
                        )

                    }
                }
            }
        }
    }

    AddOnsBottomSheet(
        open = showSheet,
        onDismiss = { showSheet = false },
        onValidate = { selected ->
            val ok = onAddToCart(selected)
            if (!ok) {
                scope.launch {
                    snackHost.showSnackbar(lang.get("stock_insufficient") ?: "Stock insuffisant")
                }
            } else {
                showSheet = false
            }

        },
                lang = lang
    )


}

@Composable
fun ExpandableSection(
    title: String,
    titleColor: Color,
    contentColor: Color,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontWeight = FontWeight.SemiBold, color = titleColor, fontSize = 16.sp)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = titleColor, modifier = Modifier.rotate(if (expanded) 180f else 0f))
        }
        AnimatedVisibility(
            visible = expanded,
            enter = slideInVertically(tween(300)) { -it },
            exit = slideOutVertically(tween(300)) { -it }
        ) {
            Column(modifier = Modifier.padding(top = 8.dp)) { content() }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, textColor: Color, accentColor: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = textColor)
        Text(
            value,
            color = Color.Black,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
