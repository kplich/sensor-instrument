package com.example.sensorinstrument.mainActivity

import android.graphics.Color

enum class Note(val noteName: String, val frequency: Double, val color: Int) {
      A3("A3",  220.0000 * 2,    Color.parseColor("#ff0000")),
    Ash3("A#3", 233.0819 * 2, Color.parseColor("#ff7f00")),
      B3("B3",  246.9417 * 2, Color.parseColor("#ffff00")),
      C4("C4",  261.6256 * 2, Color.parseColor("#7fff00")),
    Csh4("C#4", 277.1826 * 2, Color.parseColor("#00ff00")),
      D4("D4",  293.6648 * 2, Color.parseColor("#00ff7f")),
    Dsh4("D#4", 311.1270 * 2, Color.parseColor("#00ffff")),
      E4("E4",  329.6276 * 2, Color.parseColor("#007fff")),
      F4("F4",  349.2282 * 2, Color.parseColor("#0000ff")),
    Fsh4("F#4", 369.2282 * 2, Color.parseColor("#7f00ff")),
      G4("G4",  391.9954 * 2, Color.parseColor("#ff00ff")),
    Gsh4("G#4", 415.3047 * 2, Color.parseColor("#ff007f")),
      A4("A4",  440.0000 * 2,    Color.parseColor("#ff0000"));

    fun getPentatonicFrequencies(): List<Double> {
        return pentatonicDegrees.map { degree ->
            frequency * Math.pow(2.toDouble(), degree.toDouble() / 12)
        }
    }

    companion object {
        val pentatonicDegrees = intArrayOf(-12, -9, -7, -5, -2, 0, 3, 5, 7, 10, 12)
    }
}