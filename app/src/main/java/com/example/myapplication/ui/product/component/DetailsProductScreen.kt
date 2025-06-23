package com.example.myapplication.ui.product.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/* Palette */
val JauneDeNaples = Color(0xFFFADA5E)
val GrisPetitGris = Color(0xFF8E8E93)
val GrisClair = Color(0xFFF5F5F5)
val GrisFonce = Color(0xFF3A3A3A)
val RedPromo = Color(0xFFE53935)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    product: Product,
    navController: NavController? = null,
    onAddToCart: (List<Pair<Addon, Int>>) -> Boolean = { true }
) {
    /* ----------- Image ----------- */
    val imageRes = when (product.image) {
        "hibiscus.jpg" -> R.drawable.hibiscus
        "lavender.jpg" -> R.drawable.lavender
        "lily.jpg" -> R.drawable.lily
        "pansy.jpg" -> R.drawable.pansy
        else -> R.drawable.img1
    }

    var showSheet by remember { mutableStateOf(false) }
    val snackHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Calcul du prix avec promotion
    fun parsePrice(priceStr: String): Float {
        return priceStr.replace(Regex("[^0-9.]"), "").toFloatOrNull() ?: 0f
    }

    val oldPrice = parsePrice(product.price)
    val newPrice = product.discountPercent?.let { discount ->
        oldPrice * (100 - discount) / 100
    }

    // Compte à rebours
    val remainingTime by produceState(initialValue = calculateRemainingTime(product.offerEndEpochMillis),
        product.offerEndEpochMillis) {
        while (product.offerEndEpochMillis != null) {
            value = calculateRemainingTime(product.offerEndEpochMillis)
            delay(60_000) // Mise à jour chaque minute
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Détails Produit", color = GrisFonce, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController?.popBackStack() },
                        modifier = Modifier
                            .background(JauneDeNaples.copy(.2f), CircleShape)
                            .padding(6.dp)
                    ) { Icon(Icons.Default.ArrowBack, null, tint = GrisFonce) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackHost) },
        containerColor = GrisClair
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
        ) {
            /* -------- Image card -------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(JauneDeNaples.copy(.1f))
                ) {
                    Image(
                        painter = painterResource(imageRes),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .padding(8.dp)
                    )
                }

                // Badge de promotion
                if (product.discountPercent != null && remainingTime != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(RedPromo, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "-${product.discountPercent}%",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            /* -------- Detail card -------- */
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    /* Titre + Prix */
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            product.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = GrisFonce,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        if (newPrice != null) {
                            Column {
                                Text(
                                    text = "${oldPrice.toInt()} DH",
                                    color = GrisPetitGris,
                                    fontSize = 16.sp,
                                    textDecoration = TextDecoration.LineThrough
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${newPrice.toInt()} DH",
                                        color = RedPromo,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    remainingTime?.let {
                                        Text(
                                            text = "• $it",
                                            color = RedPromo,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "${oldPrice.toInt()} DH",
                                color = GrisFonce,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    /* ----------- Quantité disponible ----------- */
                    Spacer(Modifier.height(12.dp))
                    DetailRow(
                        label = "Quantité",
                        value = product.quantity,
                        textColor = GrisPetitGris,
                        accentColor = JauneDeNaples
                    )

                    Spacer(Modifier.height(24.dp))

                    /* Description */
                    Text("Description", style = MaterialTheme.typography.titleLarge, color = GrisPetitGris)
                    Text(
                        product.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = GrisPetitGris.copy(.8f),
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp
                    )

                    Spacer(Modifier.height(24.dp))
                    DetailRow("Type", product.type, GrisPetitGris, JauneDeNaples)
                    DetailRow("Couleurs", product.colors.joinToString(), GrisPetitGris, JauneDeNaples)
                }
            }

            Spacer(Modifier.weight(1f))

            /* -------- Bouton Ajouter -------- */
            Button(
                onClick = { showSheet = true },
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(JauneDeNaples, GrisFonce)
            ) {
                Text(
                    text = if (newPrice != null) {
                        "Ajouter au panier • ${newPrice.toInt()} DH"
                    } else {
                        "Ajouter au panier • ${oldPrice.toInt()} DH"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    /* -------- Bottom-sheet des addons -------- */
    AddOnsBottomSheet(
        open = showSheet,
        onDismiss = { showSheet = false },
        onValidate = { selected ->
            val ok = onAddToCart(selected)
            if (!ok) {
                scope.launch { snackHost.showSnackbar("Stock insuffisant pour ${product.name}") }
            } else {
                showSheet = false
            }
        }
    )
}

private fun calculateRemainingTime(endTime: Long?): String? {
    if (endTime == null) return null

    val now = System.currentTimeMillis()
    if (now >= endTime) return null

    val diff = endTime - now

    val days = TimeUnit.MILLISECONDS.toDays(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60

    return when {
        days > 0 -> "${days}j ${hours}h"
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}

/* ------------ Ligne clé/valeur stylisée ------------ */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    textColor: Color,
    accentColor: Color
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = textColor)
        Text(
            value,
            color = GrisFonce,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(.2f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}