package com.villacis.kevin.proyectog5app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ElegirContinenteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elegir_continente)

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Regresa a la actividad anterior (Bienvenida)
        }
        
        // Lógica para ir al juego de Sudamérica
        val btnSouthAmerica = findViewById<Button>(R.id.btnSouthAmerica)
        btnSouthAmerica.setOnClickListener {
             val intent = Intent(this, GameSudamericaActivity::class.java)
             startActivity(intent)
        }
    }
}