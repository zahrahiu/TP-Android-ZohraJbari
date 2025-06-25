package com.example.myapplication.ui.product.screens

import androidx.compose.runtime.*

object LanguageManager {

    enum class Lang(val code: String) {
        FR("fr"),
        EN("en"),
        AR("ar")
    }

    var defaultLanguage = Lang.FR

    @Composable
    fun rememberLanguage(): Instance {
        var current by remember { mutableStateOf(defaultLanguage) }
        return Instance(current) { current = it }
    }

    class Instance(
        val current: Lang,
        val onChange: (Lang) -> Unit
    ) {
        private val texts = mapOf(
            Lang.FR to mapOf(
                // Common
                "back" to "Retour",
                "search_placeholder" to "🔍 Rechercher...",
                "filters" to "Filtres",
                "select_filter" to "Sélectionnez un filtre pour afficher les options",
                "price_min" to "Prix Min",
                "price_max" to "Prix Max",
                "reset" to "Réinitialiser",
                "apply" to "Appliquer",
                "find_flower" to "💐 Trouve ta fleur préférée 💐",
                "no_products" to "Aucun produit trouvé",
                "error" to "Erreur",
                "home" to "Accueil",
                "categories" to "Catégories",
                "favorites" to "Favoris",
                "cart" to "Panier",
                "currency" to "DH",

                // Product
                "add_ons_title" to "✨ Ajouts pour sublimer le bouquet",
                "add_to_cart" to "🛒 Ajouter au panier",

                // Cart & Checkout
                "total_price" to "Prix Total",
                "checkout" to "Passer Commande",
                "increment" to "Incrémenter",
                "decrement" to "Décrémenter",
                "remove_product" to "Supprimer produit",
                "collapse" to "Réduire",
                "details" to "Détails",
                "increment_addon" to "Incrémenter addon",
                "decrement_addon" to "Décrémenter addon",
                "empty" to "vide",
                "stock_insufficient" to "Stock insuffisant pour",

                // Checkout specific
                "self_pickup" to "Retrait en main propre",
                "your_information" to "Vos coordonnées",
                "sender" to "Expéditeur",
                "recipient" to "Destinataire",
                "comment" to "Commentaire",
                "payment_method" to "Méthode de paiement",
                "card" to "Carte",
                "paypal" to "PayPal",
                "card_number" to "Numéro de carte",
                "expiry_date" to "Date d'expiration",
                "cvv" to "CVV",
                "full_name" to "Nom complet",
                "email" to "Email",
                "phone" to "Téléphone",
                "address" to "Adresse",
                "your_order" to "Votre commande",
                "quantity" to "Quantité",
                "add_ons" to "Suppléments",
                "subtotal" to "Sous-total",
                "delivery" to "Livraison",
                "total" to "Total",
                "pay" to "Payer",

                "order_summary_title" to "Récapitulatif de commande",
                "thank_you_order" to "Merci pour votre commande, %s !",
                "client_info" to "Informations client",
                "phone" to "Téléphone",
                "address" to "Adresse",
                "order_status" to "Statut de votre commande :",
                "ordered" to "Commandé",
                "shipped" to "Expédié",
                "delivered" to "Livré",
                "order_delivered_success" to "✅ Votre commande a été livrée avec succès !",
                "order_details" to "Détails de la commande :",
                "subtotal" to "Sous-total:",
                "delivery_fee" to "Frais de livraison:",
                "total" to "Total:",
            ),

            Lang.EN to mapOf(
                // Common
                "back" to "Back",
                "search_placeholder" to "🔍 Search...",
                "filters" to "Filters",
                "select_filter" to "Select a filter to see options",
                "price_min" to "Min Price",
                "price_max" to "Max Price",
                "reset" to "Reset",
                "apply" to "Apply",
                "find_flower" to "💐 Find your favourite flower 💐",
                "no_products" to "No products found",
                "error" to "Error",
                "home" to "Home",
                "categories" to "Categories",
                "favorites" to "Favorites",
                "cart" to "Cart",
                "currency" to "MAD",

                // Product
                "add_ons_title" to "✨ Add-ons to enhance the bouquet",
                "add_to_cart" to "🛒 Add to Cart",

                // Cart & Checkout
                "total_price" to "Total Price",
                "checkout" to "Checkout",
                "increment" to "Increment",
                "decrement" to "Decrement",
                "remove_product" to "Remove product",
                "collapse" to "Collapse",
                "details" to "Details",
                "increment_addon" to "Increment addon",
                "decrement_addon" to "Decrement addon",
                "empty" to "empty",
                "stock_insufficient" to "Insufficient stock for",

                // Checkout specific
                "self_pickup" to "Self Pickup",
                "your_information" to "Your Information",
                "sender" to "Sender",
                "recipient" to "Recipient",
                "comment" to "Comment",
                "payment_method" to "Payment Method",
                "card" to "Card",
                "paypal" to "PayPal",
                "card_number" to "Card Number",
                "expiry_date" to "Expiry Date",
                "cvv" to "CVV",
                "full_name" to "Full Name",
                "email" to "Email",
                "phone" to "Phone",
                "address" to "Address",
                "your_order" to "Your Order",
                "quantity" to "Quantity",
                "add_ons" to "Add-ons",
                "subtotal" to "Subtotal",
                "delivery" to "Delivery",
                "total" to "Total",
                "pay" to "Pay",
                "order_summary_title" to "Order Summary",
                "thank_you_order" to "Thank you for your order, %s!",
                "client_info" to "Client Information",
                "phone" to "Phone",
                "address" to "Address",
                "order_status" to "Order status:",
                "ordered" to "Ordered",
                "shipped" to "Shipped",
                "delivered" to "Delivered",
                "order_delivered_success" to "✅ Your order has been successfully delivered!",
                "order_details" to "Order Details:",
                "subtotal" to "Subtotal:",
                "delivery_fee" to "Delivery fee:",
                "total" to "Total:",
            ),

            Lang.AR to mapOf(
                // Common
                "back" to "رجوع",
                "search_placeholder" to "ابحث...🔍",
                "filters" to "فلاتر",
                "select_filter" to "اختر فلتر لعرض الخيارات",
                "price_min" to "السعر الأدنى",
                "price_max" to "السعر الأقصى",
                "reset" to "إعادة تعيين",
                "apply" to "تطبيق",
                "find_flower" to "💐 ابحث عن زهرتك المفضلة 💐",
                "no_products" to "لم يتم العثور على منتجات",
                "error" to "خطأ",
                "home" to "الرئيسية",
                "categories" to "الفئات",
                "favorites" to "المفضلة",
                "cart" to "السلة",
                "currency" to "درهم",

                // Product
                "add_ons_title" to "✨ إضافات لتجميل الباقة",
                "add_to_cart" to "🛒 أضف إلى السلة",

                // Cart & Checkout
                "total_price" to "السعر الإجمالي",
                "checkout" to "إتمام الطلب",
                "increment" to "زيادة",
                "decrement" to "إنقاص",
                "remove_product" to "حذف المنتج",
                "collapse" to "طي",
                "details" to "تفاصيل",
                "increment_addon" to "زيادة الإضافة",
                "decrement_addon" to "إنقاص الإضافة",
                "empty" to "فارغ",
                "stock_insufficient" to "المخزون غير كافٍ لـ",

                // Checkout specific
                "self_pickup" to "استلام شخصي",
                "your_information" to "معلوماتك",
                "sender" to "المرسل",
                "recipient" to "المستلم",
                "comment" to "تعليق",
                "payment_method" to "طريقة الدفع",
                "card" to "بطاقة",
                "paypal" to "باي بال",
                "card_number" to "رقم البطاقة",
                "expiry_date" to "تاريخ الانتهاء",
                "cvv" to "CVV",
                "full_name" to "الاسم الكامل",
                "email" to "البريد الإلكتروني",
                "phone" to "الهاتف",
                "address" to "العنوان",
                "your_order" to "طلبك",
                "quantity" to "الكمية",
                "add_ons" to "الإضافات",
                "subtotal" to "المجموع الفرعي",
                "delivery" to "التوصيل",
                "total" to "المجموع",
                "pay" to "دفع",
                "order_summary_title" to "ملخص الطلب",
                "thank_you_order" to "شكراً لطلبك، %s !",
                "client_info" to "معلومات العميل",
                "phone" to "الهاتف",
                "address" to "العنوان",
                "order_status" to "حالة طلبك:",
                "ordered" to "تم الطلب",
                "shipped" to "تم الشحن",
                "delivered" to "تم التسليم",
                "order_delivered_success" to "✅ تم تسليم طلبك بنجاح!",
                "order_details" to "تفاصيل الطلب:",
                "subtotal" to "المجموع الفرعي:",
                "delivery_fee" to "رسوم التوصيل:",
                "total" to "المجموع:",
            )
        )

        fun get(key: String): String = texts[current]?.get(key) ?: key
    }
}