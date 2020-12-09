package com.example.sensorinstrument.mainActivity

import android.view.MotionEvent
import android.view.View
import com.example.sensorinstrument.instruments.TripleOscSynthesizer
import com.example.sensorinstrument.transformValueBetweenRanges

class  PlayingViewListener(private val synthesizerWrapper: TripleOscSynthesizer): View.OnTouchListener {

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_UP) {
            synthesizerWrapper.noteOff()
        } else if (event.action == MotionEvent.ACTION_DOWN) {
            synthesizerWrapper.noteOn()
        }

        synthesizerWrapper.setFilterCutoff(transformValueBetweenRanges(event.y.toDouble(), 600.0, 1300.0, 5000.0, 150.0))
        synthesizerWrapper.setLfo(transformValueBetweenRanges(event.x.toDouble(), 450.0, 700.0, 0.0, 0.02))
        return true
    }
}