package com.example.sensorinstrument.mainActivity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sensorinstrument.R
import com.example.sensorinstrument.databinding.ActivityMainBinding
import com.example.sensorinstrument.instruments.OscillatorType
import com.example.sensorinstrument.instruments.TripleOscSynthesizer
import com.example.sensorinstrument.sensorListeners.RotationListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), RotaryKnobView.RotaryKnobListener {
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
        osc_1_amp.listener = this
        osc_2_amp.listener = this
        osc_3_amp.listener = this

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        synthesizer = TripleOscSynthesizer()

        //the whole layout is clickable and used to produce sound
        binding.rootLayout.setOnTouchListener(PlayingViewListener(synthesizer))

        setUpOsc1()
        setUpOsc2()
        setUpOsc3()

        setOscLayoutsHeight(binding.osc1, binding.osc2, binding.osc3)
        initializeSensors()
        registerSensors()


        synthesizer.startSynth()
    }


    override fun onRotate(value: Int) {

        when {
            binding.osc1Chip.isChecked -> {
                amp_1_val.text = value.toString();
                synthesizer.setOsc1Amplitude(value.toDouble() / 100)
            }
            binding.osc2Chip.isChecked -> {
                amp_2_val.text = value.toString();
                synthesizer.setOsc2Amplitude(value.toDouble() / 100)
            }
            binding.osc3Chip.isChecked -> {
                amp_3_val.text = value.toString();
                synthesizer.setOsc3Amplitude(value.toDouble() / 100)
            }
        }
    }

    private fun setOscLayoutsHeight(
        OscLayout1: ConstraintLayout,
        OscLayout2: ConstraintLayout,
        OscLayout3: ConstraintLayout
    ) {
        binding.oscChips.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.osc_1_chip -> {
                    OscLayout2.visibility = View.GONE
                    OscLayout3.visibility = View.GONE
                    OscLayout1.visibility = View.VISIBLE
                }
                R.id.osc_2_chip -> {
                    OscLayout1.visibility = View.GONE
                    OscLayout3.visibility = View.GONE
                    OscLayout2.visibility = View.VISIBLE
                }
                R.id.osc_3_chip -> {
                    OscLayout1.visibility = View.GONE
                    OscLayout2.visibility = View.GONE
                    OscLayout3.visibility = View.VISIBLE
                }
                else -> {
                    OscLayout1.visibility = View.GONE
                    OscLayout2.visibility = View.GONE
                    OscLayout3.visibility = View.GONE
                }
            }
        }

        binding.oscChips.check(R.id.osc_1_chip)
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

        synthesizer.setOsc1Amplitude(0.5)

        binding.osc1Type.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.osc_1_type_sine -> synthesizer.setOsc1Type(OscillatorType.SINE)
                R.id.osc_1_type_saw -> synthesizer.setOsc1Type(OscillatorType.SAWTOOTH)
                R.id.osc_1_type_square -> synthesizer.setOsc1Type(OscillatorType.SQUARE)
                else -> println("$checkedId not found")
            }
        }
        binding.osc1Type.check(R.id.osc_1_type_sine)

        binding.osc1Octave.setOnCheckedChangeListener { _, checkedId ->
            synthesizer.osc1Octave = when (checkedId) {
                R.id.osc_1_octave_minus_1 -> -1
                R.id.osc_1_octave_0 -> 0
                R.id.osc_1_octave_1 -> 1
                else -> 0
            }
        }
        binding.osc1Octave.check(R.id.osc_1_octave_0)


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

        synthesizer.setOsc2Amplitude(0.5)

        binding.osc2Type.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.osc_2_type_sine -> synthesizer.setOsc2Type(OscillatorType.SINE)
                R.id.osc_2_type_saw -> synthesizer.setOsc2Type(OscillatorType.SAWTOOTH)
                R.id.osc_2_type_square -> synthesizer.setOsc2Type(OscillatorType.SQUARE)
                else -> println("$checkedId not found")
            }
        }
        binding.osc2Type.check(R.id.osc_2_type_sine)

        binding.osc2Octave.setOnCheckedChangeListener { _, checkedId ->
            synthesizer.osc2Octave = when (checkedId) {
                R.id.osc_2_octave_minus_1 -> -1
                R.id.osc_2_octave_0 -> 0
                R.id.osc_2_octave_1 -> 1
                else -> 0
            }
        }
        binding.osc2Octave.check(R.id.osc_2_octave_0)


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

        synthesizer.setOsc3Amplitude(0.5)

        binding.osc3Type.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.osc_3_type_sine -> synthesizer.setOsc3Type(OscillatorType.SINE)
                R.id.osc_3_type_saw -> synthesizer.setOsc3Type(OscillatorType.SAWTOOTH)
                R.id.osc_3_type_square -> synthesizer.setOsc3Type(OscillatorType.SQUARE)
                else -> println("$checkedId not found")
            }
        }
        binding.osc3Type.check(R.id.osc_3_type_sine)

        binding.osc3Octave.setOnCheckedChangeListener { _, checkedId ->
            synthesizer.osc3Octave = when (checkedId) {
                R.id.osc_3_octave_minus_1 -> -1
                R.id.osc_3_octave_0 -> 0
                R.id.osc_3_octave_1 -> 1
                else -> 0
            }
        }
        binding.osc3Octave.check(R.id.osc_3_octave_0)

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
