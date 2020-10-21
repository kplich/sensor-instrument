package com.example.sensorinstrument.instruments

import com.jsyn.JSyn
import com.jsyn.unitgen.EnvelopeDAHDSR
import com.jsyn.unitgen.LineOut
import com.jsyn.unitgen.MorphingOscillatorBL

class TripleOscSynthesizer {

    private val synth = JSyn.createSynthesizer(JSynAndroidAudioDevice())
    private val osc1 = MorphingOscillatorBL()
    private val ampEnv1 = EnvelopeDAHDSR()

    private val osc2 = MorphingOscillatorBL()
    private val ampEnv2 = EnvelopeDAHDSR()

    private var lineOut: LineOut

    init {
        synth.add(osc1)
        synth.add(ampEnv1)
        synth.add(osc2)
        synth.add(ampEnv2)
        synth.add(LineOut().also { lineOut = it })

        lineOut.input.disconnectAll(0)
        lineOut.input.disconnectAll(1)

        osc1.output.connect(ampEnv1.amplitude)
        osc2.output.connect(ampEnv2.amplitude)

        ampEnv1.output.connect(0, lineOut.input, 0)
        ampEnv1.output.connect(0, lineOut.input, 1)
        ampEnv2.output.connect(0, lineOut.input, 0)
        ampEnv2.output.connect(0, lineOut.input, 1)

        ampEnv1.attack.set(0.0)
        ampEnv1.decay.set(0.0)
        ampEnv2.attack.set(0.0)
        ampEnv2.decay.set(0.0)
    }

    fun startSynth() {
        synth.start()
        lineOut.start()
    }

    fun stopSynth() {
        lineOut.stop()
    }

    fun noteOn() {
        ampEnv1.input.on()
        ampEnv2.input.on()
    }

    fun noteOff() {
        ampEnv1.input.off()
        ampEnv2.input.off()
    }

    fun setOsc1Amplitude(amplitude: Double) {
        ampEnv1.sustain.set(amplitude)
    }

    fun setOsc1Type(type: OscillatorType) {
        osc1.shape.set(type.shapeForMorhingOsc)
    }

    fun setOsc2Amplitude(amplitude: Double) {
        ampEnv2.sustain.set(amplitude)
    }

    fun setOsc2Type(type: OscillatorType) {
        osc2.shape.set(type.shapeForMorhingOsc)
    }

    fun setOsc3Amplitude(amplitude: Double) {

    }

    fun setOsc3Type(type: OscillatorType) {

    }
}