// ui/cart/CartViewModel.kt
package com.example.myapplication.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Entities.*
import com.example.myapplication.data.Repository.ProductRepository      // NEW
import dagger.hilt.android.lifecycle.HiltViewModel                     // إذا كنتي مستعملة Hilt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject                                             // NEW

@HiltViewModel                                                        // NEW (أزِلها إذا ما كتستعملش Hilt)
class CartViewModel @Inject constructor(                              // NEW: حقن الـ repo
    private val repo: ProductRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    /* --------------------------- ADD --------------------------- */
    fun addToCart(product: Product, selected: List<Pair<Addon, Int>>): Boolean {
        if (!repo.decrementStock(product.id, 1)) return false          // NEW

        val list = _items.value.toMutableList()
        val current = list.find { it.product.id == product.id }

        if (current == null) list += CartItem(product)
        else current.quantity += 1

        val target = current ?: list.last()
        selected.forEach { (addon, q) ->
            if (q > 0) {
                val ex = target.addons.find { it.addon.id == addon.id }
                if (ex == null) target.addons += AddonQuantity(addon, q)
                else ex.quantity += q
            }
        }

        _items.value = list
        return true
    }

    /* --------------------------- INC --------------------------- */
    fun inc(ci: CartItem): Boolean {
        if (!repo.decrementStock(ci.product.id, 1)) return false       // NEW
        ci.quantity++
        _items.value = _items.value
        return true
    }

    fun dec(ci: CartItem) {
        if (ci.quantity > 1) {
            ci.quantity--
            repo.incrementStock(ci.product.id, 1)                      // NEW: رجّع ستوك
            _items.value = _items.value
        }
    }

    fun incAddon(ci: CartItem, aq: AddonQuantity) {
        aq.quantity++
        _items.value = _items.value
    }

    fun decAddon(ci: CartItem, aq: AddonQuantity) {
        if (aq.quantity > 1) {
            aq.quantity--
            _items.value = _items.value
        }
    }

    fun removeProduct(ci: CartItem) {
        repo.incrementStock(ci.product.id, ci.quantity)                // NEW: رجّع الكمية كاملة
        val list = _items.value.toMutableList()
        list.remove(ci)
        _items.value = list
    }

    fun clearCart() {
        _items.value.forEach { repo.incrementStock(it.product.id, it.quantity) } // NEW
        _items.value = emptyList()
    }
}

typealias CartItemUi = CartItem
