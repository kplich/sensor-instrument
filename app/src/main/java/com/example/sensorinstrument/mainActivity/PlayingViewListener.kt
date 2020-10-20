package com.example.sensorinstrument.mainActivity

import android.view.MotionEvent
import android.view.View

class PlayingViewListener(private val synthesizerWrapper: SynthesizerWrapper): View.OnTouchListener {

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_UP) {
            synthesizerWrapper.noteOff()
        } else if (event.action == MotionEvent.ACTION_DOWN) {
            synthesizerWrapper.noteOn()
        }

        return true
    }
}