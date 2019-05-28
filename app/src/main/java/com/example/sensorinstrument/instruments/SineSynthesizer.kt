package com.example.sensorinstrument.instruments

import com.jsyn.JSyn
import com.jsyn.Synthesizer
import com.jsyn.ports.UnitInputPort
import com.jsyn.unitgen.Circuit
import com.jsyn.unitgen.FilterLowPass
import com.jsyn.unitgen.LineOut
import com.jsyn.unitgen.SineOscillator

class SineSynthesizer: Circuit() {
    private val synth: Synthesizer = JSyn.createSynthesizer(JSynAndroidAudioDevice())
    private val oscillator: SineOscillator = SineOscillator()
    private val lpFilter: FilterLowPass = FilterLowPass()
    private val output: LineOut = LineOut()

    init {
        synth.apply {
            add(oscillator)
            add(lpFilter)
            add(output)
        }

        oscillator.output.connect(lpFilter.input)

        lpFilter.output.connect(0, output.input, 0)
        lpFilter.output.connect(0, output.input, 1)

        lpFilter.Q.set(0.01)
        lpFilter.amplitude.set(1.0)
    }

    override fun start() {
        synth.start()
        //output.start()
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

    fun getOscillatorFrequencyPort(): UnitInputPort {
        return oscillator.frequency
    }

    fun getFilterFrequencyPort(): UnitInputPort {
        return lpFilter.frequency
    }
}