// LoginScreen.kt
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
import com.example.myapplication.data.Repository.UserStatus
import com.example.myapplication.session.SessionManager

val RougeFlora = Color(0xFFDC4C3E)
val BeigeFlora = Color(0xFFFFF8F0)
val BeigeBackground = Color(0xFFFFFFFF)

@Composable
fun LoginScreen(
    onUserLogin: () -> Unit,
    onAdminLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "🌸 Flora",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = RougeFlora,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.flora),
            contentDescription = "Logo Flora",
            modifier = Modifier
                .size(160.dp)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = BeigeFlora),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Se connecter",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = RougeFlora,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RougeFlora,
                        focusedLabelColor = RougeFlora
                    )
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RougeFlora,
                        focusedLabelColor = RougeFlora
                    )
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        val user = UserRepository.login(email, password)
                        if (user == null) {
                            // بيانات خاطئة
                            errorMsg = "Email ou mot de passe incorrect"
                        } else if (user.status == UserStatus.REFUSED) {
                            // المستخدم مرفوض من قبل الادارة
                            errorMsg = "Votre compte a été refusé par l’administration."
                        } else {
                            // حالة مقبولة (مسموح له الدخول)
                            SessionManager.currentUser.value = user
                            if (user.isAdmin) onAdminLogin() else onUserLogin()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RougeFlora)
                ) {
                    Text("Se connecter", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }


                if (errorMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(errorMsg, color = Color.Red, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        "Pas encore de compte ? S'inscrire",
                        color = RougeFlora
                    )
                }
            }
        }
    }
}
