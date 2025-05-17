package com.example.myapplication.ui.product

sealed class ProductIntent {
    object LoadProducts : ProductIntent()
}