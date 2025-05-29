package com.example.myapplication.data.Repository


import android.util.Log
import com.example.myapplication.data.Api.ProductApi
import com.example.myapplication.data.Entities.Product
import kotlinx.coroutines.delay
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val api: ProductApi
) {
    suspend fun getProducts(): List<Product> {
        // fetch data from a remote server
        return api.getProducts()
        val products = api.getProducts()
        Log.d("products repo", "size :"+ products.size)
        return products
    }
}