package com.refapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dynamichodnotenie")
data class DynamicHodnotenie(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val blockLabel : String,
    val blockScore : Int,
    val teamName : String,
    val discName : String
)
