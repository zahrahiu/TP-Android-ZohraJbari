package com.example.myapplication.ui.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.myapplication.R
import com.example.myapplication.ui.cart.CartItemUi
import com.example.myapplication.ui.cart.CartViewModel
import com.example.myapplication.ui.product.screens.LanguageManager
import kotlinx.coroutines.launch

val RougeFlora = Color(0xFFDC4C3E)
val BeigeFlora = Color(0xFFFFF8F0)
val BeigeBackground = Color(0xFFFFFBF7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CartViewModel = viewModel(),
    onBack: () -> Unit,
    lang: LanguageManager.Instance,
    onPay: (name: String, phone: String, address: String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val items = viewModel.items.collectAsState().value
    val scope = rememberCoroutineScope()

    // State variables
    var selfPickup by remember { mutableStateOf(true) }
    var senderExpanded by remember { mutableStateOf(false) }
    var recipientExpanded by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("card") }

    // User information
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    // Sender information
    var senderName by remember { mutableStateOf("") }
    var senderEmail by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }

    // Recipient information
    var recipientName by remember { mutableStateOf("") }
    var recipientEmail by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }

    // Payment information
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }

    // Calculate prices
    val deliveryFee = 20.0
    val totalWithoutDelivery = remember(items) {
        items.sumOf { ci ->
            val originalPrice = parsePrice(ci.product.price).toDouble()
            val productPrice = calculateDiscountedPrice(originalPrice, ci.product.discountPercent)
            val addonsPrice = ci.addons.sumOf { it.addon.price.toDouble() * it.quantity }
            productPrice * ci.quantity + addonsPrice
        }
    }
    val totalWithDelivery = totalWithoutDelivery + deliveryFee

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(lang.get("checkout"), color = colors.primary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = lang.get("back"))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colors.background)
            )
        },
        containerColor = colors.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colors.background),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Delivery Method Section
            item {
                DeliveryMethodSection(
                    selfPickup = selfPickup,
                    onSelfPickupChange = { selfPickup = it },
                    lang = lang
                )
            }

            // User Information Section
            if (selfPickup) {
                item { SectionTitle(lang.get("your_information")) }
                item {
                    UserFields(
                        name = name,
                        email = email,
                        phone = phone,
                        address = address,
                        lang = lang,
                        onChange = { n, e, p, a ->
                            name = n; email = e; phone = p; address = a
                        }
                    )
                }
            } else {
                // Sender Information
                item {
                    SectionCollapsible(
                        title = lang.get("sender"),
                        expanded = senderExpanded,
                        onToggle = { senderExpanded = !senderExpanded }
                    )
                }
                if (senderExpanded) {
                    item {
                        UserFields(
                            name = senderName,
                            email = senderEmail,
                            phone = senderPhone,
                            address = "",
                            lang = lang,
                            onChange = { n, e, p, _ ->
                                senderName = n; senderEmail = e; senderPhone = p
                            }
                        )
                    }
                }

                // Recipient Information
                item {
                    SectionCollapsible(
                        title = lang.get("recipient"),
                        expanded = recipientExpanded,
                        onToggle = { recipientExpanded = !recipientExpanded }
                    )
                }
                if (recipientExpanded) {
                    item {
                        UserFields(
                            name = recipientName,
                            email = recipientEmail,
                            phone = recipientPhone,
                            address = "",
                            lang = lang,
                            onChange = { n, e, p, _ ->
                                recipientName = n; recipientEmail = e; recipientPhone = p
                            }
                        )
                    }
                }

                // Comment Field
                item {
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text(lang.get("comment")) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            }

            // Payment Method Section
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
                        onChange = { cn, ce, cv ->
                            cardNumber = cn; cardExpiry = ce; cardCvv = cv
                        },
                        lang = lang
                    )
                }
            }

            // Order Summary Section
            item { SectionTitle(lang.get("your_order")) }
            items(items) { cartItem ->
                CartItemCard(
                    cartItem = cartItem,
                    colors = colors,
                    lang = lang
                )
            }

            // Total Price Section
            item {
                TotalPriceCard(
                    totalWithoutDelivery = totalWithoutDelivery,
                    deliveryFee = deliveryFee,
                    totalWithDelivery = totalWithDelivery,
                    colors = colors,
                    lang = lang
                )
            }

            // Pay Button
            item {
                PayButton(
                    enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),
                    onClick = {
                        scope.launch {
                            onPay(name, phone, address)
                        }
                    },
                    colors = colors,
                    lang = lang
                )
            }
        }
    }
}

// Helper functions
private fun parsePrice(priceStr: String): Float {
    return priceStr.replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0f
}

private fun calculateDiscountedPrice(price: Double, discountPercent: Int?): Double {
    return if (discountPercent != null) {
        price * (100 - discountPercent) / 100
    } else {
        price
    }
}

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