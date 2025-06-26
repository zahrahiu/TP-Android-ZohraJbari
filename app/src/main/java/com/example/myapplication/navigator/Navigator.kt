package com.example.myapplication.navigator

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.User.AdminScreen
import com.example.myapplication.ui.User.LoginScreen
import com.example.myapplication.ui.User.RegisterScreen
import com.example.myapplication.ui.cart.CartScreen
import com.example.myapplication.ui.cart.CartViewModel
import com.example.myapplication.ui.checkout.CheckoutScreen
import com.example.myapplication.ui.checkout.OrderSummaryScreen
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.DetailsScreen
import com.example.myapplication.ui.product.screens.*
import java.net.URLEncoder
import java.net.URLDecoder

object Routes {
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val Favorites = "favorites"
    const val ProductDetail = "details"
    const val Cart = "cart"
    const val CategorySelection = "category_selection"
    const val CategoryProducts = "category_products"
    const val Checkout = "checkout"
    const val OrderSummary = "order_summary/{name}/{phone}/{address}"
}

@Composable
fun AppNavigation(viewModel: ProductViewModel) {
    val nav = rememberNavController()
    val cartVM: CartViewModel = hiltViewModel()
    val lang = LanguageManager.rememberLanguage()

    NavHost(navController = nav, startDestination = Routes.Login) {

        composable("login") {
            LoginScreen(
                onUserLogin  = { nav.navigate("home")  { popUpTo("login") {inclusive = true} } },
                onAdminLogin = { nav.navigate("admin") { popUpTo("login") {inclusive = true} } },
                onNavigateToRegister = { nav.navigate("register") }
            )
        }

        composable("admin")  { AdminScreen()  }



        composable(Routes.Register) {
            RegisterScreen(
                onRegisterSuccess = { nav.popBackStack() }, // retourne à login après inscription réussie
                onNavigateToLogin = { nav.popBackStack() }
            )
        }


        composable(Routes.Home) {
            HomeScreen(
                viewModel = viewModel,
                lang = lang,
                onNavigateToDetails = { id -> nav.navigate("${Routes.ProductDetail}/$id") },
                onNavigateToFavorites = { nav.navigate(Routes.Favorites) },
                onNavigateToCart = { nav.navigate(Routes.Cart) },
                onNavigateToCategory = { nav.navigate(Routes.CategorySelection) },
                currentRoute = Routes.Home,
                onLogout = {                       // ⇦ هنا التوجيه الفعلي
                    nav.navigate("Login") {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Favorites) {
            FavoritesScreen(
                viewModel = viewModel,
                lang = lang,
                onNavigateToDetails = { id -> nav.navigate("${Routes.ProductDetail}/$id") },
                onNavigateHome = { nav.navigate(Routes.Home) },
                onNavigateFavorites = { /* stay */ },
                onNavigateCart = { nav.navigate(Routes.Cart) },
                onNavigateCategory = { nav.navigate(Routes.CategorySelection) }
            )
        }

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
                    lang = lang,

                    onAddToCart = { selectedAddons ->
                        val ok = cartVM.addToCart(prod, selectedAddons)
                        if (ok) nav.navigate(Routes.Cart)
                        ok
                    }
                )
            } ?: Text("Produit introuvable")
        }

        composable(Routes.Cart) {
            CartScreen(
                viewModel = cartVM,
                lang=lang,
                onNavigateToHome = { nav.navigate(Routes.Home) },
                onNavigateToFavorites = { nav.navigate(Routes.Favorites) },
                onNavigateToCategory = { nav.navigate(Routes.CategorySelection) },
                onNavigateToCart = { /* stay */ },
                onNavigateToCheckout = { nav.navigate(Routes.Checkout) }
            )
        }

        composable(Routes.CategorySelection) {
            CategorySelectionScreen(
                onCategorySelected = { category -> nav.navigate("${Routes.CategoryProducts}/$category") },
                onNavigateHome = { nav.navigate(Routes.Home) },
                onNavigateFavorites = { nav.navigate(Routes.Favorites) },
                onNavigateCart = { nav.navigate(Routes.Cart) },
                currentRoute = Routes.CategorySelection,
                lang = lang,
            )
        }

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
                currentRoute = Routes.CategoryProducts,
                lang = lang,

                )
        }

        composable(Routes.Checkout) {
            CheckoutScreen(
                viewModel = cartVM,
                onBack = { nav.popBackStack() },
                onPay = { name, phone, address ->
                    nav.navigate(
                        Routes.OrderSummary
                            .replace("{name}", URLEncoder.encode(name, "UTF-8"))
                            .replace("{phone}", URLEncoder.encode(phone, "UTF-8"))
                            .replace("{address}", URLEncoder.encode(address, "UTF-8"))
                    ) {
                        popUpTo(Routes.Home) { inclusive = false }
                    }
                }
                ,                lang=lang,

                )
        }

        composable(Routes.OrderSummary) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")?.let {
                URLDecoder.decode(it, "UTF-8")
            } ?: ""
            val phone = backStackEntry.arguments?.getString("phone")?.let {
                URLDecoder.decode(it, "UTF-8")
            } ?: ""
            val address = backStackEntry.arguments?.getString("address")?.let {
                URLDecoder.decode(it, "UTF-8")
            } ?: ""

            val total = cartVM.items.value.sumOf { ci ->
                val originalPrice = ci.product.price.toDoubleOrNull() ?: 0.0
                val productPrice = if (ci.product.discountPercent != null) {
                    originalPrice * (100 - ci.product.discountPercent) / 100
                } else {
                    originalPrice
                }
                productPrice * ci.quantity + ci.addons.sumOf { it.addon.price.toDouble() * it.quantity }
            } + 20.0

            OrderSummaryScreen(
                userName = name,
                phone = phone,
                address = address,
                items = cartVM.items.value,
                lang=lang,

                onBackHome = {
                    nav.popBackStack(Routes.Home, false)
                    cartVM.clearCart()
                }
            )
        }
    }
}