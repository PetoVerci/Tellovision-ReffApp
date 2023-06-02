package com.refapp.viewmodels

import androidx.lifecycle.ViewModel
import com.refapp.dao.DisciplinaDAO
import com.refapp.dao.TeamDAO
import com.refapp.dao.TeamyDisciplinyDAO
import com.refapp.entities.Disciplina
import com.refapp.entities.Team
import com.refapp.entities.TeamyDiscipliny
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MyViewModel : ViewModel() {
    var allTeamsNames : List<String> = ArrayList()
    var allDiscNames : List<String> = ArrayList()

    var teamsOfSelectedDiscInstances : List<TeamyDiscipliny> = ArrayList()
    var teamsOfSelectedDiscNames : List<String> = ArrayList()

    var allTeamsInstances : List<Team> = ArrayList()
    var allDiscInstances : List<Disciplina> = ArrayList()

    var currSelectedTeam : String? = null
    var currSelectedDisciplina : String? = null


    val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun setDropdownTeams(teamsDAO: TeamDAO){
        allTeamsInstances = teamsDAO.getAllTeams()
        allTeamsNames = allTeamsInstances.map { it.name }
    }

    fun setDropdownDiscs(discDao: DisciplinaDAO){
        allDiscInstances = discDao.getAllDiscipliny()
        allDiscNames = allDiscInstances.map { it.name }
    }

    fun setDropdownTeamsForDisc(teamyDisciplinyDAO: TeamyDisciplinyDAO, disc : String){
        teamsOfSelectedDiscInstances = teamyDisciplinyDAO.getAllTeamyDiscByDisc(disc)
        teamsOfSelectedDiscNames = teamsOfSelectedDiscInstances.map { it.teamName }
    }




    suspend fun insertTeamsDiscToDB(teamyDisciplinyDAO: TeamyDisciplinyDAO, arr : List<Pair<String,String>>){
        for (i in arr){
            teamyDisciplinyDAO.insertTeamDisc(TeamyDiscipliny(teamName = i.first, disciplinaName = i.second))
        }
    }
}