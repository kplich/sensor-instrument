package com.example.sensorinstrument.mainActivity

import android.view.MotionEvent
import android.view.View

class PlayingViewListener(private val synthesizerManager: SynthesizerManager): View.OnTouchListener {

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(synthesizerManager.isEnabled()) {
            if (event!!.action == MotionEvent.ACTION_UP) {
                synthesizerManager.stopPlaying()
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                synthesizerManager.startPlaying()
            }
        }

        return true
    }
}