package com.refapp.csvClasses

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets


class CSVReaderTeamsDisc(path : String) {
//    val path = "RoboCup2023_obsadenie_kategorii.csv"
    val pathh = path

    var TEAMSDISC: MutableList<Pair<String, String>>
    var TEAMS: MutableList<String>
    var DISCS: MutableList<String>


    init {

            TEAMSDISC = readCSVFile()
            TEAMS = getAllTeams()
            DISCS = getAllDiscs()



    }

    fun getAllTeams(): MutableList<String> {
        val temp = mutableListOf<String>()
        for (i in TEAMSDISC) {
            temp.add(i.first.replace("\"", ""))
        }
        return temp.toSet().toMutableList()

    }

    fun getAllDiscs(): MutableList<String> {
        val temp = mutableListOf<String>()
        for (i in TEAMSDISC) {
            temp.add(i.second.replace("\"", ""))
        }
        return temp.toSet().toMutableList()


    }


    fun readCSVFile(): MutableList<Pair<String, String>> {
        val reader = BufferedReader(InputStreamReader(FileInputStream(pathh)))
        val header = reader.readLine()
        val headerList = header.split(";").toMutableList()
//        Log.i("teamy",headerList.toString())
        headerList.removeAt(0)
        var line: String? = reader.readLine()
        var j = 0
        var arrTeamDisc = mutableListOf<Pair<String, String>>()
        while (line != null) {
            if (line.isNotBlank()) {
                val currLineList = line.split(";").toMutableList()
                val team = currLineList[0]
                currLineList.removeAt(0)
                for (i in currLineList) {
                    if (i != "") {
                        arrTeamDisc.add(
                            Pair(
                                team.replace("\"", ""),
                                headerList[j].replace("\"", "")
                            )
                        )
                    }
                    j++
                }
            }
            j = 0
            line = reader.readLine()
        }
        return arrTeamDisc
    }


}