package com.example.myapplication.ui.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(viewModel: CartViewModel) {

    val items = viewModel.items.collectAsState().value
    val snackHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mon Panier") }
            )
        },
        snackbarHost = { SnackbarHost(snackHostState) }
    ) { pad ->

        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Panier vide") }
            return@Scaffold
        }

        LazyColumn(Modifier.padding(pad)) {
            items(items) { ci ->

                val maxStock = ci.product.quantity.toIntOrNull() ?: Int.MAX_VALUE
                val outOfStock = ci.quantity >= maxStock

                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(Modifier.padding(8.dp)) {

                        /* Produit principal */
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painterResource(R.drawable.img1),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp)
                            )
                            Column(
                                Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            ) {
                                Text(ci.product.name)
                                Text(ci.product.price)
                                if (outOfStock)
                                    Text(
                                        "Stock insuffisant",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { viewModel.dec(ci) }) {
                                    Icon(Icons.Filled.Remove, contentDescription = null)
                                }
                                Text("${ci.quantity}")
                                IconButton(
                                    onClick = {
                                        val ok = viewModel.inc(ci)
                                        if (!ok) scope.launch {
                                            snackHostState.showSnackbar("Stock insuffisant pour ${ci.product.name}")
                                        }
                                    },
                                    enabled = !outOfStock
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null)
                                }
                            }
                        }

                        /* Addons */
                        ci.addons.forEach { aq ->
                            Row(
                                Modifier
                                    .padding(start = 40.dp, top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painterResource(aq.addon.imageRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    aq.addon.name,
                                    Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                )
                                IconButton(onClick = { viewModel.decAddon(ci, aq) }) {
                                    Icon(Icons.Filled.Remove, contentDescription = null)
                                }
                                Text("${aq.quantity}")
                                IconButton(onClick = { viewModel.incAddon(ci, aq) }) {
                                    Icon(Icons.Filled.Add, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
