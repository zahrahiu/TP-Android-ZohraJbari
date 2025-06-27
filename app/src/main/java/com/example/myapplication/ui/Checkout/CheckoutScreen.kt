package com.example.myapplication.ui.checkout

/* ─────────── Imports Android / Compose ─────────── */
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
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
private val LightRed = Color(0xFFF8E8E8)
private val SuccessGreen = Color(0xFF4CAF50)

data class ShippingMethod(
    val id: String,
    val name: String,
    val iconRes: Int,
    val fee: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CartViewModel = viewModel(),
    onBack: () -> Unit,
    lang: LanguageManager.Instance,
    currentOrder: Order? = null,
    onPay: (orderId: String) -> Unit
) {
    /* ---------- États ---------- */
    val colors = MaterialTheme.colorScheme
    val cartItems = viewModel.items.collectAsState().value
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(currentOrder?.clientName ?: "") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf(currentOrder?.phone ?: "") }
    var address by remember { mutableStateOf(currentOrder?.address ?: "") }

    // Méthodes d'expédition disponibles
    val shippingMethods = listOf(
        ShippingMethod("home", lang.get("home_delivery"), R.drawable.ic_home_delivery, 20.0),
        ShippingMethod("pickup", lang.get("self_pickup"), R.drawable.ic_pickup, 0.0),
        ShippingMethod("gift", lang.get("gift_delivery"), R.drawable.ic_gift, 25.0)
    )

    var selectedShippingMethod by remember { mutableStateOf<ShippingMethod?>(null) }
    var paymentMethod by remember { mutableStateOf("card") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var orderSuccess by remember { mutableStateOf(false) }

    val payEnabled = when (selectedShippingMethod?.id) {
        "pickup" -> name.isNotBlank() && phone.isNotBlank() && !orderSuccess &&
                (paymentMethod == "paypal" ||
                        (paymentMethod == "card" && cardNumber.isNotBlank() && cardExpiry.isNotBlank() && cardCvv.isNotBlank()))
        else -> name.isNotBlank() && phone.isNotBlank() && address.isNotBlank() && !orderSuccess &&
                (paymentMethod == "paypal" ||
                        (paymentMethod == "card" && cardNumber.isNotBlank() && cardExpiry.isNotBlank() && cardCvv.isNotBlank()))
    }

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
            // Section Méthode d'expédition
            item { SectionTitle(lang.get("shipping_method")) }
            item {
                ShippingMethodsGrid(
                    methods = shippingMethods,
                    selectedMethod = selectedShippingMethod,
                    onMethodSelected = { selectedShippingMethod = it },
                    lang = lang
                )
            }

            // Section Informations client (conditionnelle)
            if (selectedShippingMethod != null) {
                item { SectionTitle(lang.get("your_information")) }
                item {
                    when (selectedShippingMethod?.id) {
                        "pickup" -> SelfPickupFields(
                            name = name,
                            email = email,
                            phone = phone,
                            lang = lang
                        ) { n, e, p -> name = n; email = e; phone = p }
                        else -> UserFields(
                            name = name,
                            email = email,
                            phone = phone,
                            address = address,
                            lang = lang
                        ) { n, e, p, a -> name = n; email = e; phone = p; address = a }
                    }
                }
            }

            // Section Méthode de paiement
            if (selectedShippingMethod != null) {
                item { SectionTitle(lang.get("payment_method")) }
                item {
                    PaymentMethodSelector(
                        paymentMethod = paymentMethod,
                        onPaymentMethodChange = { paymentMethod = it },
                        lang = lang
                    )
                }

                if (paymentMethod == "card") {
                    item {
                        CardFields(
                            cardNumber = cardNumber,
                            cardExpiry = cardExpiry,
                            cardCvv = cardCvv,
                            onChange = { num, exp, cvv ->
                                cardNumber = num
                                cardExpiry = exp
                                cardCvv = cvv
                            },
                            lang = lang
                        )
                    }
                }
            }

            // Section Commande
            item { SectionTitle(lang.get("your_order")) }
            items(cartItems) { ci -> CartItemCard(ci, colors, lang) }

            // Section Total
            if (selectedShippingMethod != null) {
                item {
                    TotalPriceCard(
                        totalWithoutDelivery = cartItems.sumOf { ci ->
                            val base = parsePrice(ci.product.price).toDouble()
                            val price = calculateDiscountedPrice(base, ci.product.discountPercent)
                            price * ci.quantity + ci.addons.sumOf { it.addon.price.toDouble() * it.quantity }
                        },
                        deliveryFee = selectedShippingMethod?.fee ?: 0.0,
                        totalWithDelivery = cartItems.sumOf { ci ->
                            val base = parsePrice(ci.product.price).toDouble()
                            val price = calculateDiscountedPrice(base, ci.product.discountPercent)
                            price * ci.quantity + ci.addons.sumOf { it.addon.price.toDouble() * it.quantity }
                        } + (selectedShippingMethod?.fee ?: 0.0),
                        colors = colors,
                        lang = lang
                    )
                }
            }

            // Bouton de paiement
            if (selectedShippingMethod != null && !orderSuccess) {
                item {
                    PayButton(
                        enabled = payEnabled,
                        onClick = {
                            scope.launch {
                                val email = SessionManager.currentUser.value?.email ?: ""

                                val dtoList = cartItems.map { ci ->
                                    OrderItemDto(
                                        productName = ci.product.name,
                                        image = ci.product.image,
                                        unitPrice = parsePrice(ci.product.price).toDouble(),
                                        discountPct = ci.product.discountPercent,
                                        quantity = ci.quantity,
                                        addons = ci.addons.map {
                                            AddonDto(
                                                it.addon.name,
                                                it.addon.price.toDouble(),
                                                it.quantity
                                            )
                                        }
                                    )
                                }

                                val order = Order(
                                    userEmail = email,
                                    clientName = name,
                                    phone = phone,

                                    address = if (selectedShippingMethod?.id == "pickup") "" else address,
                                    itemsJson = Gson().toJson(dtoList),
                                    shippingMethod = selectedShippingMethod?.name ?: "",
                                    shippingFee = selectedShippingMethod?.fee ?: 0.0
                                )
                                OrderRepository.create(order)

                                UserRepository.getUser(email)?.notifications?.add(
                                    Notification(
                                        message = "Votre commande est en attente de confirmation.",
                                        orderId = order.id
                                    )
                                )

                                viewModel.clearCart()
                                orderSuccess = true
                                onPay(order.id)
                            }
                        },
                        colors = colors,
                        lang = lang
                    )
                }
            }

            if (orderSuccess) {
                item {
                    OrderSuccessMessage()
                }
            }
        }
    }
}

@Composable
private fun ShippingMethodsGrid(
    methods: List<ShippingMethod>,
    selectedMethod: ShippingMethod?,
    onMethodSelected: (ShippingMethod) -> Unit,
    lang: LanguageManager.Instance
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        methods.forEach { method ->
            ShippingMethodCard(
                method = method,
                isSelected = selectedMethod?.id == method.id,
                onSelect = { onMethodSelected(method) },
                lang = lang
            )
        }
    }
}

@Composable
private fun ShippingMethodCard(
    method: ShippingMethod,
    isSelected: Boolean,
    onSelect: () -> Unit,
    lang: LanguageManager.Instance
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LightRed else Color.White
        ),
        border = if (isSelected) BorderStroke(1.dp, RougeFlora) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(method.iconRes),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    method.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                if (method.fee > 0) {
                    Text(
                        "${lang.get("delivery_fee")}: ${method.fee} ${lang.get("currency")}",
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        lang.get("free_delivery"),
                        fontSize = 14.sp
                    )
                }
            }

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = RougeFlora
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodSelector(
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    lang: LanguageManager.Instance
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PaymentMethodCard(
            id = "card",
            name = lang.get("credit_card"),
            iconRes = R.drawable.ic_credit_card,
            isSelected = paymentMethod == "card",
            onSelect = { onPaymentMethodChange("card") }
        )

        PaymentMethodCard(
            id = "paypal",
            name = "PayPal",
            iconRes = R.drawable.ic_paypal,
            isSelected = paymentMethod == "paypal",
            onSelect = { onPaymentMethodChange("paypal") }
        )
    }
}

@Composable
private fun PaymentMethodCard(
    id: String,
    name: String,
    iconRes: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LightRed else Color.White
        ),
        border = if (isSelected) BorderStroke(1.dp, RougeFlora) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.width(16.dp))

            Text(
                name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = RougeFlora
                )
            }
        }
    }
}

@Composable
private fun OrderSuccessMessage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Commande réussie !",
            color = SuccessGreen,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Merci pour votre achat.",
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

private fun parsePrice(priceStr: String): Float =
    priceStr.replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0f

private fun calculateDiscountedPrice(price: Double, discountPercent: Int?): Double =
    discountPercent?.let { price * (100 - it) / 100 } ?: price

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

@Composable
private fun SelfPickupFields(
    name: String,
    email: String,
    phone: String,
    lang: LanguageManager.Instance,
    onChange: (String, String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { onChange(it, email, phone) },
            label = { Text(lang.get("full_name")) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        OutlinedTextField(
            value = email,
            onValueChange = { onChange(name, it, phone) },
            label = { Text(lang.get("email")) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { onChange(name, email, it) },
            label = { Text(lang.get("phone")) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
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