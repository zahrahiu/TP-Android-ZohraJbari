//  AppNavigation.kt
package com.example.myapplication.navigator

/* ─── Android / Compose ─── */
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/* ─── Projet ─── */
import com.example.myapplication.data.Repository.Notification
import com.example.myapplication.data.Repository.Order
import com.example.myapplication.data.Repository.OrderRepository
import com.example.myapplication.data.Repository.UserRepository
import com.example.myapplication.session.SessionManager
import com.example.myapplication.ui.Admin.AdminScreen
import com.example.myapplication.ui.Notification.NotificationScreen
import com.example.myapplication.ui.User.LoginScreen
import com.example.myapplication.ui.User.RegisterScreen
import com.example.myapplication.ui.cart.CartScreen
import com.example.myapplication.ui.cart.CartViewModel
import com.example.myapplication.ui.checkout.CheckoutScreen
import com.example.myapplication.ui.checkout.OrderSummaryScreen
import com.example.myapplication.ui.product.ProductViewModel
import com.example.myapplication.ui.product.component.DetailsScreen
import com.example.myapplication.ui.product.screens.*
import com.google.gson.Gson

/* ══════════════ ROUTES ══════════════ */
object Routes {
    const val Login             = "login"
    const val Register          = "register"
    const val Home              = "home"
    const val Favorites         = "favorites"
    const val ProductDetail     = "details"
    const val Cart              = "cart"
    const val CategorySelection = "category_selection"
    const val CategoryProducts  = "category_products"
    const val Checkout          = "checkout"
    const val OrderSummaryById  = "order_summary"
    const val Notify            = "notifications"
    const val AdminPanel        = "admin_panel"
}

/* ══════════════ NAVIGATION ══════════════ */
@Composable
fun AppNavigation(productVM: ProductViewModel) {

    val nav       = rememberNavController()
    val cartVM: CartViewModel = hiltViewModel()
    val lang      = LanguageManager.rememberLanguage()

    val currentUserEmail = SessionManager.currentUser.value?.email ?: ""
    val currentOrder     = OrderRepository.forUser(currentUserEmail).lastOrNull()

    NavHost(navController = nav, startDestination = Routes.Login) {

        /* ----------- Authentification ----------- */
        composable(Routes.Login) {
            LoginScreen(
                onUserLogin = {
                    nav.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onAdminLogin = {
                    nav.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = { nav.navigate(Routes.Register) }
            )
        }

        composable(Routes.Register) {
            RegisterScreen(
                onRegisterSuccess = { nav.popBackStack() },
                onNavigateToLogin = { nav.popBackStack() }
            )
        }

        /* ----------- Panneau d’administration ----------- */
        composable(Routes.AdminPanel) {
            AdminScreen(navController = nav)
        }

        /* ----------- Home ----------- */
        composable(Routes.Home) {
            HomeScreen(
                viewModel                  = productVM,
                lang                       = lang,
                onNavigateToDetails        = { id -> nav.navigate("${Routes.ProductDetail}/$id") },
                onNavigateToFavorites      = { nav.navigate(Routes.Favorites) },
                onNavigateToCart           = { nav.navigate(Routes.Cart) },
                onNavigateToCategory       = { nav.navigate(Routes.CategorySelection) },
                currentRoute               = Routes.Home,
                onLogout                   = {
                    nav.navigate(Routes.Login) { popUpTo(Routes.Home) { inclusive = true } }
                },
                onNavigateToNotifications  = { nav.navigate(Routes.Notify) },
                showAdminButton            = SessionManager.currentUser.value?.isAdmin == true,
                onAdminButtonClick         = { nav.navigate(Routes.AdminPanel) }
            )
        }

        /* ----------- Favoris ----------- */
        composable(Routes.Favorites) {
            FavoritesScreen(
                viewModel              = productVM,
                lang                   = lang,
                onNavigateToDetails    = { id -> nav.navigate("${Routes.ProductDetail}/$id") },
                onNavigateHome         = { nav.navigate(Routes.Home) },
                onNavigateFavorites    = { /* déjà ici */ },
                onNavigateCart         = { nav.navigate(Routes.Cart) },
                onNavigateCategory     = { nav.navigate(Routes.CategorySelection) }
            )
        }

        /* ----------- Détail produit ----------- */
        composable(
            "${Routes.ProductDetail}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { back ->
            val id = back.arguments?.getString("id") ?: return@composable
            val prod = productVM.getProductById(id)
            if (prod == null) {
                Text("Produit introuvable")
            } else {
                DetailsScreen(
                    product       = prod,
                    navController = nav,
                    viewModel     = productVM,
                    lang          = lang,
                    onAddToCart   = { addons ->
                        val ok = cartVM.addToCart(prod, addons)
                        if (ok) nav.navigate(Routes.Cart)
                        ok
                    }
                )
            }
        }

        /* ----------- Panier ----------- */
        composable(Routes.Cart) {
            CartScreen(
                viewModel              = cartVM,
                lang                   = lang,
                onNavigateToHome       = { nav.navigate(Routes.Home) },
                onNavigateToFavorites  = { nav.navigate(Routes.Favorites) },
                onNavigateToCategory   = { nav.navigate(Routes.CategorySelection) },
                onNavigateToCart       = { /* déjà ici */ },
                onNavigateToCheckout   = { nav.navigate(Routes.Checkout) }
            )
        }

        /* ----------- Choix catégorie ----------- */
        composable(Routes.CategorySelection) {
            CategorySelectionScreen(
                onCategorySelected     = { cat -> nav.navigate("${Routes.CategoryProducts}/$cat") },
                onNavigateHome         = { nav.navigate(Routes.Home) },
                onNavigateFavorites    = { nav.navigate(Routes.Favorites) },
                onNavigateCart         = { nav.navigate(Routes.Cart) },
                currentRoute           = Routes.CategorySelection,
                lang                   = lang
            )
        }

        /* ----------- Produits par catégorie ----------- */
        composable(
            "${Routes.CategoryProducts}/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { back ->
            val cat = back.arguments?.getString("category") ?: ""
            CategoryProductsScreen(
                viewModel              = productVM,
                category               = cat,
                onNavigateToDetails    = { id -> nav.navigate("${Routes.ProductDetail}/$id") },
                onNavigateHome         = { nav.navigate(Routes.Home) },
                onNavigateFavorites    = { nav.navigate(Routes.Favorites) },
                onNavigateCart         = { nav.navigate(Routes.Cart) },
                onNavigateCategories   = { nav.navigate(Routes.CategorySelection) },
                currentRoute           = Routes.CategoryProducts,
                lang                   = lang
            )
        }

        /* ----------- Notifications ----------- */
        composable(Routes.Notify) {
            val user = SessionManager.currentUser.value ?: return@composable
            NotificationScreen(
                user          = user,
                onBack        = { nav.popBackStack() },
                navController = nav
            )
        }

        /* ----------- Checkout ----------- */
        composable(Routes.Checkout) {
            CheckoutScreen(
                viewModel    = cartVM,
                onBack       = { nav.popBackStack() },
                lang         = lang,
                currentOrder = currentOrder,
                onPay        = { orderId ->
                    // Ajout d'une notification de suivi pour l'utilisateur
                    UserRepository.getUser(currentUserEmail)?.notifications?.add(
                        Notification(
                            message = "Votre commande est en attente de confirmation.",
                            orderId = orderId
                        )
                    )
                }
            )
        }

        /* ----------- Suivi commande ----------- */
        composable(
            "${Routes.OrderSummaryById}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { back ->
            val id = back.arguments?.getString("id") ?: return@composable
            if (OrderRepository.byId(id) == null) {
                Text("Commande introuvable")
            } else {
                OrderSummaryScreen(
                    orderId    = id,
                    lang       = lang,
                    onBackHome = { nav.popBackStack(Routes.Home, false) }
                )
            }
        }
    }
}
