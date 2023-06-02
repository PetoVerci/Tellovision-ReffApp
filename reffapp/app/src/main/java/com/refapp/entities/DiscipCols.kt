package com.refapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discipcols")
data class DiscipCols(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val discipName: String,
    val colCount: Int


)