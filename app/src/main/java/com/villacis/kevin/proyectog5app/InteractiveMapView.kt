package com.villacis.kevin.proyectog5app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser
import android.graphics.Bitmap

class InteractiveMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val countryPaths = mutableMapOf<String, Path>()
    private val originalPaths = mutableMapOf<String, Path>() // Para guardar los originales sin transformar
    private var viewportWidth = 0f
    private var viewportHeight = 0f
    private val scaleMatrix = Matrix()
    private val flagsMap = mutableMapOf<String, Bitmap>()

    // Cargar los paths desde el XML del vector
    fun loadMap(xmlResId: Int) {
        setImageResource(xmlResId) // Mostramos la imagen visualmente
        countryPaths.clear()
        originalPaths.clear()

        val parser = context.resources.getXml(xmlResId)
        var eventType = parser.eventType

        // 1. Parseo manual del XML para extraer pathData y nombres
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.name == "vector") {
                    // Obtenemos el tamaño original del lienzo (viewport)
                    viewportWidth = parser.getAttributeFloatValue("http://schemas.android.com/apk/res/android", "viewportWidth", 100f)
                    viewportHeight = parser.getAttributeFloatValue("http://schemas.android.com/apk/res/android", "viewportHeight", 100f)
                } else if (parser.name == "path") {
                    val name = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "name")
                    val pathData = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "pathData")

                    if (name != null && pathData != null) {
                        try {
                            // USAMOS LA VERSIÓN SEGURA DE ANDROIDX
                            val path = PathParser.createPathFromPathData(pathData)
                            originalPaths[name] = path
                            countryPaths[name] = Path(path) // Copia inicial
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            eventType = parser.next()
        }
    }

    // 2. Ajustar los paths al tamaño real de la pantalla (Responsive)
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (viewportWidth == 0f || viewportHeight == 0f) return

        // Calculamos la escala "fitCenter" (igual que ImageView)
        val scaleX = w / viewportWidth
        val scaleY = h / viewportHeight
        val scale = minOf(scaleX, scaleY)

        val dx = (w - viewportWidth * scale) / 2
        val dy = (h - viewportHeight * scale) / 2

        scaleMatrix.reset()
        scaleMatrix.setScale(scale, scale)
        scaleMatrix.postTranslate(dx, dy)

        // Transformamos todos los paths para que coincidan con lo que ve el usuario
        for ((name, originalPath) in originalPaths) {
            val transformedPath = Path()
            originalPath.transform(scaleMatrix, transformedPath)
            countryPaths[name] = transformedPath
        }
    }

    fun setFlagForCountry(countryId: String, originalBitmap:Bitmap) {
        val path = countryPaths[countryId]

        if (path != null) {
            // 1. Calculamos cuánto mide el país en la pantalla (ancho y alto)
            val bounds = RectF()
            path.computeBounds(bounds, true)

            // Aseguramos que tenga al menos 1 pixel para no causar error
            val targetWidth = bounds.width().toInt().coerceAtLeast(1)
            val targetHeight = bounds.height().toInt().coerceAtLeast(1)

            // 2. Creamos una versión "mini" de la bandera, ajustada a ese tamaño
            // Esto reduce el uso de memoria de 200MB a unos pocos KB
            val scaledBitmap = Bitmap.createScaledBitmap(
                originalBitmap,
                targetWidth,
                targetHeight,
                true
            )

            // 3. Guardamos la versión optimizada
            flagsMap[countryId] = scaledBitmap

            // (Opcional) Si ya no vas a usar la originalBitmap en otro lado, puedes reciclarla para liberar memoria inmediatamente:
            if (originalBitmap != scaledBitmap) originalBitmap.recycle()

            invalidate() // Redibujar
        }
    }

    override fun onDraw(canvas: Canvas) {
        // 1. Primero se dibuja el mapa base (la imagen gris/blanca de fondo)
        super.onDraw(canvas)

        // 2. Ahora dibujamos las banderas ENCIMA de los países correspondientes
        for ((countryId, bitmap) in flagsMap) {
            val path = countryPaths[countryId]

            if (path != null) {
                canvas.save() // Guardamos el estado actual del canvas

                // A. EL RECORTE MAGICO (CLIP)
                // Le decimos al canvas: "Solo dibuja dentro de estas líneas"
                try {
                    canvas.clipPath(path)
                } catch (e: UnsupportedOperationException) {
                    // Algunos dispositivos muy viejos pueden fallar aquí, pero es raro en Android moderno
                }

                // B. CALCULAR DÓNDE DIBUJAR LA IMAGEN
                // Obtenemos los límites (rectángulo) del país para estirar la bandera ahí
                val bounds = RectF()
                path.computeBounds(bounds, true)

                // Opcional: Expandir un pelín la bandera (1px) para evitar bordes blancos feos por el antialiasing
                // bounds.inset(-1f, -1f)

                // C. DIBUJAR LA BANDERA
                // null significa que pintamos toda la imagen de la bandera
                // bounds es el destino: estira la bandera para llenar el país
                canvas.drawBitmap(bitmap, null, bounds, null)

                canvas.restore() // Restauramos el canvas para el siguiente dibujo
            }
        }
    }

    // 3. Método público para detectar qué país se tocó
    fun getCountryAt(x: Float, y: Float): String? {
        for ((name, path) in countryPaths) {
            val rectF = RectF()
            path.computeBounds(rectF, true)
            val region = Region()
            region.setPath(path, Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()))

            if (region.contains(x.toInt(), y.toInt())) {
                return name
            }
        }
        return null
    }

    // Helper para leer floats del XML
    private fun XmlPullParser.getAttributeFloatValue(namespace: String, name: String, defaultValue: Float): Float {
        val value = getAttributeValue(namespace, name) ?: return defaultValue
        return value.replace("dp", "").toFloatOrNull() ?: defaultValue
    }
}