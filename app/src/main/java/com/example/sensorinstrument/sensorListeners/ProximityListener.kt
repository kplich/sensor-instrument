package com.example.sensorinstrument.sensorListeners

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.Switch

class ProximityListener(private val switch: Switch): SensorEventListener {
    companion object {
        const val SENSOR_SENSITIVITY = 1.0
    }

    private var wasClose: Boolean = false

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_PROXIMITY) {
            if(event.values[0] < SENSOR_SENSITIVITY && !wasClose) {
                wasClose = true
                switch.isChecked = !switch.isChecked
                switch.callOnClick()
            }
            else {
                wasClose = false
            }
        }
    }
}