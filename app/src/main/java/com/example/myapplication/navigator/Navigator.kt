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
import com.example.myapplication.ui.product.screens.CategoryProductsScreen
import com.example.myapplication.ui.product.screens.CategorySelectionScreen
import com.example.myapplication.ui.product.screens.FavoritesScreen

object Routes {
    const val Home = "home"
    const val Favorites = "favorites"
    const val ProductDetail = "details"
    const val Cart = "cart"
    const val CategorySelection = "category_selection"
    const val CategoryProducts = "category_products"
}

@Composable
fun AppNavigation(viewModel: ProductViewModel) {
    val nav = rememberNavController()
    val cartVM = remember { CartViewModel() }

    NavHost(navController = nav, startDestination = Routes.Home) {
        composable(Routes.Home) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToDetails = { id -> nav.navigate("${Routes.ProductDetail}/$id") },
                onNavigateToFavorites = { nav.navigate(Routes.Favorites) },
                onNavigateToCart = { nav.navigate(Routes.Cart) },
                onNavigateToCategory = { nav.navigate(Routes.CategorySelection) },
                currentRoute = Routes.Home
            )
        }
        composable(Routes.Favorites) {
            FavoritesScreen(
                viewModel = viewModel,
                onNavigateToDetails = { id -> nav.navigate("${Routes.ProductDetail}/$id") },
                onNavigateToHome = { nav.navigate(Routes.Home) },
                onNavigateToFavorites = { nav.navigate(Routes.Favorites) },
                onNavigateToCart = { nav.navigate(Routes.Cart) }
            )
        }
        composable("${Routes.ProductDetail}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            viewModel.getProductById(id)?.let { prod ->
                DetailsScreen(
                    product = prod,
                    navController = nav,
                    viewModel = viewModel,
                    onAddToCart = { selected ->
                        val ok = cartVM.addToCart(prod, selected)
                        if (ok) nav.navigate(Routes.Cart)
                        ok
                    }
                )
            } ?: Text("Produit introuvable")
        }
        composable(Routes.Cart) {
            CartScreen(
                viewModel = cartVM,
                onNavigateToHome = { nav.navigate(Routes.Home) },
                onNavigateToFavorites = { nav.navigate(Routes.Favorites) },
                onNavigateToCart = { nav.navigate(Routes.Cart) }
            )
        }
        composable(Routes.CategorySelection) {
            CategorySelectionScreen(
                onCategorySelected = { category ->
                    nav.navigate("${Routes.CategoryProducts}/$category")
                },
                onNavigateHome = { nav.navigate(Routes.Home) },
                onNavigateFavorites = { nav.navigate(Routes.Favorites) },
                onNavigateCart = { nav.navigate(Routes.Cart) },
                currentRoute = Routes.CategorySelection
            )
        }

        composable(
            "${Routes.CategoryProducts}/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            CategoryProductsScreen(
                viewModel = viewModel,
                category = category,
                onNavigateToDetails = { id -> nav.navigate("${Routes.ProductDetail}/$id") },
                onNavigateHome = { nav.navigate(Routes.Home) },
                onNavigateFavorites = { nav.navigate(Routes.Favorites) },
                onNavigateCart = { nav.navigate(Routes.Cart) },
                onNavigateCategories = { nav.navigate(Routes.CategorySelection) },
                currentRoute = Routes.CategoryProducts
            )
        }


    }
}