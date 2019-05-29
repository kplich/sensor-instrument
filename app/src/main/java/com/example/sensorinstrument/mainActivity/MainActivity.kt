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
import com.example.sensorinstrument.R
import com.example.sensorinstrument.instruments.SineSynthesizer
import com.example.sensorinstrument.sensorListeners.InteractionManager
import com.example.sensorinstrument.sensorListeners.ProximityListener
import com.example.sensorinstrument.sensorListeners.RotationListener

class MainActivity: AppCompatActivity() {

    private var synthIsActive = true

    private val synth: SineSynthesizer = SineSynthesizer()
    private lateinit var interactionManager: InteractionManager
    private lateinit var sensorManager: SensorManager

    private lateinit var rotationVectorSensor: Sensor
    private lateinit var rotationVectorListener: SensorEventListener

    private lateinit var proximitySensor: Sensor
    private lateinit var proximitySensorListener: SensorEventListener

    private lateinit var noteSpinner: Spinner
    private lateinit var noteAdapter: NoteSpinnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        interactionManager = InteractionManager(
            synth.getOscillatorFrequencyPort(),
            synth.getFilterFrequencyPort(),
            findViewById<View>(R.id.mainLayout)
        )

        initializeSensors()
        registerSensors()

        //the whole layout is clickable and used to produce sound
        findViewById<View>(R.id.mainLayout).setOnTouchListener(PlayingViewListener(synth))

        //the switch is used to turn the synthesizer on and off
        findViewById<Switch>(R.id.synthActive)!!.setOnClickListener {
            if(synthIsActive) {
                synth.stop()
            }
            else {
                synth.start()
            }
            synthIsActive = !synthIsActive
        }

        configureNoteSpinner()
    }

    override fun onPause() {
        super.onPause()
        synth.stop()

        unregisterSensors()
    }

    override fun onResume() {
        super.onResume()
        synth.start()

        registerSensors()
    }

    private fun initializeSensors() {
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        rotationVectorListener = RotationListener(
            windowManager,
            interactionManager
        )

        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        proximitySensorListener =
            ProximityListener(findViewById(R.id.synthActive))
    }

    private fun registerSensors() {
        sensorManager.registerListener(
            rotationVectorListener,
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_FASTEST)

        sensorManager.registerListener(
            proximitySensorListener,
            proximitySensor,
            SensorManager.SENSOR_DELAY_UI)
    }

    private fun configureNoteSpinner() {
        noteSpinner = findViewById(R.id.noteSpinner)
        noteAdapter = NoteSpinnerAdapter(this, Note.values())
        noteSpinner.adapter = noteAdapter
        noteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val note: Note = parent?.getItemAtPosition(position) as Note
                interactionManager.setMiddleNote(note)
            }

        }
    }

    private fun unregisterSensors() {
        sensorManager.unregisterListener(rotationVectorListener)
        sensorManager.unregisterListener(proximitySensorListener)
    }

}
