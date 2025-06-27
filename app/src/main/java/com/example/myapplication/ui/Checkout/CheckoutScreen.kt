package com.example.myapplication.ui.checkout

/* ─────────── Imports Android / Compose ─────────── */
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

/* ─────────── Imports Projet ─────────── */
import com.example.myapplication.R
import com.example.myapplication.data.Repository.*
import com.example.myapplication.session.SessionManager
import com.example.myapplication.ui.cart.CartItemUi
import com.example.myapplication.ui.cart.CartViewModel
import com.example.myapplication.ui.product.screens.LanguageManager
import com.google.gson.Gson

/* ─────────── Couleurs réutilisées ─────────── */
private val RougeFlora = Color(0xFFDC4C3E)

/* ═══════════════════════════════════════════════ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CartViewModel = viewModel(),
    onBack:   () -> Unit,
    lang:     LanguageManager.Instance,
    currentOrder: Order? = null,
    onPay    : (orderId: String) -> Unit
) {
    /* ---------- États ---------- */
    val colors   = MaterialTheme.colorScheme
    val cartItems = viewModel.items.collectAsState().value
    val scope    = rememberCoroutineScope()

    var name    by remember { mutableStateOf(currentOrder?.clientName ?: "") }
    var phone   by remember { mutableStateOf(currentOrder?.phone       ?: "") }
    var address by remember { mutableStateOf(currentOrder?.address     ?: "") }

    var orderSuccess by remember { mutableStateOf(false) }

    val payEnabled = name.isNotBlank() && phone.isNotBlank() &&
            address.isNotBlank() && !orderSuccess

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(lang.get("checkout"), color = colors.primary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, lang.get("back"))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colors.background)
            )
        },
        containerColor = colors.background
    ) { pad ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .background(colors.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* --- Infos client --- */
            item { SectionTitle(lang.get("your_information")) }
            item {
                UserFields(
                    name = name, email = "",
                    phone = phone, address = address,
                    lang = lang
                ) { n, _, p, a -> name = n; phone = p; address = a }
            }

            /* --- Récapitulatif --- */
            item { SectionTitle(lang.get("your_order")) }
            items(cartItems) { ci -> CartItemCard(ci, colors, lang) }

            /* --- Total --- */
            item {
                val subTotal = cartItems.sumOf { ci ->
                    val base = parsePrice(ci.product.price).toDouble()
                    val price = calculateDiscountedPrice(base, ci.product.discountPercent)
                    price * ci.quantity + ci.addons.sumOf { it.addon.price.toDouble() * it.quantity }
                }
                val delivery = 20.0
                TotalPriceCard(
                    totalWithoutDelivery = subTotal,
                    deliveryFee          = delivery,
                    totalWithDelivery    = subTotal + delivery,
                    colors = colors, lang = lang
                )
            }

            if (orderSuccess) {
                item {
                    Text(
                        "Commande réussie ! Merci pour votre achat.",
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                PayButton(
                    enabled = payEnabled,
                    onClick = {
                        scope.launch {
                            val email = SessionManager.currentUser.value?.email ?: ""

                            /* ----- on prépare la liste OrderItemDto ----- */
                            val dtoList = cartItems.map { ci ->
                                OrderItemDto(
                                    productName = ci.product.name,
                                    image       = ci.product.image,
                                    unitPrice   = parsePrice(ci.product.price).toDouble(),
                                    discountPct = ci.product.discountPercent,
                                    quantity    = ci.quantity,
                                    addons      = ci.addons.map {
                                        AddonDto(it.addon.name,
                                            it.addon.price.toDouble(),
                                            it.quantity)
                                    }
                                )
                            }

                            /* ----- on crée la commande ----- */
                            val order = Order(
                                userEmail  = email,
                                clientName = name,
                                phone      = phone,
                                address    = address,
                                itemsJson  = Gson().toJson(dtoList)
                            )
                            OrderRepository.create(order)

                            /* ----- notification ----- */
                            UserRepository.getUser(email)?.notifications?.add(
                                Notification(
                                    message = "Votre commande est en attente de confirmation.",
                                    orderId = order.id
                                )
                            )

                            viewModel.clearCart()
                            orderSuccess = true

                            /* 👉 on retourne l'id de la nouvelle commande */
                            onPay(order.id)
                        }
                    },
                    colors = colors,
                    lang   = lang
                )
            }
        }
    }
}

/* ─────────── Helpers (استعمل كودك الأصلي) ─────────── */

private fun parsePrice(priceStr: String): Float =
    priceStr.replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0f

private fun calculateDiscountedPrice(price: Double, discountPercent: Int?): Double =
    discountPercent?.let { price * (100 - it) / 100 } ?: price

/* =========== بقية الـ composables كما هي: SectionTitle, UserFields, CartItemCard, … =========== */


// Composable components
@Composable
private fun DeliveryMethodSection(
    selfPickup: Boolean,
    onSelfPickupChange: (Boolean) -> Unit,
    lang: LanguageManager.Instance
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(lang.get("self_pickup"), fontWeight = FontWeight.SemiBold)
        Switch(
            checked = selfPickup,
            onCheckedChange = onSelfPickupChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = RougeFlora,
                checkedTrackColor = RougeFlora.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SectionCollapsible(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = RougeFlora
            )
        }
    }
}

@Composable
private fun UserFields(
    name: String,
    email: String,
    phone: String,
    address: String,
    lang: LanguageManager.Instance,
    onChange: (String, String, String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { onChange(it, email, phone, address) },
            label = { Text(lang.get("full_name")) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        OutlinedTextField(
            value = email,
            onValueChange = { onChange(name, it, phone, address) },
            label = { Text(lang.get("email")) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { onChange(name, email, it, address) },
            label = { Text(lang.get("phone")) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        OutlinedTextField(
            value = address,
            onValueChange = { onChange(name, email, phone, it) },
            label = { Text(lang.get("address")) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
private fun PaymentMethodSelector(
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    lang: LanguageManager.Instance
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = paymentMethod == "card",
            onClick = { onPaymentMethodChange("card") },
            colors = RadioButtonDefaults.colors(selectedColor = RougeFlora)
        )
        Text(lang.get("card"), Modifier.padding(end = 16.dp))
        RadioButton(
            selected = paymentMethod == "paypal",
            onClick = { onPaymentMethodChange("paypal") },
            colors = RadioButtonDefaults.colors(selectedColor = RougeFlora)
        )
        Text("paypal")
    }
}

@Composable
private fun CardFields(
    cardNumber: String,
    cardExpiry: String,
    cardCvv: String,
    onChange: (String, String, String) -> Unit,
    lang: LanguageManager.Instance
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = cardNumber,
            onValueChange = { onChange(it, cardExpiry, cardCvv) },
            label = { Text(lang.get("card_number")) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = cardExpiry,
                onValueChange = { onChange(cardNumber, it, cardCvv) },
                label = { Text(lang.get("expiry_date")) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            OutlinedTextField(
                value = cardCvv,
                onValueChange = { onChange(cardNumber, cardExpiry, it) },
                label = { Text(lang.get("cvv")) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItemUi,
    colors: ColorScheme,
    lang: LanguageManager.Instance
) {
    val originalPrice = parsePrice(cartItem.product.price)
    val discountedPrice = calculateDiscountedPrice(originalPrice.toDouble(), cartItem.product.discountPercent)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .height(IntrinsicSize.Min)
        ) {
            Image(
                painter = painterResource(getImageResource(cartItem.product.image)),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    cartItem.product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                if (cartItem.product.discountPercent != null) {
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
                        fontSize = 16.sp
                    )
                }

                Text(
                    "${lang.get("quantity")}: ${cartItem.quantity}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (cartItem.addons.isNotEmpty()) {
                    Text(
                        "${lang.get("add_ons")}:",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    cartItem.addons.forEach { addon ->
                        Text(
                            "- ${addon.addon.name} (x${addon.quantity})",
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Text(
                text = String.format("%.2f ${lang.get("currency")}",
                    (discountedPrice * cartItem.quantity) +
                            cartItem.addons.sumOf { it.addon.price.toDouble() * it.quantity }),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = RougeFlora
            )
        }
    }
}

@Composable
private fun TotalPriceCard(
    totalWithoutDelivery: Double,
    deliveryFee: Double,
    totalWithDelivery: Double,
    colors: ColorScheme,
    lang: LanguageManager.Instance
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${lang.get("subtotal")}:", fontSize = 16.sp)
                Text(String.format("%.2f ${lang.get("currency")}", totalWithoutDelivery), fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${lang.get("delivery")}:", fontSize = 16.sp)
                Text(String.format("%.2f ${lang.get("currency")}", deliveryFee), fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${lang.get("total")}:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    String.format("%.2f ${lang.get("currency")}", totalWithDelivery),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }
    }
}

@Composable
private fun PayButton(
    enabled: Boolean,
    onClick: () -> Unit,
    colors: ColorScheme,
    lang: LanguageManager.Instance
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = Color.White
        )
    ) {
        Text(lang.get("pay"), fontWeight = FontWeight.SemiBold)
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