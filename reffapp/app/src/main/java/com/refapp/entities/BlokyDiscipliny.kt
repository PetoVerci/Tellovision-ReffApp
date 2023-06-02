package com.refapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blokydiscipliny")
data class BlokyDiscipliny(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val blokName : String,
    val blokType : String,
    val disciplinaName : String
)
