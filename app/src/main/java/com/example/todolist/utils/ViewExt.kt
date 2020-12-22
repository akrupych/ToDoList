package com.example.todolist.utils

import android.graphics.Paint
import android.view.View
import android.widget.CheckBox
import android.widget.TextView

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

var TextView.strikeThrough: Boolean
    get() = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG == Paint.STRIKE_THRU_TEXT_FLAG
    set(value) {
        paintFlags =
            if (value) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

fun CheckBox.setOnCheckedChangeListenerSafe(listener: (isChecked: Boolean) -> Unit) {
    setOnCheckedChangeListener { _, isChecked ->
        if (isPressed) listener.invoke(isChecked)
    }
}