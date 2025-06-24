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
import com.example.myapplication.R
import com.example.myapplication.ui.cart.CartItemUi
import com.example.myapplication.ui.cart.CartViewModel
import kotlinx.coroutines.launch

val RougeFlora = Color(0xFFDC4C3E)
val BeigeFlora = Color(0xFFFFF8F0)
val BeigeBackground = Color(0xFFFFFBF7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onPay: (name: String, phone: String, address: String) -> Unit
) {
    val items = viewModel.items.collectAsState().value
    val scope = rememberCoroutineScope()

    var selfPickup by remember { mutableStateOf(true) }
    var senderExpanded by remember { mutableStateOf(false) }
    var recipientExpanded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    var senderName by remember { mutableStateOf("") }
    var senderEmail by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }

    var recipientName by remember { mutableStateOf("") }
    var recipientEmail by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }

    var paymentMethod by remember { mutableStateOf("card") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }

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

    val deliveryFee = 20.0
    val totalWithoutDelivery = remember(items) {
        items.sumOf { ci ->
            val originalPrice = parsePrice(ci.product.price).toDouble()
            val productPrice = if (ci.product.discountPercent != null) {
                originalPrice * (100 - ci.product.discountPercent) / 100
            } else {
                originalPrice
            }
            val addonsPrice = ci.addons.sumOf { it.addon.price.toDouble() * it.quantity }
            productPrice * ci.quantity + addonsPrice
        }
    }
    val totalWithDelivery = totalWithoutDelivery + deliveryFee

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Finaliser la commande", color = RougeFlora, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BeigeFlora)
            )
        },
        containerColor = BeigeBackground
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .background(BeigeBackground),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Retrait en main propre", fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = selfPickup,
                        onCheckedChange = { selfPickup = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RougeFlora,
                            checkedTrackColor = RougeFlora.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            if (selfPickup) {
                item { SectionTitle("Vos coordonnées") }
                item {
                    UserFields(name, email, phone, address) { n, e, p, a ->
                        name = n; email = e; phone = p; address = a
                    }
                }
            } else {
                item { SectionCollapsible("Expéditeur", senderExpanded) { senderExpanded = !senderExpanded } }
                if (senderExpanded) {
                    item {
                        UserFields(senderName, senderEmail, senderPhone, address) { n, e, p, _ ->
                            senderName = n; senderEmail = e; senderPhone = p
                        }
                    }
                }
                item { SectionCollapsible("Destinataire", recipientExpanded) { recipientExpanded = !recipientExpanded } }
                if (recipientExpanded) {
                    item {
                        UserFields(recipientName, recipientEmail, recipientPhone, "") { n, e, p, _ ->
                            recipientName = n; recipientEmail = e; recipientPhone = p
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Commentaire") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            }

            item { SectionTitle("Méthode de paiement") }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = paymentMethod == "card",
                        onClick = { paymentMethod = "card" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = RougeFlora
                        )
                    )
                    Text("Carte", Modifier.padding(end = 16.dp))
                    RadioButton(
                        selected = paymentMethod == "paypal",
                        onClick = { paymentMethod = "paypal" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = RougeFlora
                        )
                    )
                    Text("PayPal")
                }
            }
            if (paymentMethod == "card") {
                item {
                    CardFields(cardNumber, cardExpiry, cardCvv) { cn, ce, cv ->
                        cardNumber = cn; cardExpiry = ce; cardCvv = cv
                    }
                }
            }

            item { SectionTitle("Votre commande") }
            items(items) { ci ->
                val originalPrice = parsePrice(ci.product.price)
                val discountedPrice = calculateDiscountedPrice(originalPrice, ci.product.discountPercent)

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
                            painter = painterResource(getImageResource(ci.product.image)),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(end = 12.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                ci.product.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
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
                                    fontSize = 16.sp
                                )
                            }

                            Text(
                                "Quantité: ${ci.quantity}",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            if (ci.addons.isNotEmpty()) {
                                Text(
                                    "Suppléments:",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                ci.addons.forEach { addon ->
                                    Text(
                                        "- ${addon.addon.name} (x${addon.quantity})",
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Text(
                            text = String.format("%.2f DH", (discountedPrice * ci.quantity) +
                                    ci.addons.sumOf { it.addon.price.toDouble() * it.quantity }),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = RougeFlora
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sous-total:", fontSize = 16.sp)
                            Text(String.format("%.2f DH", totalWithoutDelivery), fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Livraison:", fontSize = 16.sp)
                            Text(String.format("%.2f DH", deliveryFee), fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Total:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(
                                String.format("%.2f DH", totalWithDelivery),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = RougeFlora
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()) {
                            scope.launch {
                                onPay(name, phone, address)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RougeFlora,
                        contentColor = Color.White
                    )
                ) {
                    Text("Payer", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun SectionCollapsible(title: String, expanded: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
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
    onChange: (String, String, String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { onChange(it, email, phone, address) },
            label = { Text("Nom complet") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        OutlinedTextField(
            value = email,
            onValueChange = { onChange(name, it, phone, address) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { onChange(name, email, it, address) },
            label = { Text("Téléphone") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        OutlinedTextField(
            value = address,
            onValueChange = { onChange(name, email, phone, it) },
            label = { Text("Adresse") },
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
    number: String,
    expiry: String,
    cvv: String,
    onChange: (String, String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = number,
            onValueChange = { onChange(it, expiry, cvv) },
            label = { Text("Numéro de carte") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = expiry,
                onValueChange = { onChange(number, it, cvv) },
                label = { Text("MM/YY") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            OutlinedTextField(
                value = cvv,
                onValueChange = { onChange(number, expiry, it) },
                label = { Text("CVV") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
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