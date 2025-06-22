package com.example.myapplication.ui.product.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    ) {
        Text(
            "A great addition to the bouquet",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        Column {
            addons.forEachIndexed { i, a ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(a.imageRes), null, Modifier.size(48.dp))
                        Column(Modifier.padding(start = 8.dp)) {
                            Text(a.name)
                            Text("${a.price} DH", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (qty[i] > 0) qty[i]-- }) {
                            Icon(Icons.Filled.Remove, null)
                        }
                        Text("${qty[i]}", Modifier.width(24.dp))
                        IconButton(onClick = { qty[i]++ }) {
                            Icon(Icons.Filled.Add, null)
                        }
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
                .padding(16.dp)
        ) { Text("Ajouter au panier") }
    }
}
