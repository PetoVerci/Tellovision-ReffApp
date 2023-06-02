package com.refapp.uiCustomViews

import android.content.Context
import android.widget.LinearLayout

abstract class AbstractView (context: Context) : LinearLayout(context){
    open var label = ""
    open var currVal = 0
}