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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import kotlinx.coroutines.launch

// Couleurs mises à jour selon votre demande
val RougeFlora = Color(0xFFDC4C3E)      // Rouge cerise
val BeigeFlora = Color(0xFFFFFBF7)      // Beige clair (identique à BeigeBackground)
val GrisPetitGris = Color(0xFF8E8E93)   // Gris doux

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToCategory: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
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
                title = { Text("Mon Panier", color = RougeFlora, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BeigeFlora,
                    titleContentColor = RougeFlora
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = BeigeFlora) {
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
                    selected = false,
                    onClick = onNavigateToFavorites,
                    icon = { Text("❤", fontSize = 20.sp) },
                    label = { Text("Favoris") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = onNavigateToCart,
                    icon = { Text("🛒", fontSize = 20.sp) },
                    label = { Text("Panier") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackHostState) },
        containerColor = BeigeFlora
    ) { pad ->
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                Text("Panier vide", color = GrisPetitGris, fontWeight = FontWeight.Medium)
            }
            return@Scaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .background(BeigeFlora)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(items) { ci ->
                    var expanded by remember { mutableStateOf(false) }
                    val maxStock = ci.product.quantity.toIntOrNull() ?: Int.MAX_VALUE
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
                                            "${originalPrice.toInt()} DH",
                                            color = Color.Gray,
                                            textDecoration = TextDecoration.LineThrough,
                                            fontSize = 14.sp
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "${discountedPrice.toInt()} DH",
                                            color = RougeFlora,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                } else {
                                    Text(
                                        "${originalPrice.toInt()} DH",
                                        color = Color.Black,
                                        fontSize = 16.sp
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    IconButton(onClick = { viewModel.dec(ci) }) {
                                        Icon(Icons.Filled.Remove, contentDescription = "Décrementer", tint = Color.Black)
                                    }
                                    Text(
                                        "${ci.quantity}",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.width(32.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    IconButton(
                                        onClick = {
                                            val ok = viewModel.inc(ci)
                                            if (!ok) scope.launch {
                                                snackHostState.showSnackbar("Stock insuffisant pour ${ci.product.name}")
                                            }
                                        },
                                        enabled = !outOfStock
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = "Incrémenter", tint = Color.Black)
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
                                    Icon(Icons.Filled.Delete, contentDescription = "Supprimer produit", tint = Color.Black)
                                }
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight,
                                        contentDescription = if (expanded) "Réduire" else "Détails",
                                        tint = Color.Black
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
                                            Icon(Icons.Filled.Remove, contentDescription = "Décrementer addon", tint = Color.Black)
                                        }
                                        Text("${aq.quantity}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        IconButton(onClick = { viewModel.incAddon(ci, aq) }) {
                                            Icon(Icons.Filled.Add, contentDescription = "Incrémenter addon", tint = Color.Black)
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
                            "Prix Total",
                            fontWeight = FontWeight.Normal,
                            color = RougeFlora,
                            fontSize = 16.sp
                        )
                        Text(
                            String.format("%.2f DH", totalPrice),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    }

                    Button(
                        onClick = { onNavigateToCheckout() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RougeFlora,
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        modifier = Modifier.padding(start = 30.dp)
                    ) {
                        Text(
                            "Passer Commande",
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