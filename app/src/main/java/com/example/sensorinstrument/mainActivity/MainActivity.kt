package com.example.sensorinstrument.mainActivity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sensorinstrument.R
import com.example.sensorinstrument.databinding.ActivityMainBinding
import com.example.sensorinstrument.sensorListeners.RotationListener

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var synthesizerWrapper: SynthesizerWrapper
    private lateinit var sensorManager: SensorManager

    private lateinit var rotationVectorSensor: Sensor
    private lateinit var rotationVectorListener: SensorEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.rootLayout)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        synthesizerWrapper = SynthesizerWrapper(Note.E5)

        //the whole layout is clickable and used to produce sound
        binding.rootLayout.setOnTouchListener(PlayingViewListener(synthesizerWrapper))

        binding.osc1Type.check(R.id.osc_1_type_sine)

        initializeSensors()
        registerSensors()

        synthesizerWrapper.startSynth()
    }

    override fun onPause() {
        super.onPause()
        synthesizerWrapper.stopSynth()
        unregisterSensors()
    }

    override fun onResume() {
        super.onResume()
        registerSensors()
        synthesizerWrapper.startSynth()
    }

    private fun initializeSensors() {
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        rotationVectorListener = RotationListener(
            windowManager,
            binding.rootLayout
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
