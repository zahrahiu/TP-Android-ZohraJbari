package com.example.myapplication.data.Repository

data class OrderItemDto(
    val productName : String,
    val image       : String,
    val unitPrice   : Double,
    val discountPct : Int? = null,
    val quantity    : Int,
    val addons      : List<AddonDto> = emptyList()
){
    /* حساب السعر بعد الخصم */
    fun effectivePrice() = discountPct?.let { unitPrice * (100 - it) / 100 } ?: unitPrice
    fun addonsTotal()    = addons.sumOf { it.price * it.quantity }
}

data class AddonDto(
    val name     : String,
    val price    : Double,
    val quantity : Int
)
