package com.example.sensorinstrument.mainActivity

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.sensorinstrument.R

class NoteSpinnerAdapter(applicationContext: Context,
                         notes: Array<Note>): ArrayAdapter<Note>(applicationContext, 0, notes) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val newView: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.note_spinner_row, parent, false)

        val note = getItem(position)!!
        newView.findViewById<TextView>(R.id.noteName).apply {
            text = note.noteName
            background = ColorDrawable(note.color)
        }

        return newView
    }
}