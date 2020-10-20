package com.example.sensorinstrument.instruments

import com.jsyn.ports.UnitInputPort
import com.jsyn.ports.UnitOutputPort
import com.jsyn.unitgen.Circuit
import com.jsyn.unitgen.UnitOscillator

class StupidSynthesizer(oscillatorType: OscillatorType) : Circuit() {
    private var osc: UnitOscillator
    var amplitude: UnitInputPort
        private set

    var frequency: UnitInputPort
        private set

    var output: UnitOutputPort
        private set

    companion object {
        const val AMPLITUDE = "Amplitude"
        const val FREQUENCY = "Frequency"
        const val OUTPUT = "Output"
    }

    init {
        add(oscillatorType.getOscillator().also { osc = it })
        addPort(osc.amplitude.also { amplitude = it }, AMPLITUDE)
        addPort(osc.frequency.also { frequency = it }, FREQUENCY)
        addPort(osc.output.also { output = it }, OUTPUT)

        frequency.set(440.0)
        amplitude.set(1.0)
    }
}