package com.example.myapplication.ui.product.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Entities.Product

@Composable
fun ProductRatingSection(
    product: Product,
    onRate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        RatingBar(
            rating = product.rating,
            onRate = onRate,
        )
    }
}
