package com.example.myapplication.ui.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.product.screens.LanguageManager
import kotlinx.coroutines.launch

val RougeFlora = Color(0xFFDC4C3E)      // Rouge cerise
val BeigeFlora = Color(0xFFFFFBF7)      // Beige clair
val GrisPetitGris = Color(0xFF8E8E93)   // Gris doux

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToCategory: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    lang: LanguageManager.Instance
) {
    val colors = MaterialTheme.colorScheme
    val items = viewModel.items.collectAsState().value
    val snackHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun parsePrice(priceStr: String): Float {
        return priceStr.replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0f
    }

    fun calculateDiscountedPrice(price: Float, discountPercent: Int?): Float {
        return if (discountPercent != null) {
            price * (100 - discountPercent) / 100
        } else {
            price
        }
    }

    val totalPrice = items.sumOf { ci ->
        val originalPrice = parsePrice(ci.product.price).toDouble()
        val productPrice = calculateDiscountedPrice(originalPrice.toFloat(), ci.product.discountPercent).toDouble()
        val addonsPrice = ci.addons.sumOf { it.addon.price.toDouble() * it.quantity }
        productPrice * ci.quantity + addonsPrice
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(lang.get("cart"), color = colors.primary, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.background,
                    titleContentColor = colors.primary
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = colors.background) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text(lang.get("home")) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCategory,
                    icon = { Text("🪷", fontSize = 20.sp) },
                    label = { Text(lang.get("categories")) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToFavorites,
                    icon = { Text("❤", fontSize = 20.sp) },
                    label = { Text(lang.get("favorites")) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = onNavigateToCart,
                    icon = { Text("🛒", fontSize = 20.sp) },
                    label = { Text(lang.get("cart")) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackHostState) },
        containerColor = colors.background
    ) { pad ->
        if (items.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(pad),
                contentAlignment = Alignment.Center
            ) {
                Text(lang.get("cart") + " " + lang.get("empty"), color = GrisPetitGris, fontWeight = FontWeight.Medium)
            }
            return@Scaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .background(colors.background)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(items) { ci ->
                    var expanded by remember { mutableStateOf(false) }
                    val maxStock = ci.product.quantity
                    val outOfStock = ci.quantity >= maxStock
                    val originalPrice = parsePrice(ci.product.price)
                    val discountedPrice = calculateDiscountedPrice(originalPrice, ci.product.discountPercent)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxHeight()
                                    .width(80.dp),
                                shape = MaterialTheme.shapes.medium,
                                color = Color.White,
                                tonalElevation = 4.dp,
                                shadowElevation = 4.dp,
                            ) {
                                val imgRes = getImageResource(ci.product.image)
                                Image(
                                    painterResource(imgRes),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    ci.product.name,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    maxLines = 1,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (ci.product.discountPercent != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "${originalPrice.toInt()} ${lang.get("currency")}",
                                            color = Color.Gray,
                                            textDecoration = TextDecoration.LineThrough,
                                            fontSize = 14.sp
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "${discountedPrice.toInt()} ${lang.get("currency")}",
                                            color = colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                } else {
                                    Text(
                                        "${originalPrice.toInt()} ${lang.get("currency")}",
                                        color = Color.Black,
                                        fontSize = 16.sp
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    IconButton(onClick = { viewModel.dec(ci) }) {
                                        Icon(Icons.Filled.Remove, contentDescription = lang.get("decrement"), tint = RougeFlora)
                                    }
                                    Text(
                                        "${ci.quantity}",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.width(32.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    IconButton(
                                        onClick = {
                                            val ok = viewModel.inc(ci)
                                            if (!ok) scope.launch {
                                                snackHostState.showSnackbar(
                                                    "${lang.get("stock_insufficient")} ${ci.product.name}"
                                                )
                                            }
                                        },
                                        enabled = !outOfStock
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = lang.get("increment"), tint = RougeFlora)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.End
                            ) {
                                IconButton(onClick = { viewModel.removeProduct(ci) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = lang.get("remove_product"), tint = RougeFlora)
                                }
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight,
                                        contentDescription = if (expanded) lang.get("collapse") else lang.get("details"),
                                        tint = colors.primary
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(visible = expanded) {
                            Column(Modifier.padding(start = 48.dp, top = 8.dp)) {
                                ci.addons.forEach { aq ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    ) {
                                        Image(
                                            painterResource(aq.addon.imageRes),
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Text(
                                            aq.addon.name,
                                            Modifier.weight(1f).padding(start = 12.dp),
                                            color = Color.Black,
                                            fontSize = 16.sp
                                        )
                                        IconButton(onClick = { viewModel.decAddon(ci, aq) }) {
                                            Icon(Icons.Filled.Remove, contentDescription = lang.get("decrement_addon"), tint = RougeFlora)
                                        }
                                        Text("${aq.quantity}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        IconButton(onClick = { viewModel.incAddon(ci, aq) }) {
                                            Icon(Icons.Filled.Add, contentDescription = lang.get("increment_addon"), tint = RougeFlora)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(
                        width = 1.dp,
                        color = RougeFlora,
                        shape = MaterialTheme.shapes.medium
                    ),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            lang.get("total_price"),
                            fontWeight = FontWeight.Normal,
                            color = colors.primary,
                            fontSize = 16.sp
                        )
                        Text(
                            String.format("%.2f %s", totalPrice, lang.get("currency")),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    }

                    Button(
                        onClick = { onNavigateToCheckout() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        modifier = Modifier.padding(start = 30.dp)
                    ) {
                        Text(
                            lang.get("checkout"),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

private fun getImageResource(name: String): Int = when (name) {
    "hibiscus.jpg"        -> R.drawable.hibiscus
    "lavender.jpg"        -> R.drawable.lavender
    "lily.jpg"            -> R.drawable.lily
    "pansy.jpg"           -> R.drawable.pansy
    "img1.jpg"            -> R.drawable.img1
    "img2.jpg"            -> R.drawable.img2
    "img3.jpg"            -> R.drawable.img3
    "img4.jpg"            -> R.drawable.img4
    "img8.jpg"            -> R.drawable.img8
    "rosebox.jpg"         -> R.drawable.rosebox
    "tulipspanier.jpg"    -> R.drawable.tulipspanier
    "orchidbirthday.jpg"  -> R.drawable.orchidbirthday
    "lilygift.jpg"        -> R.drawable.lilygift
    "pansycolor.jpg"      -> R.drawable.pansycolor
    "pinkhibiscus.jpg"    -> R.drawable.pinkhibiscus
    "daisyapology.jpg"    -> R.drawable.daisyapology
    "romantictulips.jpg"  -> R.drawable.romantictulips
    "purelily.jpg"        -> R.drawable.purelily
    else                  -> R.drawable.img1
}
