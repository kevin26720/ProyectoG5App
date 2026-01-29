package com.villacis.kevin.proyectog5app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnBack = findViewById<Button>(R.id.btnBack)

        btnSave.setOnClickListener {
            val email = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // --- Validaciones ---
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etUsername.error = "Por favor ingrese un correo válido"
                return@setOnClickListener
            }
            if (password.length < 8) {
                etPassword.error = "La contraseña debe tener al menos 8 caracteres"
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                etConfirmPassword.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            // --- Registro con Firebase ---
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registro exitoso
                        val snackbar = Snackbar.make(findViewById(android.R.id.content), "Registro exitoso. Inicie sesión.", Snackbar.LENGTH_LONG)
                        snackbar.show()
                        
                        // Redirigir a LoginActivity después de un momento
                        snackbar.view.postDelayed({ // Usamos el postDelayed en la vista del snackbar
                            val intent = Intent(this, LoginActivity::class.java)
                            // Limpiar el stack de actividades para que no pueda volver al registro
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }, 2000) // 2 segundos de retraso

                    } else {
                        // Si el registro falla, mostrar un mensaje.
                        // Causa común: el correo ya está en uso.
                        val errorMessage = task.exception?.message ?: "Error desconocido en el registro."
                        Snackbar.make(findViewById(android.R.id.content), "Error en el registro: $errorMessage", Snackbar.LENGTH_LONG).show()
                    }
                }
        }

        btnBack.setOnClickListener {
            finish() // Regresa a la actividad anterior (Login)
        }
    }
}