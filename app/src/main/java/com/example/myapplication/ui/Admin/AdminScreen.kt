// AdminScreen.kt
package com.example.myapplication.ui.Admin

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.Repository.UserRepository.User
import com.example.myapplication.ui.User.BeigeBackground
import com.example.myapplication.ui.User.RougeFlora
import com.example.myapplication.navigator.Routes

private val CardShape = RoundedCornerShape(20.dp)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AdminScreen(
    navController: NavController,
    vm: AdminViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("En attente", "Acceptés", "Commandes")

    Scaffold(
        containerColor = BeigeBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Gestion des utilisateurs",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(Routes.Login) {
                                popUpTo(Routes.Login) { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour à la connexion",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = RougeFlora,
                    titleContentColor = Color.White
                )
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = RougeFlora,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(3.dp),
                        color = RougeFlora
                    )
                }
            ) {
                tabs.forEachIndexed { i, title ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i },
                        text = {
                            Text(
                                title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            when (selectedTab) {
                0 -> UsersList(
                    list = vm.pending,
                    onAccept = vm::accept,
                    onRefuse = vm::refuse
                )
                1 -> UsersList(
                    list = vm.accepted,
                    readonly = true
                )
                2 -> AdminOrdersScreen()
            }
        }
    }
}

@Composable
private fun UsersList(
    list: List<User>,
    readonly: Boolean = false,
    onAccept: (String) -> Unit = {},
    onRefuse: (String) -> Unit = {}
) {
    if (list.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aucun utilisateur", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(list) { u ->
            Card(
                shape = CardShape,
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(RougeFlora),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            u.prenom.firstOrNull()?.uppercase() ?: "?",
                            color = Color.White,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(Modifier.weight(1f)) {
                        Text("${u.prenom} ${u.nom}", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                        Text(u.email, fontSize = 12.sp, color = Color.Gray)
                    }

                    if (!readonly) {
                        IconButton(
                            onClick = { onAccept(u.email) },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = "Accepter")
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { onRefuse(u.email) },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFFE53935),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Refuser")
                        }
                    }
                }
            }
        }
    }
}
