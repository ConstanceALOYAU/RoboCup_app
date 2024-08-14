package com.example.robocup
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class JoystickView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var joystickCenterX: Float = 0f
    private var joystickCenterY: Float = 0f
    private var joystickRadius: Float = 0f
    private var handleX: Float = 0f
    private var handleY: Float = 0f
    private var handleRadius: Float = 0f
    private var valueX: Float = 0f
    private var valueY: Float = 0f


    private val paintBackground = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.FILL
    }

    private val paintHandle = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    init {
        // Initialize handle position to the center
        handleX = joystickCenterX
        handleY = joystickCenterY
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Calculate dimensions
        joystickCenterX = (w / 2).toFloat()
        joystickCenterY = (h / 2).toFloat()
        joystickRadius = min(w, h) / 3f
        handleRadius = joystickRadius / 3f

        // Reset the handle position to the center
        handleX = joystickCenterX
        handleY = joystickCenterY
    }

    override fun onDraw(canvas: Canvas) {
        // Draw the joystick background
        canvas.drawCircle(joystickCenterX, joystickCenterY, joystickRadius, paintBackground)

        // Draw the joystick handle
        canvas.drawCircle(handleX, handleY, handleRadius, paintHandle)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Calculate the vector from the center to the touch point
                val dx = event.x - joystickCenterX
                val dy = event.y - joystickCenterY
                val distance = sqrt(dx * dx + dy * dy)

                if (distance < joystickRadius) {
                    // Inside the joystick's bounds
                    handleX = event.x
                    handleY = event.y
                } else {
                    // Outside the joystick's bounds, clamp the handle to the edge
                    val angle = atan2(dy, dx)
                    handleX = joystickCenterX + joystickRadius * cos(angle)
                    handleY = joystickCenterY + joystickRadius * sin(angle)
                }

                // Invalidate to request a redraw
                invalidate()
                valueX = (handleX - joystickCenterX) / joystickRadius
                valueY = (handleY - joystickCenterY) / joystickRadius
                println(" Value Joystique $valueX, $valueY")

                true
            }
            MotionEvent.ACTION_UP -> {
                // Return the handle to the center when released
                handleX = joystickCenterX
                handleY = joystickCenterY
                invalidate()
                true
            }
            else -> false
        }
    }

    fun getDirection(): Pair<Float, Float> {
        // Calculate the normalized direction vector
        return Pair(valueX, valueY)
    }

}
