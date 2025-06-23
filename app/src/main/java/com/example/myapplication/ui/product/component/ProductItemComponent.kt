import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.myapplication.ui.product.component.RatingBar
import kotlinx.coroutines.delay

// ui/product/component/ProductItem.kt
@Composable
fun ProductItem(
    product: Product,
    isFavorite: Boolean,
    onItemClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onRateProduct: (Int) -> Unit,

    modifier: Modifier = Modifier,
    showRating: Boolean = true

) {
    val imageRes = getImageResource(product.image)

    fun parsePrice(str: String): Float =
        str.replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0f

    val oldPrice = parsePrice(product.price)
    val newPrice: Float? = product.discountPercent?.let { p ->
        oldPrice * (100 - p) / 100f
    }

    val remaining by produceState<String?>(initialValue = null, product.offerEndEpochMillis) {
        val end = product.offerEndEpochMillis ?: return@produceState
        while (true) {
            val diff = (end - System.currentTimeMillis()).coerceAtLeast(0)
            if (diff == 0L) {
                value = null
                break
            }
            val h = diff / 3_600_000
            val m = (diff / 60_000) % 60
            value = String.format("%02dh %02dm", h, m)
            delay(60_000)
        }
    }

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
                Image(
                    painterResource(imageRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037),
                        maxLines = 1
                    )

                    if (newPrice != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                String.format("%.0f DH", oldPrice),
                                fontSize = 14.sp,
                                color = Color(0xFF8D6E63),
                                textDecoration = TextDecoration.LineThrough
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                String.format("%.0f DH", newPrice),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC143C)
                            )
                        }
                    } else {
                        Text(
                            product.price,
                            fontSize = 14.sp,
                            color = Color(0xFF8D6E63)
                        )
                    }

                    remaining?.let {
                        Text(
                            it,
                            fontSize = 12.sp,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Ajout de la RatingBar ici
                    if (showRating && product.rating > 0) {
                        RatingBar(
                            rating = product.rating,
                            onRate = onRateProduct,
                            readOnly = false,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }

            if (product.discountPercent != null && remaining != null) {
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

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
            ) {
                Icon(
                    painterResource(
                        if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
    }
}

private fun getImageResource(productImage: String): Int {
    return when (productImage) {
        "hibiscus.jpg" -> R.drawable.hibiscus
        "lavender.jpg" -> R.drawable.lavender
        "lily.jpg" -> R.drawable.lily
        "pansy.jpg" -> R.drawable.pansy
        "img1.jpg" -> R.drawable.img1
        "img2.jpg" -> R.drawable.img2
        "img3.jpg" -> R.drawable.img3
        "img4.jpg" -> R.drawable.img4
        "img8.jpg" -> R.drawable.img8
        else -> R.drawable.img1
    }
}