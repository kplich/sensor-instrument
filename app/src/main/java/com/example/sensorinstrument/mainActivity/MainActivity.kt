package com.example.sensorinstrument.mainActivity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.example.sensorinstrument.R
import com.example.sensorinstrument.databinding.ActivityMainBinding
import com.example.sensorinstrument.instruments.OscillatorType
import com.example.sensorinstrument.instruments.TripleOscSynthesizer
import com.example.sensorinstrument.sensorListeners.RotationListener
import com.google.android.material.chip.ChipGroup

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var synthesizerWrapper: TripleOscSynthesizer
    private lateinit var sensorManager: SensorManager

    private lateinit var rotationVectorSensor: Sensor
    private lateinit var rotationVectorListener: SensorEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.rootLayout)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        synthesizerWrapper = TripleOscSynthesizer()

        //the whole layout is clickable and used to produce sound
        binding.rootLayout.setOnTouchListener(PlayingViewListener(synthesizerWrapper))

        binding.osc1Type.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.osc_1_type_sine -> synthesizerWrapper.setOsc1Type(OscillatorType.SINE)
                R.id.osc_1_type_saw -> synthesizerWrapper.setOsc1Type(OscillatorType.SAWTOOTH)
                R.id.osc_1_type_square -> synthesizerWrapper.setOsc1Type(OscillatorType.SQUARE)
                else -> println("$checkedId not found")
            }
        }
        binding.osc1Type.check(R.id.osc_1_type_sine)
        binding.osc1Amp.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
               synthesizerWrapper.setOsc1Amplitude(progress.div(seekBar.max.toDouble()))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.osc2Type.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.osc_2_type_sine -> synthesizerWrapper.setOsc2Type(OscillatorType.SINE)
                R.id.osc_2_type_saw -> synthesizerWrapper.setOsc2Type(OscillatorType.SAWTOOTH)
                R.id.osc_2_type_square -> synthesizerWrapper.setOsc2Type(OscillatorType.SQUARE)
                else -> println("$checkedId not found")
            }
        }
        binding.osc2Type.check(R.id.osc_2_type_sine)
        binding.osc2Amp.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizerWrapper.setOsc2Amplitude(progress.div(seekBar.max.toDouble()))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

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
