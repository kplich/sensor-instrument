package com.example.sensorinstrument.mainActivity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import com.example.sensorinstrument.R
import com.example.sensorinstrument.sensorListeners.ProximityListener
import com.example.sensorinstrument.sensorListeners.RotationListener

class MainActivity: AppCompatActivity() {
    private lateinit var synthesizerManager: SynthesizerManager
    private lateinit var sensorManager: SensorManager

    private lateinit var rotationVectorSensor: Sensor
    private lateinit var rotationVectorListener: SensorEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        synthesizerManager = SynthesizerManager(findViewById<View>(R.id.mainLayout), Note.E5)

        //the whole layout is clickable and used to produce sound
        findViewById<View>(R.id.mainLayout).setOnTouchListener(PlayingViewListener(synthesizerManager))

        initializeSensors()
        registerSensors()

        synthesizerManager.startSynth()
    }

    override fun onPause() {
        super.onPause()
        synthesizerManager.stopSynth()
        unregisterSensors()
    }

    override fun onResume() {
        super.onResume()
        registerSensors()
        synthesizerManager.startSynth()
    }

    private fun initializeSensors() {
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        rotationVectorListener = RotationListener(
            windowManager,
            synthesizerManager
        )
    }

    private fun registerSensors() {
        sensorManager.registerListener(
            rotationVectorListener,
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_FASTEST)
    }

    private fun unregisterSensors() {
        sensorManager.unregisterListener(rotationVectorListener)
    }

}
