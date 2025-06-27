/* ─────────────────────  AdminScreen.kt  ───────────────────── */
package com.example.myapplication.ui.Admin

/*  Android / Compose  */
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

/*  Projet  */
import com.example.myapplication.data.Repository.UserRepository
import com.example.myapplication.data.Repository.UserStatus
import com.example.myapplication.navigator.Routes

/* ───────── Palette & formes ───────── */
private val Sauge        = Color(0xFF8BA17E)          // accent principal
private val BeigeClair   = Color(0xFFFFF8F0)          // fond principal
private val CardShape    = RoundedCornerShape(24.dp)
private val RedRefuse    = Color(0xFFE53935)

/* ════════════════════════════════════════════════════════════ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    vm: AdminViewModel = hiltViewModel()
) {

    /* ------------ état des tabs ------------ */
    var mainTab    by remember { mutableStateOf(0) }              // 0 Users / 1 Orders
    val mainTitles = listOf("Utilisateurs", "Commandes")

    var userFilter by remember { mutableStateOf(UserStatus.PENDING) }

    val usersFiltered = when (userFilter) {
        UserStatus.PENDING  -> vm.pending
        UserStatus.ACCEPTED -> vm.accepted
        UserStatus.REFUSED  -> vm.refused
    }

    /* ------------ scaffold ------------ */
    Scaffold(
        containerColor = BeigeClair,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Espace admin", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Home) { inclusive = true }
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Sauge,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            /* ===== Tabs principaux (Users / Orders) ===== */
            TabRow(
                selectedTabIndex = mainTab,
                containerColor   = Color.Transparent,
                contentColor     = Sauge,
                indicator = { pos ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(pos[mainTab])
                            .height(4.dp),
                        color = Sauge
                    )
                }
            ) {
                mainTitles.forEachIndexed { i, txt ->
                    Tab(
                        selected = mainTab == i,
                        onClick  = { mainTab = i },
                        text     = { Text(txt, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            /* ===== Contenu ===== */
            when (mainTab) {
                /* ---------- Utilisateurs ---------- */
                0 -> {
                    ChipsRow(
                        items = listOf(
                            "En attente" to UserStatus.PENDING,
                            "Acceptés"   to UserStatus.ACCEPTED,
                            "Refusés"    to UserStatus.REFUSED
                        ),
                        selected = userFilter,
                        onSelect = { userFilter = it }
                    )
                    Spacer(Modifier.height(18.dp))

                    UsersList(
                        list     = usersFiltered,
                        readonly = userFilter != UserStatus.PENDING,
                        onAccept = vm::accept,
                        onRefuse = vm::refuse
                    )
                }

                /* ---------- Commandes ---------- */
                1 -> AdminOrdersScreen()
            }
        }
    }
}

/* ──────────────────────────────────────────────────────────── */
/*                           CHIPS                              */
/* ──────────────────────────────────────────────────────────── */
@Composable
private fun ChipsRow(
    items: List<Pair<String, UserStatus>>,
    selected: UserStatus,
    onSelect: (UserStatus) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach { (label, status) ->
            val isSel  = status == selected
            val bgAnim by animateColorAsState(
                targetValue = if (isSel) Sauge else Color.Transparent,
                animationSpec = tween(300, easing = EaseInOut)
            )
            val txtCol = if (isSel || bgAnim.luminance() < .4f) Color.White else Sauge

            Surface(
                shape  = RoundedCornerShape(50),
                color  = bgAnim,
                tonalElevation = if (isSel) 2.dp else 0.dp,
                shadowElevation = 1.dp,
                border = if (!isSel) ButtonDefaults.outlinedButtonBorder else null,
                modifier = Modifier
                    .height(34.dp)
                    .clickable { onSelect(status) }
            ) {
                Box(Modifier.padding(horizontal = 18.dp), Alignment.Center) {
                    Text(label, color = txtCol, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

/* ──────────────────────────────────────────────────────────── */
/*                        LISTE UTILISATEURS                    */
/* ──────────────────────────────────────────────────────────── */
@Composable
private fun UsersList(
    list: List<UserRepository.User>,
    readonly: Boolean,
    onAccept: (String) -> Unit,
    onRefuse: (String) -> Unit
) {
    if (list.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aucun utilisateur", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
    ) {
        items(list) { u ->
            UserCard(u, readonly, onAccept, onRefuse)
        }
    }
}

@Composable
private fun UserCard(
    user: UserRepository.User,
    readonly: Boolean,
    onAccept: (String) -> Unit,
    onRefuse: (String) -> Unit
) {
    Card(
        shape     = CardShape,
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /* Avatar rond avec gradient Sauge */
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Sauge, Sauge.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    user.prenom.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.width(16.dp))

            /* Infos utilisateur */
            Column(Modifier.weight(1f)) {
                Text("${user.prenom} ${user.nom}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(user.email, fontSize = 13.sp, color = Color.DarkGray)
            }

            /* Actions Accept/Refuse */
            if (!readonly) {
                IconButton(
                    onClick = { onAccept(user.email) },
                    colors  = IconButtonDefaults.iconButtonColors(
                        containerColor = Sauge,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "Accepter")
                }

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = { onRefuse(user.email) },
                    colors  = IconButtonDefaults.iconButtonColors(
                        containerColor = RedRefuse,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Refuser")
                }
            }
        }
    }
}
