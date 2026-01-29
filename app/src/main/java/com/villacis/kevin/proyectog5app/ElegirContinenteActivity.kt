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
            finish()
        }
        
        findViewById<Button>(R.id.btnNorthAmerica).setOnClickListener {
            startActivity(Intent(this, GameNorthAmericaActivity::class.java))
        }

        findViewById<Button>(R.id.btnCentralAmerica).setOnClickListener {
            startActivity(Intent(this, GameCentralAmericaActivity::class.java))
        }

        findViewById<Button>(R.id.btnSouthAmerica).setOnClickListener {
            startActivity(Intent(this, GameAmericaSurActivity::class.java))
        }

        findViewById<Button>(R.id.btnEurope).setOnClickListener {
            startActivity(Intent(this, GameEuropeActivity::class.java))
        }

        findViewById<Button>(R.id.btnAsia).setOnClickListener {
            startActivity(Intent(this, GameAsiaActivity::class.java))
        }

        findViewById<Button>(R.id.btnOceania).setOnClickListener {
            startActivity(Intent(this, GameOceaniaActivity::class.java))
        }

        findViewById<Button>(R.id.btnAfrica).setOnClickListener {
            startActivity(Intent(this, GameAfricaActivity::class.java))
        }
    }
}