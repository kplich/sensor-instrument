package com.example.sensorinstrument.instruments

import com.jsyn.unitgen.SawtoothOscillator
import com.jsyn.unitgen.SineOscillator
import com.jsyn.unitgen.SquareOscillator
import com.jsyn.unitgen.UnitOscillator

enum class OscillatorType() {
    SINE {
        override fun getOscillator(): UnitOscillator {
            return SineOscillator()
        }
    },
    SQUARE {
        override fun getOscillator(): UnitOscillator {
            return SquareOscillator()
        }
    },
    SAW {
        override fun getOscillator(): UnitOscillator {
            return SawtoothOscillator()
        }
    };

    abstract fun getOscillator(): UnitOscillator
}