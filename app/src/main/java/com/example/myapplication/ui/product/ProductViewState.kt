package com.example.myapplication.ui.product

import com.example.myapplication.data.Entities.Product

data class  ProductViewState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null

)