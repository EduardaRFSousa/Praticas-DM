package com.example.weatherapp

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val activity: Activity = context as Activity

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        // --- LOGO E NOME DO APP ---
        androidx.compose.material3.Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.Cloud,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
        Text(
            text = "WeatherApp",
            fontSize = 32.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Bem-vindo! Faça seu Login",
            fontSize = 20.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.size(24.dp))

        OutlinedTextField(
            value = email,
            label = { Text(text = "E-mail") },
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            label = { Text(text = "Senha") },
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            singleLine = true
        )

        Spacer(modifier = Modifier.size(32.dp))

        // --- BOTÕES ---
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    Firebase.auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(activity, "Login OK!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(activity, "Falha na autenticação.", Toast.LENGTH_LONG).show()
                            }
                        }
                },
                enabled = email.isNotEmpty() && password.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Entrar")
            }

            androidx.compose.material3.TextButton(
                onClick = {
                    activity.startActivity(Intent(activity, RegisterActivity::class.java))
                }
            ) {
                Text("Não tem uma conta? Cadastre-se")
            }
        }
    }
}