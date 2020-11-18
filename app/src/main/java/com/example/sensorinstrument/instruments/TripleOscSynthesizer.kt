package com.example.sensorinstrument.instruments

import com.jsyn.JSyn
import com.jsyn.unitgen.EnvelopeDAHDSR
import com.jsyn.unitgen.FilterLowPass
import com.jsyn.unitgen.LineOut
import com.jsyn.unitgen.MorphingOscillatorBL
import kotlin.math.pow

class TripleOscSynthesizer {

    private val synth = JSyn.createSynthesizer(JSynAndroidAudioDevice())

    var osc1Octave = 0
        set(value) {
            field = when {
                value >= 1 -> 1
                value == 0 -> 0
                else -> -1
            }
        }
    private val osc1 = MorphingOscillatorBL()
    private val ampEnv1 = EnvelopeDAHDSR()

    var osc2Octave = 0
        set(value) {
            field = when {
                value >= 1 -> 1
                value == 0 -> 0
                else -> -1
            }
        }
    private val osc2 = MorphingOscillatorBL()
    private val ampEnv2 = EnvelopeDAHDSR()

    var osc3Octave = 0
        set(value) {
            field = when {
                value >= 1 -> 1
                value == 0 -> 0
                else -> -1
            }
        }
    private val osc3 = MorphingOscillatorBL()
    private val ampEnv3 = EnvelopeDAHDSR()

    private val filter = FilterLowPass()

    private var lineOut: LineOut

    init {
        synth.add(osc1)
        synth.add(ampEnv1)
        synth.add(osc2)
        synth.add(ampEnv2)
        synth.add(osc3)
        synth.add(ampEnv3)
        synth.add(filter)
        synth.add(LineOut().also { lineOut = it })

        lineOut.input.disconnectAll(0)
        lineOut.input.disconnectAll(1)

        osc1.output.connect(ampEnv1.amplitude)
        osc2.output.connect(ampEnv2.amplitude)
        osc3.output.connect(ampEnv3.amplitude)

        ampEnv1.output.connect(filter.input)
        ampEnv2.output.connect(filter.input)
        ampEnv3.output.connect(filter.input)

        filter.output.connect(0, lineOut.input, 0)
        filter.output.connect(0, lineOut.input, 1)

        ampEnv1.attack.set(0.0)
        ampEnv1.decay.set(0.0)
        ampEnv2.attack.set(0.0)
        ampEnv2.decay.set(0.0)
        ampEnv3.attack.set(0.0)
        ampEnv3.decay.set(0.0)
        filter.frequency.setup(500.0, 20000.0, 20000.0)
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
        ampEnv3.input.on()
    }

    fun noteOff() {
        ampEnv1.input.off()
        ampEnv2.input.off()
        ampEnv3.input.off()
    }

    fun setOsc1Amplitude(amplitude: Double) {
        ampEnv1.sustain.set(amplitude)
    }

    fun setOsc1Attack(attack: Double) {
        ampEnv1.attack.set(attack)
    }

    fun setOsc1Decay(decay: Double) {
        ampEnv1.decay.set(decay)
    }

    fun setOsc1Sustain(sustain: Double) {
        ampEnv1.sustain.set(sustain)
    }

    fun setOsc1Release(release: Double) {
        ampEnv1.release.set(release)
    }

    fun setOsc1Type(type: OscillatorType) {
        osc1.shape.set(type.shapeForMorhingOsc)
    }

    fun setOsc2Amplitude(amplitude: Double) {
        ampEnv2.sustain.set(amplitude)
    }

    fun setOsc2Attack(attack: Double) {
        ampEnv2.attack.set(attack)
    }

    fun setOsc2Decay(decay: Double) {
        ampEnv2.decay.set(decay)
    }

    fun setOsc2Sustain(sustain: Double) {
        ampEnv2.sustain.set(sustain)
    }

    fun setOsc2Release(release: Double) {
        ampEnv2.release.set(release)
    }

    fun setOsc2Type(type: OscillatorType) {
        osc2.shape.set(type.shapeForMorhingOsc)
    }

    fun setOsc3Amplitude(amplitude: Double) {
        ampEnv3.sustain.set(amplitude)
    }

    fun setOsc3Attack(attack: Double) {
        ampEnv3.attack.set(attack)
    }

    fun setOsc3Decay(decay: Double) {
        ampEnv3.decay.set(decay)
    }

    fun setOsc3Sustain(sustain: Double) {
        ampEnv3.sustain.set(sustain)
    }

    fun setOsc3Release(release: Double) {
        ampEnv3.release.set(release)
    }

    fun setOsc3Type(type: OscillatorType) {
        osc3.shape.set(type.shapeForMorhingOsc)
    }

    fun setFrequency(frequency: Double) {
        osc1.frequency.set(frequency * 2.0.pow(osc1Octave))
        osc2.frequency.set(frequency * 2.0.pow(osc2Octave))
        osc3.frequency.set(frequency * 2.0.pow(osc3Octave))
    }

    fun setFilterCutoff(frequency: Double) {
        filter.frequency.set(frequency)
    }

    fun setFilterQ(q: Double) {
        filter.Q.set(q)
    }
}