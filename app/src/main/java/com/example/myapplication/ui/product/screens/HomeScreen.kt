package com.example.emtyapp.ui.product.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.product.ProductIntent
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.ProductsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ProductViewModel = viewModel(),
    onNavigateToDetails: (String) -> Unit
) {
    // ⬇️ state ديال الفيو موديل
    val state by viewModel.state.collectAsState()

    // ⬇️ state ديال البحث
    var searchQuery by remember { mutableStateOf("") }

    // ⬇️ نحمّلو المنتجات أول مرّة
    LaunchedEffect(Unit) {
        viewModel.handleIntent(ProductIntent.LoadProducts)
    }

    // ⬇️ فلترة المنتجات حسب الاسم
    val filteredProducts = state.products.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🌸 Flora Boutique",
                        color = Color(0xFFDC4C3E),
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF8F0)
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("🏡", fontSize = 22.sp) },
                    label = { Text("Accueil") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("❤️", fontSize = 22.sp) },
                    label = { Text("Favoris") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("🛒", fontSize = 22.sp) },
                    label = { Text("Panier") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("👩", fontSize = 22.sp) },
                    label = { Text("Profil") }
                )
            }
        },
        containerColor = Color(0xFFFFFBF7),
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Bienvenue, 🌷",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037)
                )

                Text(
                    text = "Trouve ta fleur préférée !",
                    fontSize = 16.sp,
                    color = Color(0xFF8D6E63),
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                /* 🔍 حقل البحث */
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("🔍 Rechercher une fleur...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFDC4C3E),
                        focusedBorderColor  = Color(0xFFDC4C3E),
                        cursorColor         = Color(0xFFDC4C3E)
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp)
                )

                /* 🌀 حمّالة حالات اللودينگ / الخطأ / النتيجة */
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    state.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Erreur : ${state.error}",
                                color = Color.Red
                            )
                        }
                    }

                    else -> {
                        ProductsList(
                            products = filteredProducts,
                            onNavigateToDetails = onNavigateToDetails
                        )

                        if (filteredProducts.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⛔ ما كاين حتى منتوج بهاد الاسم")
                            }
                        }
                    }
                }
            }
        }
    )
}
