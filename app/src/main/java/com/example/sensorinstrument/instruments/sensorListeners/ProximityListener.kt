package com.example.sensorinstrument.instruments.sensorListeners

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.Switch

class ProximityListener(private val switch: Switch): SensorEventListener {
    companion object {
        const val SENSOR_SENSITIVITY = 1.0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_PROXIMITY) {
            if(event.values[0] < SENSOR_SENSITIVITY) {
                switch.isChecked = !switch.isChecked
                switch.callOnClick()
            }
        }
    }
}