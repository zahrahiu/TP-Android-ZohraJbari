package com.example.myapplication.ui.product.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.Entities.Product
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ProductItem(
    product: Product,
    isFavorite: Boolean,
    onItemClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onRateProduct: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showRating: Boolean = true,
) {
    val imgRes = getImageResource(product.image)

    /* --------- Prix --------- */
    fun parsePrice(str: String): Float =
        str.replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0f
    val oldPrice = parsePrice(product.price)
    val newPrice = product.discountPercent?.let { oldPrice * (100 - it) / 100f }

    /* --------- Date --------- */
    val promoDate = remember(product.offerEndEpochMillis) {
        product.offerEndEpochMillis?.let { end ->
            Instant.ofEpochMilli(end)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm"))
        }
    }

    /* --------- Card --------- */
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color(0xFFFDF6F0)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(Modifier.fillMaxSize()) {

            Column(
                Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                /* ------- Image + Date ------- */
                Box(contentAlignment = Alignment.TopCenter) {
                    Image(
                        painter = painterResource(imgRes),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )

                    if (promoDate != null) {
                        TimerBox(
                            date = promoDate,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = 76.dp)
                        )
                    }
                }
                Spacer(Modifier.width(2.dp))


                /* ------- Infos ------- */
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037),
                        maxLines = 1
                    )

                    /* --- Prix --- */
                    if (newPrice != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "%.0f DH".format(oldPrice),
                                fontSize = 14.sp,
                                color = Color(0xFF8D6E63),
                                textDecoration = TextDecoration.LineThrough
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "%.0f DH".format(newPrice),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC143C)
                            )
                        }
                    } else {
                        Text(product.price, fontSize = 14.sp, color = Color(0xFF8D6E63))
                    }

                    /* --- Rating --- */
                    if (showRating) {
                        RatingBar(
                            rating = product.rating,
                            onRate = onRateProduct,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            /* ------- Badge -% ------- */
            if (product.discountPercent != null) {
                Box(
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color(0xFFDC143C), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        "-${product.discountPercent}%",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            /* ------- Favorite Button ------- */
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
            ) {
                Icon(
                    painterResource(
                        if (isFavorite) R.drawable.ic_favorite_filled
                        else R.drawable.ic_favorite_outline
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified                   )
            }
        }
    }
}

/* -------- TimerBox (Affiche date uniquement) -------- */
@Composable
private fun TimerBox(date: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFDC143C), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = Color(0xFFDC143C),
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = date,
                fontSize = 11.sp,
                color = Color(0xFFDC143C),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/* -------- Image helper -------- */
private fun getImageResource(name: String): Int = when (name) {
    "hibiscus.jpg" -> R.drawable.hibiscus
    "lavender.jpg" -> R.drawable.lavender
    "lily.jpg"     -> R.drawable.lily
    "pansy.jpg"    -> R.drawable.pansy
    "img1.jpg"     -> R.drawable.img1
    "img2.jpg"     -> R.drawable.img2
    "img3.jpg"     -> R.drawable.img3
    "img4.jpg"     -> R.drawable.img4
    "img8.jpg"     -> R.drawable.img8
    else           -> R.drawable.img1
}
