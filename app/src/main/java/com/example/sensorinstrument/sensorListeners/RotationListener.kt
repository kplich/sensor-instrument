package com.example.sensorinstrument.sensorListeners

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import android.widget.Button
import com.example.sensorinstrument.mainActivity.Note
import com.jsyn.ports.UnitInputPort

class RotationListener(private val windowManager: WindowManager,
                       private val oscFrequencyPort: UnitInputPort,
                       private val filterFrequencyPort: UnitInputPort,
                       private var middleNote: Note = Note.E4,
                       private val playingButton: Button
): SensorEventListener {

    private val minDegreeBackward = -15.0
    private val maxDegreeForward = 30.0

    private var pentatonicFrequencies: List<Double> = middleNote.getPentatonicFrequencies()

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
            playingButton.background = ColorDrawable(mapDegreesToButtonColor(orientation[1]))
        }
    }

    private fun mapDegreesToOscillatorFrequency(degrees: Float): Double {

        val numberOfNotes = pentatonicFrequencies.size
        val degreesForNote = 30

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

        return pentatonicFrequencies[noteIndex]
    }

    private fun mapDegreesToFilterFrequency(degrees: Float): Double {
        val minF = 100.0
        val maxF = 18000.0

        return transformValueBetweenRanges(degrees.toDouble(), minDegreeBackward, maxDegreeForward, maxF, minF)
    }

    fun setMiddleNote(newNote: Note) {
        middleNote = newNote
        pentatonicFrequencies = middleNote.getPentatonicFrequencies()
    }

    private fun mapDegreesToButtonColor(degrees: Float): Int {
        val mainColor = middleNote.color

        val mainR = Color.red(mainColor)
        val mainG = Color.green(mainColor)
        val mainB = Color.blue(mainColor)

        var newR = 0
        var newG = 0
        var newB = 0

        if(mainR != 0) {
            newR = transformValueBetweenRanges(degrees.toDouble(),
                minDegreeBackward, maxDegreeForward,
                mainR.toDouble(), 0.0).toInt()
        }

        if(mainG != 0) {
            newG = transformValueBetweenRanges(degrees.toDouble(),
                minDegreeBackward, maxDegreeForward,
                mainG.toDouble(), 0.0).toInt()
        }

        if(mainB != 0) {
            newB = transformValueBetweenRanges(degrees.toDouble(),
                minDegreeBackward, maxDegreeForward,
                mainB.toDouble(), 0.0).toInt()
        }

        println("mapping $degrees degrees to color: r=$newR, g=$newG, b=$newB")

        return Color.rgb(newR, newG, newB)
    }

    private fun transformValueBetweenRanges(value: Double,
                                    inputMin: Double, inputMax: Double,
                                    outputForMin: Double, outputForMax: Double): Double {
        return when {
            value <= inputMin -> outputForMin
            value >= inputMax -> outputForMax
            else -> {
                val dY = outputForMax - outputForMin
                val dX = inputMax - inputMin
                val a = dY/dX

                val b = outputForMin - a * inputMin

                a * value + b
            }
        }
    }
}