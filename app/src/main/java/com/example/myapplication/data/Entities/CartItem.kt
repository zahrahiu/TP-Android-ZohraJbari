package com.example.myapplication.data.Entities

data class AddonQuantity(
    val addon: Addon,
    var quantity: Int = 1
)

data class CartItem(
    val product: Product,
    var quantity: Int = 1,
    val addons: MutableList<AddonQuantity> = mutableListOf()
)
