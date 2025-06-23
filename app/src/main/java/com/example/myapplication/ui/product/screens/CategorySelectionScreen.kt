package com.example.myapplication.ui.product.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategorySelectionScreen(
    onCategorySelected: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf("ROSES", "FLOWERS", "POTTED", "ARRANGEMENT").forEach { category ->
            Button(
                onClick = { onCategorySelected(category) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(category)
            }
        }
    }
}
