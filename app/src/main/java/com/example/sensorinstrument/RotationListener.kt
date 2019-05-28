package com.example.sensorinstrument

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager

class RotationListener(private val windowManager: WindowManager): SensorEventListener {

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
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



            var lowPassFreq = -650f * orientation[1] + 21250f

            if (lowPassFreq < 5000) {
                lowPassFreq = 5000f
            } else if (lowPassFreq > 18000) {
                lowPassFreq = 18000f
            }
        }
    }

    private fun mapDegreesToOscillatorFrequency(degrees: Double): Double {
        val notes = doubleArrayOf(
            440.0,
            523.251130601,
            587.329535835,
            659.255113826,
            783.990871963,
            880.0,
            1046.5022612,
            1174.65907167,
            1318.51022765,
            1567.98174393,
            1760.0
        )
        val degreesForNote = 30
        val degreesForHalfRange: Double = if(notes.size % 2 == 0) {
            (notes.size / 2) * degreesForNote
        }
        else {
            ((notes.size / 2) * degreesForNote) + degreesForNote / 2
        }.toDouble()

        var noteIndex = (degrees + degreesForHalfRange).toInt() / degreesForNote
        if (noteIndex < 0) {
            noteIndex = 0
        } else if (noteIndex > 10) {
            noteIndex = 10
        }

        return notes[noteIndex]
    }

    private fun mapDegreesToFilterFrequency(degrees: Double): Double {
        val minDegree = 0.0
        val maxDegree = 30.0

        val minF = 5000.0
        val maxF = 20000.0

        val a = (maxF - minF) / (maxDegree - minDegree)
        val b = maxF + a * minDegree

        var lowPassFreq = -a * degrees + b

        if (lowPassFreq < minF) {
            lowPassFreq = minF
        } else if (lowPassFreq > maxF) {
            lowPassFreq = maxF
        }

        return lowPassFreq
    }
}