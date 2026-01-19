package com.villacis.kevin.proyectog5app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BienvenidaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        val btnNewGame = findViewById<Button>(R.id.btnNewGame)
        val tvPlayerName = findViewById<TextView>(R.id.tvPlayerName)
        
        // Recuperar el correo del usuario desde el Intent
        val userEmail = intent.getStringExtra("USER_EMAIL")
        if (!userEmail.isNullOrEmpty()) {
            tvPlayerName.text = userEmail
        }

        btnNewGame.setOnClickListener {
            val intent = Intent(this, ElegirContinenteActivity::class.java)
            startActivity(intent)
        }
    }
}