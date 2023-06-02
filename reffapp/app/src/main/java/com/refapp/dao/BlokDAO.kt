package com.refapp.dao

import androidx.room.*
import com.refapp.entities.Blok
import com.refapp.entities.Disciplina

@Dao
interface BlokDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlok(blok: Blok)

    @Delete
    suspend fun delete(blok: Blok)

    @Query("SELECT * FROM bloky")
    fun getAllBloky() : List<Blok>

    @Query("Delete from bloky")
    fun deleteAll()


}