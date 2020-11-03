package com.example.sensorinstrument.mainActivity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sensorinstrument.R
import com.example.sensorinstrument.databinding.ActivityMainBinding
import com.example.sensorinstrument.instruments.OscillatorType
import com.example.sensorinstrument.instruments.TripleOscSynthesizer
import com.example.sensorinstrument.sensorListeners.RotationListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var synthesizer: TripleOscSynthesizer
    private lateinit var sensorManager: SensorManager

    private lateinit var rotationVectorSensor: Sensor
    private lateinit var rotationVectorListener: SensorEventListener
    private var seconds = 5.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.rootLayout)
        val OscLayout_1: ConstraintLayout = findViewById(R.id.osc_1)
        val OscLayout_2: ConstraintLayout = findViewById(R.id.osc_2)
        val OscLayout_3: ConstraintLayout = findViewById(R.id.osc_3)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        synthesizer = TripleOscSynthesizer()

        //the whole layout is clickable and used to produce sound
        binding.rootLayout.setOnTouchListener(PlayingViewListener(synthesizer))

        setUpOsc1()
        setUpOsc2()
        setUpOsc3()

        setOscLayoutsHeight(OscLayout_1, OscLayout_2, OscLayout_3)
        initializeSensors()
        registerSensors()


        synthesizer.startSynth()
    }

    private fun setOscLayoutsHeight(
        OscLayout_1: ConstraintLayout,
        OscLayout_2: ConstraintLayout,
        OscLayout_3: ConstraintLayout
    ) {
        binding.osc1Params.setOnCheckedChangeListener { _, isChecked ->

            val params: ViewGroup.LayoutParams = OscLayout_1.layoutParams
            if (isChecked) {
                params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            } else {
                val scale: Float = resources.displayMetrics.density
                val pixels = (48 * scale + 0.5f).toInt()
                params.height = pixels
            }
            OscLayout_1.layoutParams = params
        }

        binding.osc2Params.setOnCheckedChangeListener { _, isChecked ->

            val params: ViewGroup.LayoutParams = OscLayout_2.layoutParams
            if (isChecked) {
                params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            } else {
                val scale: Float = resources.displayMetrics.density
                val pixels = (48 * scale + 0.5f).toInt()
                params.height = pixels
            }
            OscLayout_2.layoutParams = params
        }

        binding.osc3Params.setOnCheckedChangeListener { _, isChecked ->

            val params: ViewGroup.LayoutParams = OscLayout_3.layoutParams
            if (isChecked) {
                params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            } else {
                val scale: Float = resources.displayMetrics.density
                val pixels = (48 * scale + 0.5f).toInt()
                params.height = pixels
            }
            OscLayout_3.layoutParams = params
        }


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

        binding.osc1Attack.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc1Attack(progress.div(seekBar.max.toDouble()).times(seconds))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.osc1Decay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc1Decay(progress.div(seekBar.max.toDouble()).times(seconds))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.osc1Sustain.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc1Sustain(progress.div(seekBar.max.toDouble()))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.osc1Release.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc1Release(progress.div(seekBar.max.toDouble()).times(seconds))
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

        binding.osc2Attack.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc2Attack(progress.div(seekBar.max.toDouble()).times(seconds))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.osc2Decay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc2Decay(progress.div(seekBar.max.toDouble()).times(seconds))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.osc2Sustain.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc2Sustain(progress.div(seekBar.max.toDouble()))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.osc2Release.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc2Release(progress.div(seekBar.max.toDouble()).times(seconds))
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

        binding.osc3Attack.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc3Attack(progress.div(seekBar.max.toDouble()).times(seconds))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.osc3Decay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc3Decay(progress.div(seekBar.max.toDouble()).times(seconds))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.osc3Sustain.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc3Sustain(progress.div(seekBar.max.toDouble()))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.osc3Release.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                synthesizer.setOsc3Release(progress.div(seekBar.max.toDouble()).times(seconds))
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
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    private fun unregisterSensors() {
        sensorManager.unregisterListener(rotationVectorListener)
    }

}
