package com.example.myapplication.ui.product.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

    val quantityColor = if (product.quantity > 10) Color(0xFF2E7D32) else Color.Red

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF1F3))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(product.name, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD81B60))
            Spacer(modifier = Modifier.height(8.dp))
            Text(product.description, fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Prix : ${product.price} DH", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF616161))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Quantité disponible : ${product.quantity}", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = quantityColor)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { /* TODO: Ajouter au panier */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ajouter au panier", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
