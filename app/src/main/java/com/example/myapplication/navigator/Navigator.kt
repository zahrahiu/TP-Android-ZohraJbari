package com.example.emtyapp.nav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.emtyapp.ui.product.screens.HomeScreen
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.DetailsScreen
import com.example.myapplication.ui.product.screens.FavoritesScreen

object Routes {
    const val Home          = "home"
    const val Favorites     = "favorites"
    const val ProductDetail = "details"
}

@Composable
fun AppNavigation(viewModel: ProductViewModel) {
    val nav = rememberNavController()

    NavHost(nav, Routes.Home) {

        /* Home */
        composable(Routes.Home) {
            HomeScreen(
                viewModel               = viewModel,
                onNavigateToDetails     = { id -> nav.navigate("${Routes.ProductDetail}/$id") },
                onNavigateToFavorites   = { nav.navigate(Routes.Favorites) }
            )
        }

        /* Favoris */
        composable(Routes.Favorites) {
            val state  by viewModel.state.collectAsState()
            val favIds by viewModel.favoriteIds.collectAsState()

            FavoritesScreen(
                viewModel = viewModel,
                onNavigateToDetails = { id -> nav.navigate("${Routes.ProductDetail}/$id") },
                onNavigateToHome = { nav.navigate(Routes.Home) },
                onNavigateToFavorites = { nav.navigate(Routes.Favorites) },  // لازم تزيده هنا
            )

        }


        /* Details */
        composable(
            "${Routes.ProductDetail}/{id}",
            arguments = listOf(navArgument("id"){ type = NavType.StringType })
        ) { back ->
            val id = back.arguments?.getString("id") ?: return@composable
            viewModel.getProductById(id)?.let { DetailsScreen(it) }
                ?: Text("Produit introuvable")
        }
    }
}


/* Deprecated */
@Composable
fun HomeScreenDeprecated(onNavigateToDetails: (String) -> Unit) {
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