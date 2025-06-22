import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.Entities.Product

@Composable
fun ProductItem(
    product: Product,
    isFavorite: Boolean,
    onItemClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
)  {
    val imageRes = getImageResource(product.image)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF6F0)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037),
                        maxLines = 1
                    )
                    Text(
                        text = product.price,
                        fontSize = 14.sp,
                        color = Color(0xFF8D6E63),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Bouton favoris en haut à droite
            IconButton(
                onClick = onFavoriteClick,                       // ⇐
                modifier = Modifier.align(Alignment.TopEnd)
                    .padding(8.dp).size(24.dp)
            )  {
                Icon(
                    painter = painterResource(
                        if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline
                    ),
                    contentDescription = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
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
        else -> R.drawable.img1
    }
}