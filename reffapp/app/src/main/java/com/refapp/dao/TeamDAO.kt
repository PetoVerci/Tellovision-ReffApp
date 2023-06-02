package com.refapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.refapp.entities.Team

@Dao
interface TeamDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: Team)

    @Delete
    suspend fun deleteTeam(team: Team)

    @Query("SELECT * FROM teamy")
    fun getAllTeams() : List<Team>


    @Query("Delete from teamy")
    fun deleteAll()
}