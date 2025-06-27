package com.example.myapplication.data.Repository

import java.util.UUID

/** État d’une commande */
enum class OrderStatus { PENDING, CONFIRMED, REFUSED }

/** Une commande minimaliste */
data class Order(
    val id: String = UUID.randomUUID().toString(),
    val userEmail: String,

    val clientName: String,
    val phone: String,
    val address: String,
    val itemsJson: String,
    val shippingMethod: String,
    val shippingFee: Double,
    // ⇠ tu peux serialiser ta liste CartItemUi en JSON
    var status: OrderStatus = OrderStatus.PENDING
)

object OrderRepository {

    private val orders = mutableMapOf<String, Order>()

    /* CRUD  */
    fun create(order: Order)                       { orders[order.id] = order }
    fun byId(id: String)      = orders[id]
    fun all()               = orders.values.toList()
    fun pending()           = orders.values.filter { it.status == OrderStatus.PENDING }
    fun forUser(email: String) = orders.values.filter { it.userEmail == email }

    fun confirm(id: String)  { orders[id]?.status = OrderStatus.CONFIRMED }
    fun refuse(id: String)   { orders[id]?.status = OrderStatus.REFUSED }
}
