package com.example.sensorinstrument

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sensorinstrument.instruments.SineSynthesizer
import com.jsyn.unitgen.Circuit

class MainActivity : AppCompatActivity() {

    private val synth: Circuit = SineSynthesizer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onPause() {
        super.onPause()
        synth.stop()
    }

    override fun onResume() {
        super.onResume()
        synth.start()
    }
}
