package com.example.myapplication.navigator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.Product
import com.example.myapplication.R

object Routes {
    const val Home = "home"
    const val ProductDetails = "productDetails"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Home) {
        composable(Routes.Home) {
            HomeScreen(onNavigateToDetails = { productId ->
                navController.navigate("${Routes.ProductDetails}/$productId")
            })
        }
        composable(
            "${Routes.ProductDetails}/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            DetailsScreen(productId = productId)
        }
    }
}

@Composable
fun HomeScreen(onNavigateToDetails: (String) -> Unit) {
    val products = listOf(
        Product("1", "Sunflower", "A radiant sunflower, symbolizing happiness and positivity with its bright yellow petals.", 100.0, 8),
        Product("2", "Lily", "A delicate lily that represents purity, renewal, and refined beauty.", 120.0, 14),
        Product("3", "Hibiscus", "A vibrant hibiscus known for its tropical charm and bold colors.", 85.0, 20),
        Product("4", "Tulip", "A graceful tulip, perfect for expressing elegance and affection.", 150.0, 5),
        Product("5", "Pansy", "A charming pansy with velvety petals, symbolizing thoughtful remembrance.", 200.0, 17),
        Product("6", "Lavender", "Fragrant lavender, soothing and calming, ideal for peace and relaxation.", 50.0, 9)
    )


    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🌸زهراء🌸",
                        color = Color(0xFFD81B60),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color(0xFFD81B60))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Text("🏠", fontSize = 20.sp) }, label = { Text("Accueil") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Text("❤️", fontSize = 20.sp) }, label = { Text("Favoris") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Text("🧑", fontSize = 20.sp) }, label = { Text("Profil") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Text("🛒", fontSize = 20.sp) }, label = { Text("Panier") })
            }
        },
        containerColor = Color(0xFFFFF1F3)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("🔍 Rechercher une fleur...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFFD81B60), RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            for (i in products.chunked(2)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (product in i) {
                        val imageRes = when (product.id) {
                            "1" -> R.drawable.sunflawer
                            "2" -> R.drawable.lily
                            "3" -> R.drawable.hibiscus
                            "4" -> R.drawable.tulip
                            "5" -> R.drawable.pansy
                            "6" -> R.drawable.lavender

                            else -> R.drawable.img1
                        }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .clickable { onNavigateToDetails(product.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(120.dp)
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFF1F3), shape = RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(product.name, fontWeight = FontWeight.Bold, color = Color(0xFF880E4F))
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun DetailsScreen(productId: String) {
    val product = when (productId) {
        "1" -> Product("1", "Sunflower", "A radiant sunflower, symbolizing happiness and positivity with its bright yellow petals.", 100.0, 8)
        "2" -> Product("2", "Lily", "A delicate lily that represents purity, renewal, and refined beauty.", 120.0, 14)
        "3" -> Product("3", "Hibiscus", "A vibrant hibiscus known for its tropical charm and bold colors.", 85.0, 20)
        "4" -> Product("4", "Tulip", "A graceful tulip, perfect for expressing elegance and affection.", 150.0, 5)
        "5" -> Product("5", "Pansy", "A charming pansy with velvety petals, symbolizing thoughtful remembrance.", 200.0, 17)
        "6" -> Product("6", "Lavender", "Fragrant lavender, soothing and calming, ideal for peace and relaxation.", 50.0, 9)
        else -> Product("0", "Unknown", "No description available.", 0.0, 0)
    }


    val imageRes = when (product.id) {
        "1" -> R.drawable.sunflawer
        "2" -> R.drawable.lily
        "3" -> R.drawable.hibiscus
        "4" -> R.drawable.tulip
        "5" -> R.drawable.pansy
        "6" -> R.drawable.lavender
        else -> R.drawable.img1
    }

    val quantityColor = if (product.quantity > 10) Color(0xFF2E7D32) else Color.Red

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF1F3))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(product.name, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD81B60))
            Spacer(modifier = Modifier.height(8.dp))
            Text(product.description, fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Prix : ${product.price} DH", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF616161))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Quantité disponible : ${product.quantity}", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = quantityColor)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { /* TODO: Ajouter au panier */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ajouter au panier", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}