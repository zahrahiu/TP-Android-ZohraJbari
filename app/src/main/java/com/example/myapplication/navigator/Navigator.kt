package com.example.emtyapp.nav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.emtyapp.ui.product.screens.HomeScreen
import com.example.myapplication.ui.cart.CartScreen
import com.example.myapplication.ui.cart.CartViewModel
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.DetailsScreen
import com.example.myapplication.ui.product.screens.FavoritesScreen

object Routes {
    const val Home          = "home"
    const val Favorites     = "favorites"
    const val ProductDetail = "details"
    const val Cart          = "cart"
}

@Composable
fun AppNavigation(viewModel: ProductViewModel) {
    val nav     = rememberNavController()
    val cartVM  = remember { CartViewModel() }

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
                onNavigateToFavorites = { nav.navigate(Routes.Favorites) },
            )
        }

        /* Details */
        composable(
            "${Routes.ProductDetail}/{id}",
            arguments = listOf(navArgument("id"){ type = NavType.StringType })
        ) { back ->
            val id = back.arguments?.getString("id") ?: return@composable
            viewModel.getProductById(id)?.let { prod ->
                DetailsScreen(
                    product       = prod,
                    navController = nav,
                    onAddToCart   = { selected ->
                        val ok = cartVM.addToCart(prod, selected)
                        if (ok) nav.navigate(Routes.Cart)
                        ok
                    }
                )
            } ?: Text("Produit introuvable")
        }

        /* Cart */
        composable(Routes.Cart) {
            CartScreen(cartVM)
        }
    }
}
