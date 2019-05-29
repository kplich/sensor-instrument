package com.example.sensorinstrument.mainActivity

import android.graphics.Color

enum class Note(val noteName: String, val frequency: Double, val color: Int) {
      A3( "A4",  220.0000 * 2,    Color.parseColor("#ff0000")),
    Ash3("A#4", 233.0819 * 2, Color.parseColor("#ff7f00")),
      B3( "B4",  246.9417 * 2, Color.parseColor("#ffff00")),
      C5( "C5",  261.6256 * 2, Color.parseColor("#7fff00")),
    Csh5("C#5", 277.1826 * 2, Color.parseColor("#00ff00")),
      D5( "D5",  293.6648 * 2, Color.parseColor("#00ff7f")),
    Dsh5("D#5", 311.1270 * 2, Color.parseColor("#00ffff")),
      E5( "E5",  329.6276 * 2, Color.parseColor("#007fff")),
      F5( "F5",  349.2282 * 2, Color.parseColor("#0000ff")),
    Fsh5("F#5", 369.2282 * 2, Color.parseColor("#7f00ff")),
      G5( "G5",  391.9954 * 2, Color.parseColor("#ff00ff")),
    Gsh5("G#5", 415.3047 * 2, Color.parseColor("#ff007f")),
      A5( "A5",  440.0000 * 2,    Color.parseColor("#ff0000"));

    fun getPentatonicFrequencies(): List<Double> {
        return pentatonicDegrees.map { degree ->
            frequency * Math.pow(2.toDouble(), degree.toDouble() / 12)
        }
    }

    companion object {
        val pentatonicDegrees = intArrayOf(-12, -9, -7, -5, -2, 0, 3, 5, 7, 10, 12)
    }
}