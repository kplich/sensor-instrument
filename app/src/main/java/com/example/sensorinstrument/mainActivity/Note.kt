package com.example.sensorinstrument.mainActivity

enum class Note(val noteName: String, val frequency: Double) {
      A3("A3",220.0),
    Ash3("A#3", 233.0819),
      B3("B3",246.9417),
      C4("C4", 261.6256),
    Csh4("C#4", 277.1826),
      D4("D4", 293.6648),
    Dsh4("D#4", 311.1270),
      E4("E4", 329.6276),
      F4("F4", 349.2282),
    Fsh4("F#4", 369.2282),
      G4("G4", 391.9954),
    Gsh4("G#4", 415.3047),
      A4("A4", 440.0);

    fun getPentatonicFrequencies(): List<Double> {
        return pentatonicDegrees.map { degree ->
            frequency * Math.pow(2.toDouble(), degree.toDouble() / 12)
        }
    }

    companion object {
        val pentatonicDegrees = intArrayOf(-12, -10, -7, -5, -3, 0, 3, 5, 7, 10, 12)
    }
}