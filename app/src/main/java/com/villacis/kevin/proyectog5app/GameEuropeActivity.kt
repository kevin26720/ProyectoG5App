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


class GameEuropeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_europe)


        val interactiveMap = findViewById<InteractiveMapView>(R.id.ivMap)
        interactiveMap.loadMap(R.drawable.europa)

        configurarBandera(R.id.flagItem11, "belgica", R.drawable.belgica)
        configurarBandera(R.id.flagItem8, "suiza", R.drawable.suiza)
        configurarBandera(R.id.flagItem10, "alemania", R.drawable.alemania)
        configurarBandera(R.id.flagItem13, "finlandia", R.drawable.finlandia)
        configurarBandera(R.id.flagItem7, "inglaterra", R.drawable.inglaterra)
        configurarBandera(R.id.flagItem3, "grecia", R.drawable.grecia)
        configurarBandera(R.id.flagItem12, "croacia", R.drawable.croacia)
        configurarBandera(R.id.flagItem14, "hungria", R.drawable.hungria)
        configurarBandera(R.id.flagItem4, "italia", R.drawable.italia)
        configurarBandera(R.id.flagItem15, "lituania", R.drawable.lituania)
        configurarBandera(R.id.flagItem5, "noruega", R.drawable.noruega)
        configurarBandera(R.id.flagItem1, "espa_a", R.drawable.espa_a)
        configurarBandera(R.id.flagItem6, "paises_bajos", R.drawable.paises_bajos)
        configurarBandera(R.id.flagItem2, "francia", R.drawable.francia)


        // 3. Configurar el SOLTAR (Drop) en el Mapa
        interactiveMap.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true // Aceptar el evento de arrastre

                DragEvent.ACTION_DROP -> {
                    // 1. Recuperamos el ID del país
                    val idPaisArrastrado = event.clipData.getItemAt(0).text.toString() //

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

        // TRUCO: Forzamos un tamaño pequeño estándar (ej. 200px de ancho)
        // No necesitamos más calidad que esta para un mapa en el celular.
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

        // TRUCO CLAVE: Guardamos el ID del recurso dentro de la vista
        // para poder recuperarlo después en el evento de soltar.
        flagView.tag = drawableId

        flagView.setOnLongClickListener { view ->
            // 1. Pasamos el nombre del país en el ClipData
            val data = ClipData.newPlainText("pais_id", nombrePaisXML)

            val shadowBuilder = View.DragShadowBuilder(view)

            // Iniciamos el arrastre
            view.startDragAndDrop(data, shadowBuilder, view, 0)

            view.visibility = View.INVISIBLE
            true
        }
    }
}
