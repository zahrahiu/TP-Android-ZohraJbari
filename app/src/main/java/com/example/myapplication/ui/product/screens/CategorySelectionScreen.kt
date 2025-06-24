package com.example.myapplication.ui.product.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.navigator.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionScreen(
    onCategorySelected: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigateCart: () -> Unit,
    currentRoute: String = Routes.CategorySelection
) {
    val categories = listOf(
        "ROSES" to "img_rose",
        "FLOWERS" to "img_flowers",
        "POTTED" to "img_potted",
        "ARRANGEMENT" to "img_arrangement"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🌸 Flora Boutique - Catégories",
                        color = Color(0xFFDC4C3E),
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF8F0))
            )
        },
        containerColor = Color(0xFFFFFBF7),
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFFFF8F0)) {
                NavigationBarItem(
                    selected = currentRoute == Routes.Home,
                    onClick = onNavigateHome,
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = true, // Ici on est dans catégories
                    onClick = { /* rester ici */ },
                    icon = { Text("🪷", fontSize = 20.sp) },
                    label = { Text("Catégories") }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Favorites,
                    onClick = onNavigateFavorites,
                    icon = { Text("❤", fontSize = 20.sp) },
                    label = { Text("Favoris") }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.Cart,
                    onClick = onNavigateCart,
                    icon = { Text("🛒", fontSize = 20.sp) },
                    label = { Text("Panier") }
                )
            }
        },
        content = { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.forEach { (category, imageName) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3F1)),
                        onClick = { onCategorySelected(category) },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val context = LocalContext.current
                                val imgId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
                                if (imgId != 0) {
                                    Image(
                                        painter = painterResource(id = imgId),
                                        contentDescription = category,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = category,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFDC4C3E)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Aller vers $category",
                                tint = Color(0xFFDC4C3E)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Footer

            }
        }
    )
}
