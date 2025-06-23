package com.example.myapplication.ui.product.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun RatingBar(
    rating: Int,
    onRate: (Int) -> Unit,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            val filled = index < rating
            Icon(
                imageVector = if (filled) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (filled) Color(0xFFFFC107) else Color(0xFFBDBDBD),
                modifier = Modifier
                    .size(28.dp)
                    .clickable(enabled = !readOnly) { onRate(index + 1) }
            )
        }
    }
}
