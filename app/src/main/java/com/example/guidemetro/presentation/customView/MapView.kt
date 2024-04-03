package com.example.guidemetro.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guidemetro.R
import com.example.guidemetro.presentation.fragment.StationFragment
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class MapView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val radius: Float
    private val stationCoordinates = mutableListOf<Pair<Float, Float>>()

    private var scaleFactor = 1.0f
    private var translateX = 0.0f
    private var translateY = 0.0f

    private val scaleDetector: ScaleGestureDetector
    private val gestureDetector: GestureDetector

    private val sensitivityX = 0.5f
    private val sensitivityY = 0.5f

    private val minScaleFactor = 0.7f
    private val maxScaleFactor = 2.0f

    private val mapCenterX: Float
    private val mapCenterY: Float

    private lateinit var stationNames: Array<String>

    init {
        initializeStationNames()

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()
        mapCenterX = screenWidth / 2
        mapCenterY = screenHeight / 2
        radius = 1.5f * screenWidth / 4

        for (i in 0 until 12) {
            val angle = 360f / 12 * i
            val stationX =
                mapCenterX + radius * cos(Math.toRadians(angle.toDouble())).toFloat()
            val stationY =
                mapCenterY + radius * sin(Math.toRadians(angle.toDouble())).toFloat()
            stationCoordinates.add(Pair(stationX, stationY))
        }

        scaleDetector = ScaleGestureDetector(context, ScaleListener())
        gestureDetector = GestureDetector(context, GestureListener())
    }

    private fun initializeStationNames() {
        stationNames = arrayOf(
            context.getString(R.string.kurskaya), context.getString(R.string.taganskaya), context.getString(R.string.paveletskaya), context.getString(R.string.dobryninskaya), context.getString(R.string.oktyabrskaya),
            context.getString(R.string.parkkultury), context.getString(R.string.kiyevskaya), context.getString(R.string.krasnopresnenskaya), context.getString(R.string.belorusskaya), context.getString(R.string.novoslobodskaya),
            context.getString(R.string.prospektmira), context.getString(R.string.komsomolskaya)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.scale(scaleFactor, scaleFactor, mapCenterX, mapCenterY)
        canvas.translate(translateX / scaleFactor, translateY / scaleFactor)

        val paintCircle = Paint()
        paintCircle.color = Color.rgb(139, 69, 19)
        paintCircle.style = Paint.Style.STROKE
        paintCircle.strokeWidth = 13f // Толщина кольца
        canvas.drawCircle(mapCenterX, mapCenterY, radius, paintCircle)

        val paintStation = Paint()
        paintStation.color = Color.rgb(139, 69, 19)
        paintStation.style = Paint.Style.FILL
        val stationRadius = 20f // Размер точек станций
        paintStation.strokeWidth = 5f
        paintStation.isAntiAlias = true

        val paintStationOutline = Paint()
        paintStationOutline.color = Color.BLACK
        paintStationOutline.style = Paint.Style.STROKE
        paintStationOutline.strokeWidth = 2f
        paintStationOutline.isAntiAlias = true

        val paintStationInner = Paint()
        paintStationInner.color = Color.WHITE
        paintStationInner.style = Paint.Style.FILL

        val paintText = Paint()
        paintText.setColor(resources.getColor(R.color.md_theme_onBackground))
        paintText.textSize = 30f
        var i = 0

        for ((x, y) in stationCoordinates) {
            canvas.drawCircle(x, y, stationRadius, paintStation)
            canvas.drawCircle(x, y, stationRadius, paintStationOutline)
            canvas.drawCircle(x, y, stationRadius / 2, paintStationInner)

            when (stationNames[i]) {
                context.getString(R.string.novoslobodskaya) -> canvas.drawText(stationNames[i], x - 100, y - 30, paintText)
                context.getString(R.string.dobryninskaya) -> canvas.drawText(stationNames[i], x - 100, y + 40, paintText)
                context.getString(R.string.paveletskaya) -> canvas.drawText(stationNames[i], x, y + 40, paintText)
                context.getString(R.string.oktyabrskaya) -> canvas.drawText(stationNames[i], x - 200, y + 25, paintText)
                context.getString(R.string.parkkultury) -> canvas.drawText(stationNames[i], x - 230, y - 10, paintText)
                context.getString(R.string.kiyevskaya) -> canvas.drawText(stationNames[i], x - 150, y - 10, paintText)
                context.getString(R.string.krasnopresnenskaya) -> canvas.drawText(stationNames[i], x - 300, y - 10, paintText)
                context.getString(R.string.belorusskaya) -> canvas.drawText(stationNames[i], x - 200, y - 10, paintText)
                else -> canvas.drawText(stationNames[i], x + 20, y, paintText)
            }
            i++
        }
        scaleFactor = Math.max(minScaleFactor, Math.min(scaleFactor, maxScaleFactor))

        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val scaledX = (event.x - mapCenterX - translateX) / scaleFactor + mapCenterX
                val scaledY = (event.y - mapCenterY - translateY) / scaleFactor + mapCenterY

                // Проверяем, была ли нажата какая-либо точка станции
                for ((x, y) in stationCoordinates) {
                    val distance =
                        sqrt((x - scaledX).toDouble().pow(2) + (y - scaledY).toDouble().pow(2))
                    if (distance <= 30 * scaleFactor) {
                        val stationName = getStationName(x, y)
                        val fragment = StationFragment.newInstance(stationName)
                        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frameLayout, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                        return true
                    }

                }
            }
        }
        return true
    }

    private fun getStationName(x: Float, y: Float): String {
        val index = stationCoordinates.indexOfFirst { it.first == x && it.second == y }
        return if (index != -1 && index < stationNames.size) {
            stationNames[index]
        } else {
            "Неизвестная станция"
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor

            // Ограничение по масштабу
            scaleFactor = scaleFactor.coerceIn(minScaleFactor, maxScaleFactor)

            return true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            translateX -= distanceX * sensitivityX
            translateY -= distanceY * sensitivityY

            return true
        }
    }
}

