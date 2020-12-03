package com.example.sensorinstrument.instruments

import com.jsyn.JSyn
import com.jsyn.ports.UnitOutputPort
import com.jsyn.unitgen.*
import kotlin.math.pow

class TripleOscSynthesizer {

    private val synth = JSyn.createSynthesizer(JSynAndroidAudioDevice())

    var osc1Octave = 0
        set(value) {
            field = value.coerceIn(-1, 1)
        }
    private val osc1 = MorphingOscillatorBL()
    private val mul1 = Multiply()
    private val env1 = EnvelopeDAHDSR()

    var osc2Octave = 0
        set(value) {
            field = value.coerceIn(-1, 1)
        }
    private val osc2 = MorphingOscillatorBL()
    private val mul2 = Multiply()
    private val env2 = EnvelopeDAHDSR()

    var osc3Octave = 0
        set(value) {
            field = value.coerceIn(-1, 1)
        }
    private val osc3 = MorphingOscillatorBL()
    private val mul3 = Multiply()
    private val env3 = EnvelopeDAHDSR()

    private val filter = FilterLowPass()

    private var lineOut: LineOut

    init {
        synth.add(osc1)
        synth.add(mul1)
        synth.add(env1)
        synth.add(osc2)
        synth.add(mul2)
        synth.add(env2)
        synth.add(osc3)
        synth.add(mul3)
        synth.add(env3)
        synth.add(filter)
        synth.add(LineOut().also { lineOut = it })

        lineOut.input.disconnectAll(0)
        lineOut.input.disconnectAll(1)

        osc1.output.connect(mul1.inputA)
        osc2.output.connect(mul2.inputA)
        osc3.output.connect(mul3.inputA)

        mul1.output.connect(env1.amplitude)
        mul2.output.connect(env2.amplitude)
        mul3.output.connect(env3.amplitude)

        env1.output.connect(filter.input)
        env2.output.connect(filter.input)
        env3.output.connect(filter.input)

        filter.output.connect(0, lineOut.input, 0)
        filter.output.connect(0, lineOut.input, 1)

        env1.amplitude.set(0.5)
        env1.delay.set(0.0)
        env1.attack.set(2.5)
        env1.hold.set(2.5)
        env1.decay.set(2.5)
        env1.sustain.set(0.5)
        env1.release.set(2.5)
        env2.amplitude.set(0.5)
        env2.delay.set(0.0)
        env2.attack.set(2.5)
        env2.hold.set(2.5)
        env2.decay.set(2.5)
        env2.sustain.set(0.5)
        env2.release.set(2.5)
        env3.amplitude.set(0.5)
        env3.delay.set(0.0)
        env3.attack.set(2.5)
        env3.hold.set(2.5)
        env3.decay.set(2.5)
        env3.sustain.set(0.5)
        env3.release.set(2.5)
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
        env1.input.on()
        env2.input.on()
        env3.input.on()
    }

    fun noteOff() {
        env1.input.off()
        env2.input.off()
        env3.input.off()
    }

    fun setOsc1Amplitude(amplitude: Double) {
        mul1.inputB.set(amplitude.coerceIn(0.0, 1.0))
    }

    fun setOsc1Delay(delay: Double) {
        env1.delay.set(delay)
    }

    fun setOsc1Attack(attack: Double) {
        env1.attack.set(attack)
    }

    fun setOsc1Hold(hold: Double) {
        env1.hold.set(hold)
    }

    fun setOsc1Decay(decay: Double) {
        env1.decay.set(decay)
    }

    fun setOsc1Sustain(sustain: Double) {
        env1.sustain.set(sustain)
    }

    fun setOsc1Release(release: Double) {
        env1.release.set(release)
    }

    fun setOsc1Type(type: OscillatorType) {
        osc1.shape.set(type.shapeForMorhingOsc)
    }

    fun setOsc2Amplitude(amplitude: Double) {
        mul2.inputB.set(amplitude.coerceIn(0.0, 1.0))
    }

    fun setOsc2Delay(delay: Double) {
        env2.delay.set(delay)
    }

    fun setOsc2Attack(attack: Double) {
        env2.attack.set(attack)
    }

    fun setOsc2Hold(hold: Double) {
        env2.hold.set(hold)
    }

    fun setOsc2Decay(decay: Double) {
        env2.decay.set(decay)
    }

    fun setOsc2Sustain(sustain: Double) {
        env2.sustain.set(sustain)
    }

    fun setOsc2Release(release: Double) {
        env2.release.set(release)
    }

    fun setOsc2Type(type: OscillatorType) {
        osc2.shape.set(type.shapeForMorhingOsc)
    }

    fun setOsc3Amplitude(amplitude: Double) {
        mul3.inputB.set(amplitude.coerceIn(0.0, 1.0))
    }

    fun setOsc3Delay(delay: Double) {
        env3.delay.set(delay)
    }

    fun setOsc3Attack(attack: Double) {
        env3.attack.set(attack)
    }
    fun setOsc3Hold(hold: Double) {
        env3.hold.set(hold)
    }

    fun setOsc3Decay(decay: Double) {
        env3.decay.set(decay)
    }

    fun setOsc3Sustain(sustain: Double) {
        env3.sustain.set(sustain)
    }

    fun setOsc3Release(release: Double) {
        env3.release.set(release)
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