package com.villacis.kevin.proyectog5app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // Validar email al perder foco
        etUsername.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = etUsername.text.toString().trim()
                if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                     etUsername.error = "Por favor ingrese un correo electrónico válido"
                }
            }
        }

        btnContinue.setOnClickListener {
            val email = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            val isEmailValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val isPasswordValid = password.length >= 8

            if (!isEmailValid || !isPasswordValid) {
                val message = when {
                    !isEmailValid && !isPasswordValid -> {
                        etUsername.error = "Correo inválido"
                        etPassword.error = "Contraseña inválida (mínimo 8 caracteres)"
                        "Por favor verifique el correo y la contraseña"
                    }
                    !isEmailValid -> {
                        etUsername.error = "Correo inválido"
                        "Por favor ingrese un correo electrónico válido"
                    }
                    else -> { // !isPasswordValid
                        etPassword.error = "Contraseña inválida"
                        "La contraseña debe tener al menos 8 caracteres"
                    }
                }
                
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- Autenticación con Firebase ---
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, navigate to BienvenidaActivity
                        Log.d("FIREBASE_AUTH", "signInWithEmail:success")
                        val intent = Intent(this, BienvenidaActivity::class.java)
                        intent.putExtra("USER_EMAIL", auth.currentUser?.email)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("FIREBASE_AUTH", "signInWithEmail:failure", task.exception)
                        Snackbar.make(findViewById(android.R.id.content), "Error de autenticación: Usuario o contraseña incorrectos.", Snackbar.LENGTH_LONG).show()
                    }
                }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}