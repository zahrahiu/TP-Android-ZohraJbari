package com.example.emtyapp.nav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.emtyapp.ui.product.screens.HomeScreen
import com.example.myapplication.ui.cart.CartScreen
import com.example.myapplication.ui.cart.CartViewModel
import com.example.myapplication.ui.checkout.CheckoutScreen
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.DetailsScreen
import com.example.myapplication.ui.product.screens.CategoryProductsScreen
import com.example.myapplication.ui.product.screens.CategorySelectionScreen
import com.example.myapplication.ui.product.screens.FavoritesScreen

/**
 * Centralised navigation routes
 */
object Routes {
    const val Home = "home"
    const val Favorites = "favorites"
    const val ProductDetail = "details"
    const val Cart = "cart"
    const val CategorySelection = "category_selection"
    const val CategoryProducts = "category_products"
    const val Checkout = "checkout"
}

@Composable
fun AppNavigation(viewModel: ProductViewModel) {
    val nav = rememberNavController()
    // ✅ Single instance of the cart VM shared across all destinations
    val cartVM = remember { CartViewModel() }

    NavHost(navController = nav, startDestination = Routes.Home) {
        // ---------- Home ----------
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

        // ---------- Favorites ----------
        composable(Routes.Favorites) {
            FavoritesScreen(
                viewModel = viewModel,
                onNavigateToDetails = { id -> nav.navigate("${Routes.ProductDetail}/$id") },
                onNavigateToHome = { nav.navigate(Routes.Home) },
                onNavigateToFavorites = { /* stay */ },
                onNavigateToCart = { nav.navigate(Routes.Cart) },
                onNavigateToCategory = { nav.navigate(Routes.CategorySelection) }
            )
        }

        // ---------- Product details ----------
        composable(
            route = "${Routes.ProductDetail}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            viewModel.getProductById(id)?.let { prod ->
                DetailsScreen(
                    product = prod,
                    navController = nav,
                    viewModel = viewModel,
                    onAddToCart = { selectedAddons ->
                        val ok = cartVM.addToCart(prod, selectedAddons)
                        if (ok) nav.navigate(Routes.Cart)
                        ok
                    }
                )
            } ?: Text("Produit introuvable")
        }

        // ---------- Cart ----------
        composable(Routes.Cart) {
            CartScreen(
                viewModel = cartVM,
                onNavigateToHome = { nav.navigate(Routes.Home) },
                onNavigateToFavorites = { nav.navigate(Routes.Favorites) },
                onNavigateToCategory = { nav.navigate(Routes.CategorySelection) },
                onNavigateToCart = { /* stay */ },
                onNavigateToCheckout = { nav.navigate(Routes.Checkout) }
            )
        }

        // ---------- Category selection ----------
        composable(Routes.CategorySelection) {
            CategorySelectionScreen(
                onCategorySelected = { category -> nav.navigate("${Routes.CategoryProducts}/$category") },
                onNavigateHome = { nav.navigate(Routes.Home) },
                onNavigateFavorites = { nav.navigate(Routes.Favorites) },
                onNavigateCart = { nav.navigate(Routes.Cart) },
                currentRoute = Routes.CategorySelection
            )
        }

        // ---------- Category products ----------
        composable(
            route = "${Routes.CategoryProducts}/{category}",
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

        // ---------- Checkout ----------
        composable(Routes.Checkout) {
            // ✅ Pass the SAME cart view-model so we can read the cart items
            CheckoutScreen(
                viewModel = cartVM,
                onBack = { nav.popBackStack() },
                onPay = {
                    // TODO handle success (e.g. clear cart + snackbar)
                    nav.popBackStack(Routes.Home, false)
                }
            )
        }
    }
}