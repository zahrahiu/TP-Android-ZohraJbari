package com.example.myapplication.navigator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bienvenue sur HomeScreen")
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { onNavigateToDetails("PR1234") }) {
            Text(text = "Aller aux détails du produit PR1234")
        }
    }
}

@Composable
fun DetailsScreen(productId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bienvenue sur DetailsScreen")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Produit ID: $productId")
    }
}