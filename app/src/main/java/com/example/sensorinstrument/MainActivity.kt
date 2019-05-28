package com.example.sensorinstrument

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import com.example.sensorinstrument.instruments.SineSynthesizer

class MainActivity : AppCompatActivity() {

    private var active = true
    private val synth: SineSynthesizer = SineSynthesizer()
    private lateinit var sensorManager: SensorManager
    private lateinit var rotationVectorSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        sensorManager.registerListener(RotationListener(windowManager, synth.getOscillatorFrequencyPort()),
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_FASTEST)

        findViewById<Button>(R.id.playingButton)!!.setOnTouchListener(PlayingButtonListener(synth))

        findViewById<Switch>(R.id.synthActive)!!.setOnClickListener { view: View? ->
            if(active) {
                synth.stop()
            }
            else {
                synth.start()
            }
            active = !active
        }
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
