package com.example.sensorinstrument.mainActivity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import com.example.sensorinstrument.R
import com.example.sensorinstrument.instruments.SineSynthesizer
import com.example.sensorinstrument.sensorListeners.ProximityListener
import com.example.sensorinstrument.sensorListeners.RotationListener

class MainActivity: AppCompatActivity() {

    private var active = true

    private val synth: SineSynthesizer = SineSynthesizer()
    private lateinit var sensorManager: SensorManager

    private lateinit var rotationVectorSensor: Sensor
    private lateinit var rotationVectorListener: SensorEventListener

    private lateinit var proximitySensor: Sensor
    private lateinit var proximitySensorListener: SensorEventListener

    private lateinit var noteSpinner: Spinner
    private lateinit var noteAdapter: NoteSpinnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        rotationVectorListener = RotationListener(
            windowManager,
            synth.getOscillatorFrequencyPort(),
            synth.getFilterFrequencyPort()
        )
        sensorManager.registerListener(
            rotationVectorListener,
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_FASTEST)

        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        proximitySensorListener =
            ProximityListener(findViewById(R.id.synthActive))
        sensorManager.registerListener(
            proximitySensorListener,
            proximitySensor,
            SensorManager.SENSOR_DELAY_UI)

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

        noteSpinner = findViewById(R.id.noteSpinner)
        noteAdapter = NoteSpinnerAdapter(this, Note.values())
        noteSpinner.adapter = noteAdapter
        noteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val note: Note = parent?.getItemAtPosition(position) as Note
                (rotationVectorListener as RotationListener).setMiddleNote(note)
            }

        }
    }

    override fun onPause() {
        super.onPause()
        synth.stop()

        sensorManager.unregisterListener(rotationVectorListener)
        sensorManager.unregisterListener(proximitySensorListener)
    }

    override fun onResume() {
        super.onResume()
        synth.start()
    }
}
