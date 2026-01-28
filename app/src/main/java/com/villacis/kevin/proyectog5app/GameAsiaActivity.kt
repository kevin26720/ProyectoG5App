package com.villacis.kevin.proyectog5app

import android.content.ClipData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class GameAsiaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_asia)

        val interactiveMap = findViewById<InteractiveMapView>(R.id.ivMap)
        interactiveMap.loadMap(R.drawable.asia)

        configurarBandera(R.id.flagItem1, "afganistan", R.drawable.afganistan)
        configurarBandera(R.id.flagItem2, "arabia_saudita", R.drawable.arabia_saudita)
        configurarBandera(R.id.flagItem3, "india", R.drawable.india)
        configurarBandera(R.id.flagItem4, "corea_norte", R.drawable.corea_norte)
        configurarBandera(R.id.flagItem5, "corea_sur", R.drawable.corea_sur)
        configurarBandera(R.id.flagItem6, "japon", R.drawable.japon)
        configurarBandera(R.id.flagItem8, "tailandia", R.drawable.tailandia)
        configurarBandera(R.id.flagItem9, "china", R.drawable.china)
        configurarBandera(R.id.flagItem10, "turquia", R.drawable.turquia)
        configurarBandera(R.id.flagItem11, "iran", R.drawable.iran)
        configurarBandera(R.id.flagItem12, "israel", R.drawable.israel)
        configurarBandera(R.id.flagItem13, "pakistan", R.drawable.pakistan)
        configurarBandera(R.id.flagItem14, "vietnam", R.drawable.vietnam)
        configurarBandera(R.id.flagItem15, "jordania", R.drawable.jordania)



        // 3. Configurar el SOLTAR (Drop) en el Mapa
        interactiveMap.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true // Aceptar el evento de arrastre

                DragEvent.ACTION_DROP -> {
                    // 1. Recuperamos el ID del país (Texto: "angola", "benin", etc.)
                    val idPaisArrastrado = event.clipData.getItemAt(0).text.toString() // Esto vale "angola"

                    // 2. Recuperamos la vista original para leer su TAG
                    val viewOriginal = event.localState as View

                    // 3. RECUPERAMOS EL RECURSO DE IMAGEN GUARDADO EN EL TAG
                    // Esto es lo que hace que sea dinámico
                    val resourceIdBandera = viewOriginal.tag as Int

                    // 4. Detectamos qué país se tocó en el mapa
                    val paisTocado = interactiveMap.getCountryAt(event.x, event.y)


                    if (paisTocado != null) {

                        if (paisTocado == idPaisArrastrado) {
                            // --- ¡CORRECTO! ---
                            val bitmapBandera = getBitmapFromVectorDrawable(this, resourceIdBandera)
                            interactiveMap.setFlagForCountry(paisTocado, bitmapBandera)

                            Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show()

                            viewOriginal.visibility = View.GONE // Ocultamos la bandera de la lista
                        } else {
                            // --- INCORRECTO ---
                            Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show()
                            viewOriginal.visibility = View.VISIBLE // Regresamos la bandera a su sitio
                        }
                    } else {
                        // --- SOLTÓ EN EL OCÉANO ---
                        viewOriginal.visibility = View.VISIBLE
                    }
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    // Si la acción terminó y no se soltó bien, aseguramos que la bandera sea visible
                    val view = event.localState as View
                    if (!event.result) {
                        view.visibility = View.VISIBLE
                    }
                    true
                }
                else -> true
            }
        }

        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        btnClose.setOnClickListener {
            finish()
        }
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)


        val width = 200
        val height = (width * drawable.intrinsicHeight) / drawable.intrinsicWidth // Mantener proporción

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    // Función auxiliar para configurar cualquier bandera
    private fun configurarBandera(viewId: Int, nombrePaisXML: String, drawableId: Int) {
        val flagView = findViewById<View>(viewId)

        // TRUCO CLAVE: Guardamos el ID del recurso (R.drawable.xxx) dentro de la vista
        // para poder recuperarlo después en el evento de soltar.
        flagView.tag = drawableId

        flagView.setOnLongClickListener { view ->
            // 1. Pasamos el nombre del país (ej: "angola") en el ClipData
            val data = ClipData.newPlainText("pais_id", nombrePaisXML)

            val shadowBuilder = View.DragShadowBuilder(view)

            // Iniciamos el arrastre
            view.startDragAndDrop(data, shadowBuilder, view, 0)

            view.visibility = View.INVISIBLE
            true
        }
    }
}