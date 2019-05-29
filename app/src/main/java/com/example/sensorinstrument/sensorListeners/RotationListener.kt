package com.example.sensorinstrument.sensorListeners

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import com.jsyn.ports.UnitInputPort

class RotationListener(private val windowManager: WindowManager,
                       private val oscFrequencyPort: UnitInputPort,
                       private val filterFrequencyPort: UnitInputPort): SensorEventListener {

    companion object {
        const val A3 = 220.0
        const val
    }

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

            oscFrequencyPort.set(mapDegreesToOscillatorFrequency(orientation[2]))
            filterFrequencyPort.set(mapDegreesToFilterFrequency(orientation[1]))
        }
    }

    private fun mapDegreesToOscillatorFrequency(degrees: Float): Double {
        val notes = List(24) {note -> 138.5913 * Math.pow(2.0, (note.toDouble()/12))}

        val numberOfNotes = notes.size
        val degreesForNote = 15

        if(numberOfNotes * degreesForNote > 360) {
            throw IllegalArgumentException("Too many ($numberOfNotes) or too wide ($degreesForNote) notes!")
        }

        val degreesForHalfRange: Double = if(numberOfNotes % 2 == 0) {
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

        return notes[noteIndex]
    }

    private fun mapDegreesToFilterFrequency(degrees: Float): Double {
        val minDegree = -10.0
        val maxDegree = 60.0

        val minF = 100.0
        val maxF = 18000.0

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