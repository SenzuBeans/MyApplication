package com.example.myapplication.model.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import java.text.SimpleDateFormat
import java.util.*

class CmlDbHelper(octContext: Context): SQLiteOpenHelper(octContext, "AdaExp.SQLite", null, 1) {
    override fun onCreate(odbP0: SQLiteDatabase?) {
        odbP0?.execSQL(sCreateDatabase)
    }

    override fun onUpgrade(odbP0: SQLiteDatabase?, nP1: Int, nP2: Int) {
        odbP0?.execSQL(sDeleteDatabase)
        onCreate(odbP0)
    }

    companion object{
        private const val sCreateDatabase = "CREATE TABLE ${CSDataContract.CSDataEntry.T01VEntry} ("+
                "${BaseColumns._ID} INTEGER PRIMARY KEY autoincrement," +
                "${CSDataContract.CSDataEntry.FTNmtName} TEXT," +
                "${CSDataContract.CSDataEntry.FDDtcDataTime} integer)"

        private const val sDeleteDatabase = "drop table IF exists ${CSDataContract.CSDataEntry.T01VEntry}"
    }
}

object CSDataContract {
    object CSDataEntry: BaseColumns {
        const val T01VEntry = "entry"
        const val FTNmtName = "text"
        const val FDDtcDataTime = "datetime"
    }
//    "yyyyy.MMMMM.dd GGG hh aaa"
}