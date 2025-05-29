package com.example.myapplication.data.Entities

import com.google.gson.annotations.SerializedName


data class Product(
    @SerializedName("productID")
    val id: String,
    @SerializedName("productTitle")
    val name: String,
    @SerializedName("productDescription")
    val description: String,
    @SerializedName("productImage")
    val image: String,
    @SerializedName("quantity")
    val quantity: String,
    @SerializedName("prouctPrice")
    val price: String
)


