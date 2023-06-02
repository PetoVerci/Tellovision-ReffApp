package com.refapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teamy")
data class Team(
    @PrimaryKey(autoGenerate = true)
    val teamID : Int? = null,
    val name : String,

    )
