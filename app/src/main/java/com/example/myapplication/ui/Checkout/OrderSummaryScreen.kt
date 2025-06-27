package com.example.myapplication.ui.checkout

/* ─── Imports Android / Compose ─── */
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/* ─── Imports projet ─── */
import com.example.myapplication.R
import com.example.myapplication.data.Repository.AddonDto
import com.example.myapplication.data.Repository.OrderItemDto
import com.example.myapplication.data.Repository.OrderRepository
import com.example.myapplication.ui.cart.CartItemUi
import com.example.myapplication.ui.product.screens.LanguageManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val DELIVERY_FEE = 20.0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummaryScreen(
    orderId    : String,
    lang       : LanguageManager.Instance,
    onBackHome : () -> Unit
) {
    /* 1. Récupération de la commande */
    val order = remember(orderId) { OrderRepository.byId(orderId) }

    if (order == null) {
        Scaffold { pad ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(pad),
                Alignment.Center
            ) { Text("Commande introuvable") }
        }
        return
    }

    // 2. Désérialisation avec prise en charge des 2 formats
    val items: List<OrderItemDto> = remember(order.itemsJson) {

        // ---------- essai sur le NOUVEAU format ----------
        val typeNew  = object : TypeToken<List<OrderItemDto>>() {}.type
        val newList  = Gson().fromJson<List<OrderItemDto>>(order.itemsJson, typeNew)

        if (newList.isNotEmpty()) {
            newList
        } else {
            // ---------- sinon → ancien format CartItemUi ----------
            val typeOld = object : TypeToken<List<CartItemUi>>() {}.type
            val oldList = Gson().fromJson<List<CartItemUi>>(order.itemsJson, typeOld)

            oldList.map { ci ->
                OrderItemDto(
                    productName = ci.product.name,
                    image       = ci.product.image,
                    unitPrice   = ci.product.price
                        .replace("[^0-9.]".toRegex(),"")
                        .toDoubleOrNull() ?: 0.0,
                    discountPct = ci.product.discountPercent,
                    quantity    = ci.quantity,
                    addons      = ci.addons.map {
                        AddonDto(it.addon.name,
                            it.addon.price.toDouble(),
                            it.quantity)
                    }
                )
            }
        }
    }


    /* 3. Calculs */
    val (subtotal, totalAmount) = remember(items) {
        val st = items.sumOf { it.effectivePrice() * it.quantity + it.addonsTotal() }
        st to st + DELIVERY_FEE
    }

    /* 4. Animation de suivi d’état */
    var currentStep by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        delay(2_000); currentStep = 1
        delay(2_000); currentStep = 2
    }

    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lang.get("order_summary_title")) },
                navigationIcon = {
                    IconButton(onClick = onBackHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {
            /* Titre */
            Text(
                lang.get("thank_you_order").format(order.clientName),
                color = colors.primary,
                fontSize = 20.sp
            )

            Spacer(Modifier.height(16.dp))
            ClientInfoSection(lang, order.phone, order.address)
            Spacer(Modifier.height(24.dp))
            OrderTrackingSection(lang, currentStep)
            Spacer(Modifier.height(24.dp))
            OrderDetailsSection(lang, items)
            Spacer(Modifier.height(16.dp))
            TotalSection(lang, subtotal, totalAmount)
        }
    }
}

/* ────────── Sous-composants réutilisables ────────── */

@Composable
private fun ClientInfoSection(lang: LanguageManager.Instance, phone: String, address: String) =
    Card {
        Column(Modifier.padding(16.dp)) {
            Text(lang.get("client_info"), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Text("📞 ${lang.get("phone")}: $phone")
            Text("📍 ${lang.get("address")}: $address")
        }
    }

@Composable
private fun OrderTrackingSection(lang: LanguageManager.Instance, currentStep: Int) {
    val colors = MaterialTheme.colorScheme
    Column {
        Text(lang.get("order_status"), style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceEvenly,
            Alignment.CenterVertically
        ) {
            StepIndicator(R.drawable.inprogress,  lang.get("ordered")  , currentStep >= 0)
            Icon(Icons.Default.KeyboardArrowRight, null, tint = colors.primary)
            StepIndicator(R.drawable.shipped,     lang.get("shipped")  , currentStep >= 1)
            Icon(Icons.Default.KeyboardArrowRight, null, tint = colors.primary)
            StepIndicator(R.drawable.delivered,   lang.get("delivered"), currentStep >= 2)
        }
        if (currentStep == 2) {
            Spacer(Modifier.height(16.dp))
            Text(lang.get("order_delivered_success"), color = colors.primary)
        }
    }
}

@Composable
private fun OrderDetailsSection(
    lang  : LanguageManager.Instance,
    items : List<OrderItemDto>
) {
    val colors = MaterialTheme.colorScheme
    Card {
        Column(Modifier.padding(16.dp)) {
            Text(lang.get("order_details"), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            /* Liste des articles */
            items.forEach { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Image(
                        painter = painterResource(getImageResource(item.image)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.small)
                            .border(1.dp, colors.primary, MaterialTheme.shapes.small)
                    )
                    Spacer(Modifier.width(8.dp))

                    Column {
                        Text("- ${item.productName} ×${item.quantity}", fontWeight = FontWeight.SemiBold)
                        item.addons.forEach { ad ->
                            Text("  + ${ad.name} (×${ad.quantity})", fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    val total = item.effectivePrice() * item.quantity + item.addonsTotal()
                    Text(
                        "%.2f DH".format(total),
                        fontWeight = FontWeight.Bold,
                        color      = colors.primary,
                        fontSize   = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TotalSection(lang: LanguageManager.Instance, subtotal: Double, totalAmount: Double) =
    Card {
        val colors = MaterialTheme.colorScheme
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(lang.get("subtotal"))
                Text("%.2f DH".format(subtotal))
            }
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(lang.get("delivery_fee"))
                Text("%.2f DH".format(DELIVERY_FEE))
            }
            Divider(Modifier.padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(lang.get("total"), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "%.2f DH".format(totalAmount),
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = colors.primary
                )
            }
        }
    }

@Composable
fun StepIndicator(iconRes: Int, label: String, isActive: Boolean) {
    val colors = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) colors.primary.copy(alpha = .1f)
                    else           colors.surfaceVariant.copy(alpha = .1f)
                )
                .border(2.dp, if (isActive) colors.primary else colors.outline, CircleShape),
            contentAlignment = Alignment.Center
        ) { Image(painterResource(iconRes), label, Modifier.size(48.dp)) }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 12.sp)
    }
}

/* ────────── Helpers ────────── */

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
