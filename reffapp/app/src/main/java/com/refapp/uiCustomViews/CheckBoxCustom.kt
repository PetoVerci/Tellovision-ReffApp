package com.refapp.uiCustomViews

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView

class CheckBoxCustom(context: Context): AbstractView(context) {
    override var label = ""

    override var currVal = 0

    private val verticalLayout = LinearLayout(context)
    private val linearLayout = LinearLayout(context)
    private val currValView = CheckBox(context)
    private val popis = TextView(context)

    init {
        verticalLayout.orientation = VERTICAL
        verticalLayout.addView(popis)
        linearLayout.addView(currValView)
        verticalLayout.addView(linearLayout)


        addView(verticalLayout)

        linearLayout.gravity = Gravity.CENTER
        popis.gravity = Gravity.CENTER
        this.gravity = Gravity.CENTER
        currValView.gravity = Gravity.CENTER
        verticalLayout.gravity = Gravity.CENTER



        currValView.setOnClickListener {
            currVal = if (currValView.isChecked) 5
            else 0
        }

    }


    fun setSize() {
        popis.text = label
        popis.typeface = Typeface.DEFAULT_BOLD
        popis.textSize = 20f
        currValView.textSize = 20f
        val currVal = currValView.layoutParams
        currValView.layoutParams = currVal
        this.setPadding(0, 0, 0, 100)
    }
}