package com.refapp.uiCustomViews

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.refapp.R

class ThreeOption(context: Context) : AbstractView(context) {
    override var label = ""
    override var currVal = 0

    val ratingMap = mapOf(0 to 10, 1 to 5, 2 to 0)


    val size = 130

    val options = mutableListOf("Dobre", "Obstojne", "Zle")
    var indexInOptions = 0


    val verticalLayout = LinearLayout(context)
    val linearLayout = LinearLayout(context)
    val rightArrow = ImageButton(context)
    val leftArrow = ImageButton(context)
    val currValView = TextView(context)
    val popis = TextView(context)

    init {
        verticalLayout.orientation = VERTICAL
        verticalLayout.addView(popis)
        linearLayout.addView(leftArrow)
        linearLayout.addView(currValView)
        linearLayout.addView(rightArrow)
        verticalLayout.addView(linearLayout)

        addView(verticalLayout)
        currVal = ratingMap[indexInOptions]!!




        leftArrow.setOnClickListener {
            if (indexInOptions > 0) {
                indexInOptions--
                currValView.text = options[indexInOptions]
                currVal = ratingMap[indexInOptions]!!
            }
            else {
                indexInOptions = 2
                currValView.text = options[indexInOptions]
                currVal = ratingMap[indexInOptions]!!
            }
        }
        rightArrow.setOnClickListener {
            if (indexInOptions < 2) {
                indexInOptions++
                currValView.text = options[indexInOptions]
                currVal = ratingMap[indexInOptions]!!
            }
            else {
                indexInOptions = 0
                currValView.text = options[indexInOptions]
                currVal = ratingMap[indexInOptions]!!
            }
        }

    }

    fun setSize() {
        linearLayout.gravity = Gravity.CENTER
        popis.gravity = Gravity.CENTER
        this.gravity = Gravity.CENTER
        currValView.gravity = Gravity.CENTER


        popis.text = label
        popis.typeface = Typeface.DEFAULT_BOLD
        popis.textSize = 20f

        currValView.textSize = 16f



        val margins = currValView.layoutParams as LayoutParams
        margins.setMargins(20, 0, 20, 0)

        //set size of left Arrow ImageButton
        leftArrow.setImageResource(R.drawable.left)
        leftArrow.scaleType = ImageView.ScaleType.FIT_CENTER
        val mparams = leftArrow.layoutParams
        mparams.height = size
        mparams.width = size
        leftArrow.layoutParams = mparams

        //set size of right Arrow ImageButton
        rightArrow.setImageResource(R.drawable.right)
        rightArrow.scaleType = ImageView.ScaleType.FIT_CENTER
        val pparams = rightArrow.layoutParams
        pparams.height = size
        pparams.width = size
        rightArrow.layoutParams = pparams

        //set size of current points Textview
        currValView.text = options[indexInOptions]
        val currVal = currValView.layoutParams
        currVal.height = size
        currVal.width = size + 75
        currValView.layoutParams = currVal
        this.setPadding(0, 0, 0, 100)
    }
}