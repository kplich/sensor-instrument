package com.example.sensorinstrument.mainActivity

import com.example.sensorinstrument.instruments.JSynAndroidAudioDevice
import com.example.sensorinstrument.instruments.TripleOscSynthesizer
import com.example.sensorinstrument.transformValueBetweenRanges
import com.jsyn.JSyn
import com.jsyn.instruments.DualOscillatorSynthVoice
import com.jsyn.unitgen.LineOut
import com.jsyn.util.PolyphonicInstrument

class SynthesizerWrapper (
    private var middleNote: Note
) {

    private val synth = JSyn.createSynthesizer(JSynAndroidAudioDevice())
    private var lineOut: LineOut
    private var instrument: PolyphonicInstrument

    private val minDegreeBackward = -25.0
    private val maxDegreeForward = 15.0

    private var pentatonicFrequencies: List<Double> = middleNote.getPentatonicFrequencies()

    private fun mapDegreesToOscillatorFrequency(degrees: Float): Double {

        val numberOfNotes = pentatonicFrequencies.size
        val degreesForNote = 30

        if (numberOfNotes * degreesForNote > 360) {
            throw IllegalArgumentException("Too many ($numberOfNotes) or too wide ($degreesForNote) notes!")
        }

        val degreesForHalfRange: Double = if (numberOfNotes % 2 == 0) {
            (numberOfNotes / 2) * degreesForNote
        } else {
            ((numberOfNotes / 2) * degreesForNote) + degreesForNote / 2
        }.toDouble()

        var noteIndex = (degrees + degreesForHalfRange).toInt() / degreesForNote
        if (noteIndex < 0) {
            noteIndex = 0
        } else if (noteIndex > numberOfNotes - 1) {
            noteIndex = numberOfNotes - 1
        }

        return pentatonicFrequencies[noteIndex]
    }

    private fun mapDegreesToFilterFrequency(degrees: Float): Double {
        val minF = 500.0
        val maxF = 20000.0

        return transformValueBetweenRanges(
            degrees.toDouble(),
            minDegreeBackward,
            maxDegreeForward,
            maxF,
            minF
        )
    }

    init {
        synth.add(LineOut().also { lineOut = it })
        val voices = arrayOf(DualOscillatorSynthVoice())
        synth.add(PolyphonicInstrument(voices).also { instrument = it })

        lineOut.input.disconnectAll(0)
        lineOut.input.disconnectAll(1)

        instrument.output.connect(0, lineOut.input, 0)
        instrument.output.connect(0, lineOut.input, 1)

        instrument.amplitude.set(1.0)
    }

    fun setMiddleNote(newNote: Note) {
        middleNote = newNote
        pentatonicFrequencies = middleNote.getPentatonicFrequencies()
    }

    fun startSynth() {
        synth.start()
        lineOut.start()
    }

    fun stopSynth() {
        lineOut.stop()
    }

    fun noteOn() {
        println("note on $id")
        instrument.noteOn(id, 1000.0, 1.0, synth.createTimeStamp())
    }

    fun noteOff() {
        println("not off $id")
        instrument.noteOff(id, synth.createTimeStamp())
        id++
    }

    companion object {
        var id: Int = 0
    }
}