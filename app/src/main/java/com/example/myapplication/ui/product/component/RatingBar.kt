package com.example.myapplication.ui.product.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun RatingBar(
    rating: Int,
    onRate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = 20.dp,
    spaceBetween: Dp = 4.dp
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            Icon(
                painter = painterResource(
                    if (i <= rating) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                ),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(starSize)
                    .padding(end = spaceBetween)
                    .clickable { onRate(i) }
            )
        }
    }
}

