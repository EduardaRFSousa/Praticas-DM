package com.example.weatherapp

import android.app.Activity
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
import com.example.weatherapp.db.fb.FBDatabase
import com.example.weatherapp.db.fb.toFBUser
import com.example.weatherapp.model.User
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                WeatherAppTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        RegisterPage(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmpassword by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val activity: Activity = context as Activity

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = "Criar nova conta",
            fontSize = 28.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp)) // Espaço entre título e inputs

        OutlinedTextField(
            value = name,
            label = { Text(text = "Nome completo") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { name = it },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            label = { Text(text = "E-mail") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { email = it },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            label = { Text(text = "Senha") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmpassword,
            label = { Text(text = "Confirmar senha") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { confirmpassword = it },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(horizontalAlignment = CenterHorizontally) {
            Button(
                onClick = {
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(activity, "Registro OK!", Toast.LENGTH_LONG).show()
                                FBDatabase().register(User(name, email).toFBUser())
                                activity.finish() // Volta para a tela de Login
                            } else {
                                Toast.makeText(activity, "Falha no registro.", Toast.LENGTH_LONG).show()
                            }
                        }
                },
                enabled = email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() &&
                        confirmpassword.isNotEmpty() && password == confirmpassword,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Registrar")
            }

            androidx.compose.material3.TextButton(
                onClick = { activity.finish() } // Fecha a tela de cadastro e volta ao login
            ) {
                Text("Já possui conta? Voltar ao Login")
            }
        }
    }
}