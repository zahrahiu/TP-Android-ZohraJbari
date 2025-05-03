package com.example.myapplication.navigator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        Product("1", "Rose rouge", "Une rose magnifique", 10.0),
        Product("2", "Tulipe blanche", "Belle tulipe blanche", 12.0),
        Product("3", "Tournesol", "Tournesol éclatant", 8.5),
        Product("4", "Lys rose", "Délicat lys", 15.0)
    )

    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌸 Flower Store", color = Color(0xFFB22222)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                actions = {
                    IconButton(onClick = {}) {
                        Text("⋮", color = Color(0xFFB22222))
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text("🏠")
                Text("❤️")
                Text("🧑")
                Text("🛒")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Rechercher...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Column {
                for (i in products.chunked(2)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (product in i) {
                            val imageRes = when (product.id) {
                                "1" -> R.drawable.img1
                                "2" -> R.drawable.img2
                                "3" -> R.drawable.img3
                                "4" -> R.drawable.img4
                                else -> R.drawable.img1
                            }
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .clickable { onNavigateToDetails(product.id) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = imageRes),
                                        contentDescription = "Image fleur",
                                        modifier = Modifier
                                            .height(100.dp)
                                            .fillMaxWidth()
                                    )
                                    Text(product.name, fontWeight = FontWeight.Bold)
                                    Text("${product.price} €", fontSize = 14.sp)
                                }
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
        "1" -> Product("1", "Rose rouge", "Une rose magnifique", 10.0)
        "2" -> Product("2", "Tulipe blanche", "Belle tulipe blanche", 12.0)
        "3" -> Product("3", "Tournesol", "Tournesol éclatant", 8.5)
        "4" -> Product("4", "Lys rose", "Délicat lys", 15.0)
        else -> Product("0", "Inconnu", "Pas de description", 0.0)
    }

    val imageRes = when (product.id) {
        "1" -> R.drawable.img1
        "2" -> R.drawable.img2
        "3" -> R.drawable.img3
        "4" -> R.drawable.img4
        else -> R.drawable.img1
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = product.name, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB22222))
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = product.description)
        Text(text = "Prix: ${product.price} €", fontSize = 20.sp, fontWeight = FontWeight.Medium)
    }
}
