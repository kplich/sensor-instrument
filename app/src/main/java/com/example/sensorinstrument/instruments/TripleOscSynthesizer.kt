package com.example.sensorinstrument.instruments

import com.jsyn.ports.UnitInputPort
import com.jsyn.ports.UnitOutputPort
import com.jsyn.unitgen.*
import com.softsynth.shared.time.TimeStamp

class TripleOscSynthesizer: Circuit(), UnitVoice {

    private var frequencyPassThrough = PassThrough()
    private var osc1: UnitOscillator = SineOscillator()
    //private var osc2: StupidSynthesizer = StupidSynthesizer(OscillatorType.SINE)
    //private var osc3: StupidSynthesizer = StupidSynthesizer(OscillatorType.SINE)
    private var amplitudeEnvelope = EnvelopeDAHDSR()

    var frequency: UnitInputPort

    var osc1Amplitude: UnitInputPort
    //var osc2Amplitude: UnitInputPort
    //var osc3Amplitude: UnitInputPort

    var mixedOutput: UnitOutputPort

    init {
        add(frequencyPassThrough)
        add(osc1)
        //add(osc2)
        //add(osc3)
        add(amplitudeEnvelope)

        frequencyPassThrough.output.connect(osc1.frequency)
        //frequencyPassThrough.output.connect(osc2.frequency)
        //frequencyPassThrough.output.connect(osc3.frequency)

        osc1.output.connect(amplitudeEnvelope.input)
        //osc2.output.connect(amplitudeEnvelope.input)
        //osc3.output.connect(amplitudeEnvelope.input)

        addPort(osc1.amplitude.also { osc1Amplitude = it }, OSC_1_AMPLITUDE)
        //addPort(osc2.amplitude.also { osc2Amplitude = it }, OSC_2_AMPLITUDE)
        //addPort(osc3.amplitude.also { osc3Amplitude = it }, OSC_3_AMPLITUDE)
        addPort(frequencyPassThrough.input.also { frequency = it }, PORT_NAME_FREQUENCY)
        addPort(amplitudeEnvelope.output.also { mixedOutput = it }, PORT_NAME_OUTPUT)

        amplitudeEnvelope.decay.set(5.0)
        frequency.set(1500.0)
        osc1Amplitude.set(1.0)
        //osc2Amplitude.set(1.0)
        //osc3Amplitude.set(1.0)

        amplitudeEnvelope.export(this, "Amp")
        amplitudeEnvelope.setupAutoDisable(this)
    }

    companion object {
        const val OSC_1_AMPLITUDE = "OSC 1 Amplitude"
        const val OSC_2_AMPLITUDE = "OSC 2 Amplitude"
        const val OSC_3_AMPLITUDE = "OSC 3 Amplitude"
    }

    override fun getOutput(): UnitOutputPort {
        return mixedOutput
    }

    override fun noteOn(frequency: Double, amplitude: Double, timeStamp: TimeStamp) {
        this.frequency.set(frequency, timeStamp)
        this.amplitudeEnvelope.amplitude.set(amplitude, timeStamp)
        amplitudeEnvelope.input.on(timeStamp)
    }

    override fun noteOff(timeStamp: TimeStamp) {
        println("note off ${timeStamp.time}")
        amplitudeEnvelope.input.off(timeStamp)
    }
}