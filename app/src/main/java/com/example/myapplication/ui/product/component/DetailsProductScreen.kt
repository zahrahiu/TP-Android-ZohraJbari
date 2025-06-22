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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.data.Entities.Addon
import com.example.myapplication.data.Entities.Product
import kotlinx.coroutines.launch

/* الألوان */
val JauneDeNaples = Color(0xFFFADA5E)
val GrisPetitGris = Color(0xFF8E8E93)
val GrisClair     = Color(0xFFF5F5F5)
val GrisFonce     = Color(0xFF3A3A3A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    product: Product,
    navController: NavController? = null,
    onAddToCart: (List<Pair<Addon, Int>>) -> Boolean = { true }
) {
    val imageRes = when (product.image) {
        "hibiscus.jpg" -> R.drawable.hibiscus
        "lavender.jpg" -> R.drawable.lavender
        "lily.jpg"     -> R.drawable.lily
        "pansy.jpg"    -> R.drawable.pansy
        else           -> R.drawable.img1
    }

    var showSheet by remember { mutableStateOf(false) }
    val snackState = remember { SnackbarHostState() }
    val scope      = rememberCoroutineScope()

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
        snackbarHost = { SnackbarHost(snackState) },
        containerColor = GrisClair
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(300.dp),
                shape  = RoundedCornerShape(16.dp),
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

            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            product.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = GrisFonce,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(JauneDeNaples.copy(.3f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(product.price, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text("Description", style = MaterialTheme.typography.titleLarge, color = GrisPetitGris)
                    Text(
                        product.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = GrisPetitGris.copy(.8f),
                        textAlign = TextAlign.Justify,
                        lineHeight = 24.sp
                    )

                    Spacer(Modifier.height(24.dp))

                    DetailRow("Type",     product.type,                 GrisPetitGris, JauneDeNaples)
                    DetailRow("Couleurs", product.colors.joinToString(),GrisPetitGris, JauneDeNaples)
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { showSheet = true },
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(JauneDeNaples, GrisFonce)
            ) { Text("Ajouter au panier • ${product.price}") }
        }
    }

    AddOnsBottomSheet(
        open = showSheet,
        onDismiss = { showSheet = false },
        onValidate = { selected ->
            val ok = onAddToCart(selected)
            if (!ok) {
                scope.launch { snackState.showSnackbar("Stock insuffisant pour ${product.name}") }
            } else {
                showSheet = false
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String, textColor: Color, accentColor: Color) {
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
