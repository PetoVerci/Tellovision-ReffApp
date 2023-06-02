package com.refapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bloky")
data class Blok(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val label : String,
    val type : String
)
