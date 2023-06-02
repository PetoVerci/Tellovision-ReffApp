package com.refapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.refapp.entities.Persona


@Dao
interface PersonaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(persona: Persona)

    @Delete
    suspend fun deletePerson(persona: Persona)

    @Query("SELECT * FROM persony")
    fun getAllPeople() : LiveData<List<Persona>>
}