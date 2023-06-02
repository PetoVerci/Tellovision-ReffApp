package com.refapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.refapp.entities.TeamyDiscipliny


@Dao
interface TeamyDisciplinyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamDisc(teamyDiscipliny: TeamyDiscipliny)

    @Delete
    suspend fun deleteTeamDisc(teamyDiscipliny: TeamyDiscipliny)

    @Query("SELECT * FROM teamydiscipliny")
    fun getAllTeamyDisc() : List<TeamyDiscipliny>

    @Query("SELECT * FROM teamydiscipliny where disciplinaName = :discName")
    fun getAllTeamyDiscByDisc(discName : String) : List<TeamyDiscipliny>


    @Query("Delete from teamydiscipliny")
    fun deleteAllFromTeamydiscipliny()
}