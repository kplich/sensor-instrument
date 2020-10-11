package com.example.sensorinstrument.instruments

import com.jsyn.JSyn
import com.jsyn.Synthesizer
import com.jsyn.unitgen.*

class SineSynthesizer: Circuit() {
    companion object {
        const val SINE_OSCILLATOR_FREQUENCY = "sineOscillatorFrequency"
        const val SAWTOOTH_OSCILLATOR_FREQUENCY = "sawtoothOscillatorFrequency"
        const val LP_FILTER_FREQUENCY = "lpFilterFrequency"
        const val OUTPUT = "output"
    }

    private val synth: Synthesizer = JSyn.createSynthesizer(JSynAndroidAudioDevice())
    private val sineOscillator: SineOscillator = SineOscillator()
    private val sawtoothOscillator = SawtoothOscillator()
    private val lpFilter: FilterLowPass = FilterLowPass()
    private val output: LineOut = LineOut()

    init {
        synth.apply {
            add(sineOscillator)
            add(sawtoothOscillator)
            add(lpFilter)
            add(output)

            addPort(sawtoothOscillator.frequency, SAWTOOTH_OSCILLATOR_FREQUENCY)
            addPort(sineOscillator.frequency, SINE_OSCILLATOR_FREQUENCY)
            addPort(lpFilter.frequency, LP_FILTER_FREQUENCY)
        }

        sineOscillator.output.connect(lpFilter.input)
        sawtoothOscillator.output.connect(lpFilter.input)

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