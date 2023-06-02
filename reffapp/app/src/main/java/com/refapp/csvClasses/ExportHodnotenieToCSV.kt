package com.refapp.csvClasses

import android.os.Environment
import com.refapp.entities.DynamicHodnotenie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

class ExportHodnotenieToCSV() {


    suspend fun saveDataToCSV(data: List<DynamicHodnotenie>, interval: Int) {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        if (!root.exists()) {
            root.mkdirs()
        }
        val dir = File(root, "/robocup")
        if (!dir.exists()) dir.mkdirs()
        var file = File(dir, "${data[0].discName}Ratings.csv")
        if (file.exists()) file.delete()
        file = File(dir, "${data[0].discName}Ratings.csv")
        try {
            val f = withContext(Dispatchers.IO) {
                FileOutputStream(file)
            }
            val pw = PrintWriter(f)
            pw.print("Meno Teamu;")
            for (i in 0 until interval) {
                pw.print(data[i].blockLabel)
                if (i < interval - 1) pw.print(";")
                else pw.print(";Suma bodov")

            }
            pw.print("\n")
            var cnt = 0
            var sumaBodov = 0
            for (i in data) {
                if (cnt == 0) {
                    pw.print("${i.teamName};")
                }
                pw.print("${i.blockScore}")
                sumaBodov += i.blockScore
                cnt++
                if (cnt < interval) pw.print(";")

                if (cnt == interval) {
                    pw.print(";$sumaBodov")
                    pw.print("\n")
                    cnt = 0
                    sumaBodov = 0
                }
            }
            pw.flush()
            pw.close()
            withContext(Dispatchers.IO) {
                f.close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}