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
import com.example.sensorinstrument.instruments.TripleOscSynthesizer
import com.example.sensorinstrument.transformValueBetweenRanges
import kotlin.math.pow

class RotationListener(private val windowManager: WindowManager,
                       private val coloredView: View,
                       private val synthesizer: TripleOscSynthesizer
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
            synthesizer.setFrequency(mapDegreesToOscillatorFrequency(orientation[2]))
                //synthesizer.setFilterCutoff(mapDegreesToFilterCutoff(orientation[1]))
            //synthesizer.setFilterQ(mapDegreesToFilterResonance(orientation[0]))
        }
    }

    private val minDegreeBackward = 0.0
    private val maxDegreeForward = 50.0

    private val pentatonicDegrees = intArrayOf(-12, -9, -7, -5, -2, 0, 3, 5, 7, 10, 12)

    private fun getPentatonicFrequencies(): List<Double> {
        return pentatonicDegrees.map { degree ->
            440 * 2.toDouble().pow((degree.toDouble() + 4) / 12)
        }
    }

    private var pentatonicFrequencies: List<Double> = getPentatonicFrequencies()

    private fun mapDegreesToOscillatorFrequency(degrees: Float): Double {

        val numberOfNotes = pentatonicFrequencies.size
        val degreesForNote = 30

        if(numberOfNotes * degreesForNote > 360) {
            throw IllegalArgumentException("Too many ($numberOfNotes) or too wide ($degreesForNote) notes!")
        }

        val degreesForHalfRange: Double =   if(numberOfNotes % 2 == 0) {
            (numberOfNotes / 2) * degreesForNote
        }
        else {
            ((numberOfNotes / 2) * degreesForNote) + degreesForNote / 2
        }.toDouble()

        var noteIndex = (degrees + degreesForHalfRange).toInt() / degreesForNote
        if (noteIndex < 0) {
            noteIndex = 0
        } else if (noteIndex > numberOfNotes-1) {
            noteIndex = numberOfNotes-1
        }

        return pentatonicFrequencies[noteIndex]
    }

    private fun mapDegreesToFilterCutoff(degrees: Float): Double {
        return transformValueBetweenRanges(
            degrees.toDouble(),
            minDegreeBackward, maxDegreeForward,
            200.0, 500.0
        )
    }

    private fun mapDegreesToViewColor(degrees: Float): Int {
        val hue = transformValueBetweenRanges(
            degrees.toDouble(),
            minDegreeBackward, maxDegreeForward,
            0.0, 360.0
        ).toFloat()
        return Color.HSVToColor(floatArrayOf(hue, 0.9f, 0.9f))
    }

    private fun mapDegreesToFilterResonance(degrees: Float): Double {
        return transformValueBetweenRanges(
            degrees.toDouble(),
            -180.0, 180.0,
            2.0, 4.0
        )
    }
}