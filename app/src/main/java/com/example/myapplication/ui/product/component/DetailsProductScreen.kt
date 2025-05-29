package com.example.myapplication.ui.product.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun DetailsScreen(product: Product) {
    val imageRes = when (product.id) {
        "1" -> R.drawable.sunflawer
        "2" -> R.drawable.lily
        "3" -> R.drawable.hibiscus
        "4" -> R.drawable.tulip
        "5" -> R.drawable.pansy
        "6" -> R.drawable.lavender
        else -> R.drawable.img1
    }


    val backgroundColor = Color(0xFFFDF6F0) // نفس لون البطاقات

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = product.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.description,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Prix : ${product.price} DH",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5D4037)
            )

            Spacer(modifier = Modifier.height(8.dp))



            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* TODO: Ajouter au panier */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Ajouter au panier", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
