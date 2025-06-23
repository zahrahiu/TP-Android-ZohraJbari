package com.example.myapplication.ui.product.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

// Enum des filtres rapides
enum class QuickFilter(val title: String, val image: Int) {
    GIFT("Fleurs + Cadeau", R.drawable.qf_gift),
    MULTICOLOR("Bouquet coloré", R.drawable.qf_multicolor),
    BASKET("Arrangement", R.drawable.qf_basket)
}

@Composable
fun QuickFilterImage(
    filter: QuickFilter,
    isSelected: Boolean,
    onClick: (QuickFilter) -> Unit
) {
    val borderColor = if (isSelected) Color(0xFFDC4C3E) else Color.Transparent
    Image(
        painter = painterResource(filter.image),
        contentDescription = filter.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(200.dp)    // largeur fixe
            .height(90.dp)    // hauteur fixe
            .clip(RoundedCornerShape(12.dp))
            .border(BorderStroke(3.dp, borderColor), RoundedCornerShape(12.dp))
            .clickable { onClick(filter) }
    )
}
