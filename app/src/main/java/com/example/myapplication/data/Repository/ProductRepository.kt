package com.example.myapplication.data.Repository


import com.example.myapplication.data.Entities.Product
import kotlinx.coroutines.delay

class ProductRepository {
    suspend fun getProducts(): List<Product> {
        // Simulate fetching data from a remote server or database
        delay(2000)
        return listOf(
            Product("1", "Tournesol", "Un tournesol éclatant, symbole de bonheur et de positivité avec ses pétales jaune vif.", 100.0, 8),
            Product("2", "Lys", "Un lys délicat qui représente la pureté, le renouveau et la beauté raffinée.", 120.0, 14),
            Product("3", "Hibiscus", "Un hibiscus vibrant connu pour son charme tropical et ses couleurs audacieuses.", 85.0, 20),
            Product("4", "Tulipe", "Une tulipe gracieuse, parfaite pour exprimer l’élégance et l’affection.", 150.0, 5),
            Product("5", "Pensée", "Une pensée charmante aux pétales veloutés, symbole de souvenir affectueux.", 200.0, 17),
            Product("6", "Lavande", "Lavande parfumée, apaisante et relaxante, idéale pour la paix intérieure.", 50.0, 9)

        )
    }
}