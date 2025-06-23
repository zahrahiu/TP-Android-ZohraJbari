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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "ratings_store")

@Singleton
class ProductRepository @Inject constructor(
    private val api: ProductApi,
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun getProducts(): List<Product> {
        return try {
            val products = api.getProducts()
            val ratings = loadRatings()
            products.map { product ->
                product.copy(rating = ratings[product.id] ?: 0)
            }.also {
                Log.d("ProductRepository", "Loaded ${it.size} products")
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error getting products", e)
            emptyList()
        }
    }

    suspend fun updateProductRating(productId: String, rating: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(productId)] = rating
        }
        Log.d("ProductRepository", "Updated rating for $productId to $rating")
    }

    private suspend fun loadRatings(): Map<String, Int> {
        return dataStore.data
            .map { preferences ->
                preferences.asMap()
                    .filterKeys { it.name.contains("rating_") }
                    .mapKeys { it.key.name.removePrefix("rating_") }
                    .mapValues { (it.value as? Int) ?: 0 }
            }
            .first()
    }
}