// data/Repository/ProductRepository.kt
package com.example.myapplication.data.Repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.Api.ProductApi
import com.example.myapplication.data.Entities.Product
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "ratings_store")

@Singleton
class ProductRepository @Inject constructor(
    private val api: ProductApi,
    @ApplicationContext private val context: Context
) {

    private val ds: DataStore<Preferences> = context.dataStore

    private val _products = MutableStateFlow<List<Product>>(emptyList())   // NEW
    val products: StateFlow<List<Product>> = _products                     // NEW

    suspend fun refreshProducts() {                                        // NEW
        try {
            val remote = api.getProducts()
            val ratings = loadRatings()
            _products.value = remote.map { it.copy(rating = ratings[it.id] ?: 0) }
            Log.d("ProductRepository", "Loaded ${_products.value.size} products")
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error getting products", e)
        }
    }

    suspend fun getProducts(): List<Product> {
        if (_products.value.isEmpty()) refreshProducts()                   // NEW
        return _products.value
    }

    fun decrementStock(productId: String, amount: Int = 1): Boolean {      // NEW
        var ok = false
        _products.update { list ->
            list.map {
                if (it.id == productId && it.quantity >= amount) {
                    ok = true
                    it.copy(quantity = it.quantity - amount)
                } else it
            }
        }
        return ok
    }

    fun incrementStock(productId: String, amount: Int = 1) {               // NEW (اختياري)
        _products.update { list ->
            list.map {
                if (it.id == productId) it.copy(quantity = it.quantity + amount)
                else it
            }
        }
    }

    suspend fun updateProductRating(productId: String, rating: Int) {
        ds.edit { it[intPreferencesKey("rating_$productId")] = rating }
        _products.update { list ->                                         // NEW: حدّث الـ Flow
            list.map { if (it.id == productId) it.copy(rating = rating) else it }
        }
        Log.d("ProductRepository", "Updated rating for $productId to $rating")
    }

    private suspend fun loadRatings(): Map<String, Int> {
        return ds.data
            .map { prefs ->
                prefs.asMap()
                    .filterKeys { it.name.startsWith("rating_") }
                    .mapKeys { it.key.name.removePrefix("rating_") }
                    .mapValues { (it.value as? Int) ?: 0 }
            }
            .first()
    }
}
