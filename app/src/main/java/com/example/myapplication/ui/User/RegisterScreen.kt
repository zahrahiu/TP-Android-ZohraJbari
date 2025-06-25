package com.example.myapplication.ui.User

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.Repository.UserRepository



@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var errorMsg   by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "🌸 Flora",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = RougeFlora,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
        )


        Image(
            painter = painterResource(id = R.drawable.flora),
            contentDescription = "Logo Flora",
            modifier = Modifier.size(160.dp)
        )

        Spacer(Modifier.height(5.dp))

        Card(
            colors  = CardDefaults.cardColors(containerColor = BeigeFlora),
            shape   = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Créer votre compte",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = RougeFlora,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = nom,
                        onValueChange = { nom = it },
                        label = { Text("Nom") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RougeFlora,
                            focusedLabelColor  = RougeFlora
                        )
                    )
                    OutlinedTextField(
                        value = prenom,
                        onValueChange = { prenom = it },
                        label = { Text("Prénom") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RougeFlora,
                            focusedLabelColor  = RougeFlora
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                /* Téléphone */
                OutlinedTextField(
                    value = telephone,
                    onValueChange = { telephone = it },
                    label = { Text("Téléphone") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RougeFlora,
                        focusedLabelColor  = RougeFlora
                    )
                )

                Spacer(Modifier.height(12.dp))

                /* Email */
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RougeFlora,
                        focusedLabelColor  = RougeFlora
                    )
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RougeFlora,
                            focusedLabelColor  = RougeFlora
                        )
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmer") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RougeFlora,
                            focusedLabelColor  = RougeFlora
                        )
                    )
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        when {
                            nom.isBlank() || prenom.isBlank() || telephone.isBlank()
                                    || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                                errorMsg   = "Merci de remplir tous les champs"
                                successMsg = ""
                            }
                            password != confirmPassword -> {
                                errorMsg   = "Les mots de passe ne correspondent pas"
                                successMsg = ""
                            }
                            !UserRepository.register(email, password, nom, prenom, telephone) -> {
                                errorMsg   = "Email déjà utilisé"
                                successMsg = ""
                            }
                            else -> {
                                errorMsg   = ""
                                successMsg = "Inscription réussie ! Connecte-toi 😊"
                                onRegisterSuccess()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RougeFlora)
                ) {
                    Text("S'inscrire", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                if (errorMsg.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }

                if (successMsg.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(successMsg, color = RougeFlora, fontSize = 14.sp)
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = onNavigateToLogin) {
                    Text("Déjà un compte ? Se connecter", color = RougeFlora)
                }
            }
        }
    }
}
