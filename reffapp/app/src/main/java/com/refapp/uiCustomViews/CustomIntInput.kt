package com.refapp.uiCustomViews

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class CustomIntInput(context: Context) : AbstractView(context) {
    override var label = ""
    override var currVal = 0
    private val size = 130
    private val verticalLayout = LinearLayout(context)
    private val linearLayout = LinearLayout(context)
    private val currValView = EditText(context)
    private val popis = TextView(context)

    init {
        verticalLayout.orientation = VERTICAL
        verticalLayout.addView(popis)
        linearLayout.addView(currValView)
        verticalLayout.addView(linearLayout)


        currValView.inputType = InputType.TYPE_CLASS_NUMBER
        addView(verticalLayout)

        linearLayout.gravity = Gravity.CENTER
        popis.gravity = Gravity.CENTER
        this.gravity = Gravity.CENTER
        currValView.gravity = Gravity.CENTER

        currValView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used in this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the variable with the entered text
                currVal = Integer.parseInt(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used in this example
            }
        })

    }


        fun setSize() {
            val margins = currValView.layoutParams as LinearLayout.LayoutParams
            margins.setMargins(100, 0, 100, 0)
            popis.text = label
            popis.typeface = Typeface.DEFAULT_BOLD
            popis.textSize = 20f

            currValView.textSize = 20f


            //set size of current points Textview
            val currVal = currValView.layoutParams
            currVal.height = size
            currVal.width = size
            currValView.layoutParams = currVal
            this.setPadding(0, 0, 0, 100)
        }


}