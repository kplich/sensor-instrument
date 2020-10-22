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

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var synthesizer: TripleOscSynthesizer
    private lateinit var sensorManager: SensorManager

    private lateinit var rotationVectorSensor: Sensor
    private lateinit var rotationVectorListener: SensorEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.rootLayout)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        synthesizer = TripleOscSynthesizer()

        //the whole layout is clickable and used to produce sound
        binding.rootLayout.setOnTouchListener(PlayingViewListener(synthesizer))

        setUpOsc1()
        setUpOsc2()
        setUpOsc3()

        initializeSensors()
        registerSensors()

        synthesizer.startSynth()
    }

    override fun onPause() {
        super.onPause()
        synthesizer.stopSynth()
        unregisterSensors()
    }

    override fun onResume() {
        super.onResume()
        registerSensors()
        synthesizer.startSynth()
    }

    private fun setUpOsc1() {
        binding.osc1Type.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.osc_1_type_sine -> synthesizer.setOsc1Type(OscillatorType.SINE)
                R.id.osc_1_type_saw -> synthesizer.setOsc1Type(OscillatorType.SAWTOOTH)
                R.id.osc_1_type_square -> synthesizer.setOsc1Type(OscillatorType.SQUARE)
                else -> println("$checkedId not found")
            }
        }
        binding.osc1Type.check(R.id.osc_1_type_sine)
        binding.osc1Amp.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc1Amplitude(progress.div(seekBar.max.toDouble()))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    private fun setUpOsc2() {
        binding.osc2Type.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.osc_2_type_sine -> synthesizer.setOsc2Type(OscillatorType.SINE)
                R.id.osc_2_type_saw -> synthesizer.setOsc2Type(OscillatorType.SAWTOOTH)
                R.id.osc_2_type_square -> synthesizer.setOsc2Type(OscillatorType.SQUARE)
                else -> println("$checkedId not found")
            }
        }
        binding.osc2Type.check(R.id.osc_2_type_sine)
        binding.osc2Amp.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc2Amplitude(progress.div(seekBar.max.toDouble()))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    private fun setUpOsc3() {
        binding.osc3Type.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.osc_3_type_sine -> synthesizer.setOsc3Type(OscillatorType.SINE)
                R.id.osc_3_type_saw -> synthesizer.setOsc3Type(OscillatorType.SAWTOOTH)
                R.id.osc_3_type_square -> synthesizer.setOsc3Type(OscillatorType.SQUARE)
                else -> println("$checkedId not found")
            }
        }
        binding.osc3Type.check(R.id.osc_3_type_sine)
        binding.osc3Amp.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc3Amplitude(progress.div(seekBar.max.toDouble()))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    private fun initializeSensors() {
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        rotationVectorListener = RotationListener(
            windowManager,
            binding.rootLayout,
            synthesizer
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
