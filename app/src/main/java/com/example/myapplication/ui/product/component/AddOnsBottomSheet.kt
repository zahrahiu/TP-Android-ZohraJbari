package com.example.myapplication.ui.product.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.Entities.Addon



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOnsBottomSheet(
    open: Boolean,
    onDismiss: () -> Unit,
    onValidate: (List<Pair<Addon, Int>>) -> Unit
) {
    if (!open) return

    val addons = listOf(
        Addon("choc",  "Chocolate",   25f, R.drawable.chocolat),
        Addon("raf",   "Raffaello",   35f, R.drawable.raffaello),
        Addon("vase",  "Glass Vase",  40f, R.drawable.vase),
        Addon("teddy", "Plush Teddy", 50f, R.drawable.plush)
    )
    val qty = remember { mutableStateListOf(0, 0, 0, 0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "✨ Ajouts pour sublimer le bouquet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = RougeCerise,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            addons.forEachIndexed { i, a ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(GrisClair, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(a.imageRes),
                            contentDescription = a.name,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 8.dp)
                        )
                        Column {
                            Text(a.name, fontWeight = FontWeight.Medium)
                            Text("${a.price} DH", fontSize = 12.sp, color = GrisPetitGris)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (qty[i] > 0) qty[i]-- }) {
                            Icon(Icons.Filled.Remove, contentDescription = "-", tint = RougeCerise)
                        }
                        Text("${qty[i]}", modifier = Modifier.width(24.dp), fontSize = 16.sp)
                        IconButton(onClick = { qty[i]++ }) {
                            Icon(Icons.Filled.Add, contentDescription = "+", tint = RougeCerise)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val selected = addons.mapIndexed { i, a -> a to qty[i] }.filter { it.second > 0 }
                    onValidate(selected)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RougeCerise, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("🛒 Ajouter au panier", fontSize = 16.sp)
            }
        }
    }
}
