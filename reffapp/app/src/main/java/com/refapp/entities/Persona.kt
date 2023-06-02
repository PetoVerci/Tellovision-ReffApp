package com.refapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persony")
data class Persona(
    @PrimaryKey(autoGenerate = true)
    val personId : Int,

    val firstName : String,
    val lastName : String,
    val teamId : Int
)