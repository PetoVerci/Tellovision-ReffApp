package com.refapp.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.refapp.dao.*
import com.refapp.entities.*

@Database(entities = [DiscipCols::class,Disciplina::class, Persona::class, Team::class,Blok::class,BlokyDiscipliny::class,DynamicHodnotenie::class,TeamyDiscipliny::class],
    version = 1)
abstract class RefereeDatabase : RoomDatabase(){


    abstract val discDao : DisciplinaDAO
    abstract val personDao : PersonaDAO
    abstract val teamDao : TeamDAO
    abstract val teamyDisciplinyDAO: TeamyDisciplinyDAO
    abstract val blokyDisciplinyDAO : BlokyDisciplinyDAO
    abstract val dynamicHodnotenieDAO : DynamicHodnotenieDAO
    abstract val blokDAO : BlokDAO
    abstract val discipColsDAO : DiscipColsDAO


    companion object {
        @Volatile
        private var INSTANCE: RefereeDatabase? = null

        fun getInstance(context: Context): RefereeDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RefereeDatabase::class.java,
                    "school_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}