package com.refapp.dao

import androidx.room.*
import com.refapp.entities.DynamicHodnotenie

@Dao
interface DynamicHodnotenieDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDynHod(dynamicHodnotenie: DynamicHodnotenie)

    @Delete
    suspend fun deleteDynHod(dynamicHodnotenie: DynamicHodnotenie)

    @Query("SELECT * FROM dynamichodnotenie")
    fun getAllDynHodnotenia() : List<DynamicHodnotenie>

    @Query("SELECT * FROM dynamichodnotenie where discName = :discName")
    fun getAllDynHodnoteniaByDisc(discName : String) : List<DynamicHodnotenie>
}