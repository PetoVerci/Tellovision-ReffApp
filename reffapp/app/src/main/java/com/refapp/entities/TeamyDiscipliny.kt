package com.refapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teamydiscipliny")
data class TeamyDiscipliny(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val teamName : String,
    val disciplinaName : String
)
