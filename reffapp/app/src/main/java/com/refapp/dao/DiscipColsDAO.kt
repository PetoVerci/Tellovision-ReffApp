package com.refapp.dao

import androidx.room.*
import com.refapp.entities.BlokyDiscipliny
import com.refapp.entities.DiscipCols

@Dao
interface DiscipColsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserDiscipCols(discipCols: DiscipCols)

    @Delete
    suspend fun deleteDiscipCols(discipCols: DiscipCols)

    @Query("SELECT * FROM discipcols")
    fun getAllDiscipCols() : List<DiscipCols>

    @Query("Delete from discipcols")
    fun deleteAll()

    @Query("Select colCount from discipcols where discipName =:dN")
    fun getColsOfDiscip(dN : String) : Int
}