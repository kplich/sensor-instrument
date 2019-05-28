package com.example.sensorinstrument

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sensorinstrument.instruments.SineSynthesizer
import com.jsyn.unitgen.Circuit

class MainActivity : AppCompatActivity() {

    private val synth: Circuit = SineSynthesizer()
    private lateinit var sensorManager: SensorManager
    private lateinit var rotationVectorSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        sensorManager.registerListener(RotationListener(windowManager),
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
        //synth.stop()
    }

    override fun onResume() {
        super.onResume()
        //synth.start()
    }
}
