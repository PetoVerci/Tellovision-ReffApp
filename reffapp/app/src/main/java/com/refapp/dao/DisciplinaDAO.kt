package com.refapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.google.android.material.circularreveal.CircularRevealHelper.Strategy
import com.refapp.entities.Disciplina

@Dao
interface DisciplinaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisciplina(disciplina: Disciplina)

    @Delete
    suspend fun deleteDisciplina(disciplina: Disciplina)

    @Query("SELECT * FROM discipliny")
    fun getAllDiscipliny() : List<Disciplina>

    @Query("SELECT * FROM discipliny WHERE name = :nameofDiscip ")
    fun getInstanceOfDisciplina(nameofDiscip : String) : Disciplina

    @Query("Delete from discipliny")
    fun deleteAll()
}