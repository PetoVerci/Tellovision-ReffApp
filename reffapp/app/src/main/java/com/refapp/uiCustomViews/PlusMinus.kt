package com.refapp.uiCustomViews

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.refapp.R


class PlusMinus(context: Context) : AbstractView(context) {

    override var label = ""
    override var currVal = 0
    private val size = 130

    private val verticalLayout = LinearLayout(context)
    private val linearLayout = LinearLayout(context)
    private val plusko = ImageButton(context)
    private val minusko = ImageButton(context)
    private val currValView = TextView(context)
    private val popis = TextView(context)

    init {
        verticalLayout.orientation = VERTICAL
        verticalLayout.addView(popis)
        linearLayout.addView(minusko)
        linearLayout.addView(currValView)
        linearLayout.addView(plusko)
        verticalLayout.addView(linearLayout)

        addView(verticalLayout)

        linearLayout.gravity = Gravity.CENTER
        popis.gravity = Gravity.CENTER
        this.gravity = Gravity.CENTER
        currValView.gravity = Gravity.CENTER


        minusko.setOnClickListener {
            if (currVal > 0) {
                currVal--
                currValView.text = currVal.toString()
            }
        }
        plusko.setOnClickListener {
            currVal++
            currValView.text = currVal.toString()
        }

    }

    fun setSize() {
        val margins = currValView.layoutParams as LayoutParams
        margins.setMargins(100, 0, 100, 0)
        popis.text = label
        popis.typeface = Typeface.DEFAULT_BOLD
        popis.textSize = 20f

        currValView.textSize = 20f

        //set size of minus ImageButton
        minusko.setImageResource(R.drawable.minus)
        minusko.scaleType = ImageView.ScaleType.FIT_CENTER
        val mparams = minusko.layoutParams
        mparams.height = size
        mparams.width = size
        minusko.layoutParams = mparams

        //set size of plus ImageButton
        plusko.setImageResource(R.drawable.plus)
        plusko.scaleType = ImageView.ScaleType.FIT_CENTER
        val pparams = plusko.layoutParams
        pparams.height = size
        pparams.width = size
        plusko.layoutParams = pparams

        //set size of current points Textview
        currValView.text = "0"
        val currVal = currValView.layoutParams
        currVal.height = size
        currVal.width = size
        currValView.layoutParams = currVal
        this.setPadding(0, 0, 0, 100)
    }
}