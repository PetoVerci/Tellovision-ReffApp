package com.refapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discipliny")
data class Disciplina(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val name : String




)
