package com.example.sensorinstrument.instruments

import com.jsyn.JSyn
import com.jsyn.Synthesizer
import com.jsyn.unitgen.*

class SineSynthesizer: Circuit() {
    companion object {
        const val OSCILLATOR_FREQUENCY = "oscillatorFrequency"
        const val LP_FILTER_FREQUENCY = "lpFilterFrequency"
        const val OUTPUT = "output"
    }

    private val synth: Synthesizer = JSyn.createSynthesizer(JSynAndroidAudioDevice())
    private val oscillator: SineOscillator = SineOscillator()
    private val lpFilter: FilterLowPass = FilterLowPass()
    private val output: LineOut = LineOut()

    init {
        synth.apply {
            add(oscillator)
            add(lpFilter)
            add(output)

            addPort(oscillator.frequency, OSCILLATOR_FREQUENCY)
            addPort(lpFilter.frequency, LP_FILTER_FREQUENCY)
        }

        oscillator.output.connect(lpFilter.input)

        lpFilter.output.connect(0, output.input, 0)
        lpFilter.output.connect(0, output.input, 1)

        lpFilter.Q.set(0.01)
        lpFilter.amplitude.set(1.0)
    }

    override fun start() {
        synth.start()
    }

    override fun stop() {
        output.stop()
        synth.stop()
    }

    fun startPlaying() {
        output.start()
    }

    fun stopPlaying() {
        output.stop()
    }
}