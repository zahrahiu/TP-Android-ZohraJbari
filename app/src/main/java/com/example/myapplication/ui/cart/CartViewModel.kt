package com.example.myapplication.ui.cart

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.myapplication.data.Entities.*

class CartViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items


    fun addToCart(product: Product, selected: List<Pair<Addon, Int>>): Boolean {
        val stock = product.quantity.toIntOrNull() ?: Int.MAX_VALUE

        val list = _items.value.toMutableList()
        val current = list.find { it.product.id == product.id }

        if (current != null) {
            if (current.quantity + 1 > stock) return false
            current.quantity += 1
        } else {
            if (stock < 1) return false
            list += CartItem(product)
        }

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

    fun inc(ci: CartItem): Boolean {
        val stock = ci.product.quantity.toIntOrNull() ?: Int.MAX_VALUE
        return if (ci.quantity + 1 > stock) {
            false
        } else {
            ci.quantity++
            _items.value = _items.value
            true
        }
    }

    fun dec(ci: CartItem) {
        if (ci.quantity > 1) {
            ci.quantity--
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
        val list = _items.value.toMutableList()
        list.remove(ci)
        _items.value = list
    }

}
