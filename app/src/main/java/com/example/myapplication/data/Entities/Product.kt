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
    val quantity: Int,
    @SerializedName("productPrice")
    val price: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("colors")
    val colors: List<String>,
    @SerializedName("occasions")
    val occasions: List<String>,
    @SerializedName("discountPercent")
    val discountPercent: Int?  ,

    @SerializedName("offerEnd")
    val offerEndEpochMillis: Long?,
    val rating: Int = 0,
    @SerializedName("category")
val category: String


)