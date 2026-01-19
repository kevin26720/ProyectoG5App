package com.villacis.kevin.proyectog5app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import android.os.Handler
import android.os.Looper

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

            // Login successful
            Snackbar.make(findViewById(android.R.id.content), "Inicio de sesión correcto", Snackbar.LENGTH_SHORT).show()
            
            // Navegar a BienvenidaActivity con un pequeño retraso
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, BienvenidaActivity::class.java)
                intent.putExtra("USER_EMAIL", email)
                startActivity(intent)
                finish()
            }, 1000)
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}