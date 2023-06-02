package com.refapp.dao

import androidx.room.*
import com.refapp.entities.BlokyDiscipliny
import com.refapp.entities.Disciplina

@Dao
interface BlokyDisciplinyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertblokyDiscipliny(blokyDiscipliny: BlokyDiscipliny)

    @Delete
    suspend fun deleteblokyDiscipliny(blokyDiscipliny: BlokyDiscipliny)

    @Query("SELECT * FROM blokydiscipliny")
    fun getAllBlokyDiscipliny() : List<BlokyDiscipliny>

    @Query("Delete from blokydiscipliny")
    fun deleteAll()

    @Query("Select * from blokydiscipliny where disciplinaName =:dN")
    fun getBlocksOfDisc(dN : String) : List<BlokyDiscipliny>


//    @Query("SELECT * FROM bloky where :discName in (SELECT)")
//    fun getAllBlokyDisciplinyByDisc(discName : String) : List<BlokyDiscipliny>
}