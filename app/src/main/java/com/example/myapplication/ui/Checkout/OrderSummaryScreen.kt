package com.example.myapplication.ui.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.cart.CartItemUi
import kotlinx.coroutines.delay

// ---------------------------- Couleurs du thème ----------------------------
val PrimaryColor = Color(0xFFDC4C3E)   // Rouge Flora
val SecondaryColor = Color(0xFFFFF8F0) // Beige Flora
val BackgroundColor = Color(0xFFFFFBF7) // Beige clair

// -------------------------- Constantes -------------------------
private val StepOuterSize = 80.dp   // Taille du cercle extérieur
private val StepInnerIconSize = 48.dp // Taille de l'icône à l'intérieur
private const val DELIVERY_FEE = 20.0 // Frais de livraison fixes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummaryScreen(
    userName: String,
    phone: String,
    address: String,
    items: List<CartItemUi>,
    onBackHome: () -> Unit
) {
    // Calcul du total avec frais de livraison
    val (subtotal, totalAmount) = remember(items) {
        val st = computeSubtotal(items)
        Pair(st, st + DELIVERY_FEE)
    }

    // Animation des étapes de commande
    var currentStep by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        delay(2_000); currentStep = 1
        delay(2_000); currentStep = 2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Récapitulatif de commande", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackHome) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SecondaryColor)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // En-tête
            Text(
                "Merci pour votre commande, $userName !",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PrimaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section informations client
            ClientInfoSection(phone, address)

            Spacer(modifier = Modifier.height(24.dp))

            // Suivi de commande
            OrderTrackingSection(currentStep)

            Spacer(modifier = Modifier.height(24.dp))

            // Détails de la commande
            OrderDetailsSection(items)

            Spacer(modifier = Modifier.height(16.dp))

            // Total avec décomposition
            TotalSection(subtotal, totalAmount)

            Spacer(modifier = Modifier.height(24.dp))



        }
    }
}

@Composable
private fun ClientInfoSection(phone: String, address: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SecondaryColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Informations client", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text("📞 $phone", fontSize = 16.sp)
            Text("📍 $address", fontSize = 16.sp)
        }
    }
}

@Composable
private fun OrderTrackingSection(currentStep: Int) {
    Column {
        Text("Statut de votre commande:", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepIndicator(R.drawable.inprogress, "Commandé", currentStep >= 0)
            Icon(Icons.Default.KeyboardArrowRight, null, tint = PrimaryColor)
            StepIndicator(R.drawable.shipped, "Expédié", currentStep >= 1)
            Icon(Icons.Default.KeyboardArrowRight, null, tint = PrimaryColor)
            StepIndicator(R.drawable.delivered, "Livré", currentStep >= 2)
        }

        if (currentStep == 2) {
            Spacer(Modifier.height(16.dp))
            Text(
                "✅ Votre commande a été livrée avec succès !",
                color = PrimaryColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OrderDetailsSection(items: List<CartItemUi>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SecondaryColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Détails de la commande:", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Image(
                        painter = painterResource(getImageResource(item.product.image)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(MaterialTheme.shapes.small)
                            .border(1.dp, PrimaryColor, MaterialTheme.shapes.small)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("- ${item.product.name} x${item.quantity}")
                        item.addons.forEach { addon ->
                            Text("  + ${addon.addon.name} (x${addon.quantity})", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalSection(subtotal: Double, totalAmount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SecondaryColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Sous-total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sous-total:")
                Text("${"%.2f".format(subtotal)} DH")
            }

            // Frais de livraison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Frais de livraison:")
                Text("${"%.2f".format(DELIVERY_FEE)} DH")
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.LightGray
            )

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "${"%.2f".format(totalAmount)} DH",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryColor
                )
            }
        }
    }
}

@Composable
fun StepIndicator(
    iconRes: Int,
    label: String,
    isActive: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(StepOuterSize)
                .clip(CircleShape)
                .background(if (isActive) PrimaryColor.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.1f))
                .border(2.dp, if (isActive) PrimaryColor else Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(StepInnerIconSize))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 12.sp)
    }
}

private fun getImageResource(name: String): Int = when (name) {
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

private fun computeSubtotal(items: List<CartItemUi>): Double {
    return items.sumOf { item ->
        // Conversion du prix du produit (String to Double)
        val productPrice = item.product.price
            .replace("[^0-9.]".toRegex(), "") // Nettoyage
            .toDoubleOrNull() ?: 0.0

        // Application de la réduction si elle existe
        val discountedPrice = item.product.discountPercent?.let { discount ->
            productPrice * (100 - discount) / 100
        } ?: productPrice

        // Calcul du total pour le produit
        val productTotal = discountedPrice * item.quantity

        // Calcul des addons (Float to Double)
        val addonsTotal = item.addons.sumOf { addon ->
            addon.addon.price.toDouble() * addon.quantity
        }

        productTotal + addonsTotal
    }
}