package com.refapp.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class DynamicHodnotenieViewModel : ViewModel() {

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var currTeamName = ""
    var currDisciplinaName = ""



}