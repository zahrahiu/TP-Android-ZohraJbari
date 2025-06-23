// ui/product/ProductViewModel.kt
package com.example.myapplication.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Entities.Product
import com.example.myapplication.data.Repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductViewState())
    val state: StateFlow<ProductViewState> = _state

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds

    init {
        loadProducts()
    }

    fun toggleFavorite(product: Product) {
        _favoriteIds.value = if (_favoriteIds.value.contains(product.id)) {
            _favoriteIds.value - product.id
        } else {
            _favoriteIds.value + product.id
        }
    }

    fun updateProductRating(productId: String, newRating: Int) {
        viewModelScope.launch {
            repository.updateProductRating(productId, newRating)
            _state.update { currentState ->
                currentState.copy(
                    products = currentState.products.map { product ->
                        if (product.id == productId) product.copy(rating = newRating) else product
                    }
                )
            }
        }
    }

    fun getProductById(id: String): Product? {
        return _state.value.products.find { it.id == id }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val products = repository.getProducts()
                _state.value = ProductViewState(
                    isLoading = false,
                    products = products
                )
            } catch (e: Exception) {
                _state.value = ProductViewState(
                    isLoading = false,
                    error = e.message ?: "Failed to load products"
                )
            }
        }
    }
}