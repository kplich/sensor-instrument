package com.example.sensorinstrument.mainActivity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import com.example.sensorinstrument.R
import com.example.sensorinstrument.instruments.SineSynthesizer
import com.example.sensorinstrument.sensorListeners.ProximityListener
import com.example.sensorinstrument.sensorListeners.RotationListener
import com.jsyn.unitgen.Circuit

class MainActivity: AppCompatActivity() {

    private val synth: Circuit = SineSynthesizer()
    private lateinit var synthesizerManager: SynthesizerManager
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
        synthesizerManager = SynthesizerManager(
            synth, findViewById<View>(R.id.mainLayout)
        )

        //the whole layout is clickable and used to produce sound
        findViewById<View>(R.id.mainLayout).setOnTouchListener(PlayingViewListener(synthesizerManager))

        //the switch is used to turn the synthesizer on and off
        findViewById<Switch>(R.id.synthActive)!!.setOnClickListener {
            synthesizerManager.switchSynthState()
            findViewById<TextView>(R.id.synthOffDescription).visibility =
                if(synthesizerManager.isEnabled()) View.INVISIBLE else View.VISIBLE
        }

        configureNoteSpinner()

        initializeSensors()
        registerSensors()
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
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val note: Note = parent?.getItemAtPosition(position) as Note
                synthesizerManager.setMiddleNote(note)
            }

        }
    }

    private fun unregisterSensors() {
        sensorManager.unregisterListener(rotationVectorListener)
        sensorManager.unregisterListener(proximitySensorListener)
    }

}
