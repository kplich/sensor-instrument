package com.example.sensorinstrument.sensorListeners

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.example.sensorinstrument.mainActivity.Note
import com.jsyn.ports.UnitInputPort

class InteractionManager(private val oscFrequencyPort: UnitInputPort,
                         private val filterFrequencyPort: UnitInputPort,
                         private val coloredView: View,
                         private var middleNote: Note = Note.E5) {

    private val minDegreeBackward = -20.0
    private val maxDegreeForward = 20.0

    private val minColorValue = 95.0

    private var pentatonicFrequencies: List<Double> = middleNote.getPentatonicFrequencies()

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

    private fun mapDegreesToFilterFrequency(degrees: Float): Double {
        val minF = 500.0
        val maxF = 20000.0

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
                mainR.toDouble(), minColorValue).toInt()
        }

        if(mainG != 0) {
            newG = transformValueBetweenRanges(degrees.toDouble(),
                minDegreeBackward, maxDegreeForward,
                mainG.toDouble(), minColorValue).toInt()
        }

        if(mainB != 0) {
            newB = transformValueBetweenRanges(degrees.toDouble(),
                minDegreeBackward, maxDegreeForward,
                mainB.toDouble(), minColorValue).toInt()
        }

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

    fun interact(orientation: FloatArray) {
        oscFrequencyPort.set(mapDegreesToOscillatorFrequency(orientation[2]))
        filterFrequencyPort.set(mapDegreesToFilterFrequency(orientation[1]))
        coloredView.background = ColorDrawable(mapDegreesToButtonColor(orientation[1]))
    }
}