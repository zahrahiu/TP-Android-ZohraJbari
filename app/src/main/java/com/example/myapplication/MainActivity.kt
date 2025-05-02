package com.example.myapplication

import android.media.tv.TvContract.Channels.Logo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigator.AppNavigation
import com.example.myapplication.ui.theme.MyApplicationTheme

import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface{
                    AppNavigation()
                }
            }
        }
    }
}



@Composable
fun MyComposable(id: String) {
    LaunchedEffect(id) {
        // Coroutine démarrée quand `id` change
        delay(1000)
        println("Chargement terminé pour $id")
    }
}

@Composable
fun MyDisposable() {
    DisposableEffect(Unit) {
        println("Composé")

        onDispose {
            println("Nettoyé")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

val imageModifier = Modifier
    .size(150.dp)
    .border(BorderStroke(1.dp, Color.Black))
    .background(Color.Yellow)






/*@Composable
fun CounterSaveable() {
    // Crée un état “count” initialisé à 0 et mémorisé
    var count by rememberSaveable  { mutableIntStateOf(0) }

    Button(onClick = { count++ }) {
        Text("Vous avez cliqué $count fois")
    }
}*/

@Composable
fun Counter(count: Int, onIncrement: () -> Unit) {
    Button(onClick = onIncrement) {
        Text("Vous avez cliqué $count fois")
    }
}

@Composable
fun CounterScreen() {
    var count by rememberSaveable { mutableStateOf(0) }
    Counter(count = count, onIncrement = { count++ })
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Column {
            Greeting("Android")
            CounterScreen()

        }

    }
}

