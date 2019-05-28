package com.example.sensorinstrument

import android.view.MotionEvent
import android.view.View
import com.example.sensorinstrument.instruments.SineSynthesizer

class PlayingButtonListener(private val synth: SineSynthesizer): View.OnTouchListener {

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_UP) {
            synth.stopPlaying()
        } else if (event.action == MotionEvent.ACTION_DOWN) {
            synth.startPlaying()
        }

        return true
    }
}