package com.example.sensorinstrument.sensorListeners

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.View
import android.view.WindowManager
import com.example.sensorinstrument.maxDegreeForward
import com.example.sensorinstrument.minColorValue
import com.example.sensorinstrument.minDegreeBackward
import com.example.sensorinstrument.transformValueBetweenRanges

class RotationListener(private val windowManager: WindowManager,
                       private val coloredView: View
): SensorEventListener {


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val worldAxisForDeviceAxisX: Int
            val worldAxisForDeviceAxisY: Int

            // Remap the axes as if the device screen was the instrument panel,
            // and adjust the rotation matrix for the device orientation.
            when (windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_0 -> {
                    worldAxisForDeviceAxisX = SensorManager.AXIS_X
                    worldAxisForDeviceAxisY = SensorManager.AXIS_Z
                }
                Surface.ROTATION_90 -> {
                    worldAxisForDeviceAxisX = SensorManager.AXIS_Z
                    worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X
                }
                Surface.ROTATION_180 -> {
                    worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X
                    worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z
                }
                Surface.ROTATION_270 -> {
                    worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z
                    worldAxisForDeviceAxisY = SensorManager.AXIS_X
                }
                else -> {
                    worldAxisForDeviceAxisX = SensorManager.AXIS_X
                    worldAxisForDeviceAxisY = SensorManager.AXIS_Z
                }
            }

            val adjustedRotationMatrix = FloatArray(9)
            SensorManager.remapCoordinateSystem(
                rotationMatrix, worldAxisForDeviceAxisX,
                worldAxisForDeviceAxisY, adjustedRotationMatrix
            )

            // Transform rotation matrix into azimuth/pitch/roll
            val orientation = FloatArray(3)
            SensorManager.getOrientation(adjustedRotationMatrix, orientation)

            for (i in 0..2) {
                orientation[i] = Math.toDegrees(orientation[i].toDouble()).toFloat()
            }

            coloredView.background = ColorDrawable(mapDegreesToViewColor(orientation[1]))
        }
    }

    private fun mapDegreesToViewColor(degrees: Float): Int {
        val mainColor = Color.parseColor("#ff0000")

        val mainR = Color.red(mainColor)
        val mainG = Color.green(mainColor)
        val mainB = Color.blue(mainColor)

        var newR = 0
        var newG = 0
        var newB = 0

        if (mainR != 0) {
            newR = transformValueBetweenRanges(
                degrees.toDouble(),
                minDegreeBackward, maxDegreeForward,
                mainR.toDouble(), minColorValue
            ).toInt()
        }

        if (mainG != 0) {
            newG = transformValueBetweenRanges(
                degrees.toDouble(),
                minDegreeBackward, maxDegreeForward,
                mainG.toDouble(), minColorValue
            ).toInt()
        }

        if (mainB != 0) {
            newB = transformValueBetweenRanges(
                degrees.toDouble(),
                minDegreeBackward, maxDegreeForward,
                mainB.toDouble(), minColorValue
            ).toInt()
        }

        return Color.rgb(newR, newG, newB)
    }
}