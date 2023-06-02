package com.refapp.jsonUtilClasses

import android.os.Environment
import android.util.Log
import com.refapp.db.RefereeDatabase
import com.refapp.entities.Blok
import com.refapp.entities.BlokyDiscipliny
import com.refapp.entities.DiscipCols
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.nio.channels.FileChannel
import java.nio.charset.Charset


class JsonParser(path: String) {
    var uniqueBlocks = mutableListOf<Blok>()

    var mapaViews: MutableMap<Int, MutableList<BlokyDiscipliny>> = mutableMapOf()
    var mapaNumOfBlocksPerDiscip: MutableMap<String, Int> = mutableMapOf()


    init {
        try {
            loadJson(path)
        }
        catch (e: Exception){
            e.printStackTrace()
        }

    }


    fun loadJson(path : String){
        var jsonData: String? = null
        try {
            val root =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (!root.exists()) {
                root.mkdirs()
            }
            val yourFile = File(path)
            val stream = FileInputStream(yourFile)
            try {
                val fc = stream.channel
                val bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())

                jsonData = Charset.defaultCharset().decode(bb).toString()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                stream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val o = jsonData?.let { JSONObject(it) }

        val discip = o?.getJSONArray("discipliny") as JSONArray
        for (i in 0 until discip.length()) {
            val menoHodnot = discip.getJSONObject(i).getString("name")
            val description = discip.getJSONObject(i).getString("description")

            if (!mapaViews.containsKey(i)) {
                mapaViews[i] = mutableListOf()
            }


            val usedBlok = discip.getJSONObject(i).getJSONArray("usedBlocks") as JSONArray
            var temp = mutableListOf<Pair<String, String>>()
            for (j in 0 until usedBlok.length()) {
                val label = usedBlok.getJSONObject(j).getString("label")
                val type = usedBlok.getJSONObject(j).getString("type")

                mapaNumOfBlocksPerDiscip[menoHodnot] =
                    mapaNumOfBlocksPerDiscip.getOrDefault(menoHodnot, 0) + 1

                mapaViews[i]?.add(
                    BlokyDiscipliny(
                        blokName = label,
                        disciplinaName = menoHodnot,
                        blokType = type
                    )
                )

                temp.add(Pair(label, type))

                if (i == 0 && j == 0) uniqueBlocks.add(Blok(label = label, type = type))

                checkArr(label, type)
            }
        }
    }


    fun checkArr(label: String, type: String) {
        for (u in uniqueBlocks) {
            if (u.label == label && u.type == type) {
                return
            }
        }
        uniqueBlocks.add(Blok(label = label, type = type))
    }


    suspend fun remodelDB(db: RefereeDatabase) {
        val allBlokyFromDB = db.blokDAO.getAllBloky()
        if (allBlokyFromDB.isNotEmpty()) db.blokDAO.deleteAll()
        val allBlokyDiscip = db.blokyDisciplinyDAO.getAllBlokyDiscipliny()
        if (allBlokyDiscip.isNotEmpty()) db.blokyDisciplinyDAO.deleteAll()
        val allDiscipCols = db.discipColsDAO.getAllDiscipCols()
        if (allDiscipCols.isNotEmpty()) db.discipColsDAO.deleteAll()


        for (i in uniqueBlocks) {
            db.blokDAO.insertBlok(Blok(label = i.label, type = i.type))
        }

        for (i in 0 until mapaViews.size) {
            for (j in mapaViews[i]!!) {
                db.blokyDisciplinyDAO.insertblokyDiscipliny(j)
            }
        }

        for (i in mapaNumOfBlocksPerDiscip) {
            db.discipColsDAO.inserDiscipCols(DiscipCols(discipName = i.key, colCount = i.value))
        }
    }
}