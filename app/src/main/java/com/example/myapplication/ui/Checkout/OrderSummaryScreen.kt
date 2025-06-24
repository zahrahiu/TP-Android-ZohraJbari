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

// Nouvelles couleurs
val PrimaryColor = Color(0xFFDC4C3E) // Rouge Flora
val SecondaryColor = Color(0xFFFFF8F0) // Beige Flora
val BackgroundColor = Color(0xFFFFFBF7) // Beige clair
val SuccessColor = Color(0xFF4CAF50) // Vert pour succès
val InfoColor = Color(0xFF2196F3) // Bleu pour informations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummaryScreen(
    userName: String,
    phone: String,
    address: String,
    items: List<CartItemUi>,
    total: Double,
    onBackHome: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        delay(2000)
        currentStep = 1
        delay(2000)
        currentStep = 2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Récapitulatif de commande", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackHome) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryColor
                )
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
            Text(
                "Merci pour votre commande, $userName !",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PrimaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section informations client
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SecondaryColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_favorite_outline),
                            contentDescription = "Client",
                            modifier = Modifier.size(24.dp)
                                   )
                                    Text("Informations client", fontWeight = FontWeight.SemiBold)
                    }

                    Text("📞 $phone", fontSize = 16.sp)
                    Text("📍 $address", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Suivi de commande
            Text("Statut de votre commande:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepIndicator(
                    iconRes = R.drawable.inprogress,
                    label = "Commandé",
                    isActive = currentStep >= 0,
                    isCompleted = currentStep > 0
                )

                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = PrimaryColor
                )

                StepIndicator(
                    iconRes = R.drawable.shipped,
                    label = "Expédié",
                    isActive = currentStep >= 1,
                    isCompleted = currentStep > 1
                )

                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = PrimaryColor
                )

                StepIndicator(
                    iconRes = R.drawable.delivered,
                    label = "Livré",
                    isActive = currentStep >= 2,
                    isCompleted = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Détails de la commande
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SecondaryColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Détails de la commande:", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

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
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("- ${item.product.name} x${item.quantity}")
                                if (item.addons.isNotEmpty()) {
                                    item.addons.forEach { addon ->
                                        Text(
                                            "  + ${addon.addon.name} (x${addon.quantity})",
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SecondaryColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Total: ${String.format("%.2f", total)} DH",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PrimaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBackHome,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = Color.White
                )
            ) {
                Text("Retour à l'accueil", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun StepIndicator(
    iconRes: Int,
    label: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) PrimaryColor.copy(alpha = 0.1f)
                    else Color.LightGray.copy(alpha = 0.1f)
                )
                .border(
                    width = 2.dp,
                    color = if (isActive) PrimaryColor else Color.LightGray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Completed",
                    tint = PrimaryColor,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = label,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
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