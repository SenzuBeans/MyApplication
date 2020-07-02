package com.example.myapplication.model.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class CmlDbHelper(octContext: Context) : SQLiteOpenHelper(octContext, TC_DatabaseName, null, 1) {
    override fun onCreate(odbP0: SQLiteDatabase) {
        odbP0.execSQL(sCreateDatabase)
        odbP0.execSQL(sCreateDatabaseBV)
    }

    override fun onUpgrade(odbP0: SQLiteDatabase, nP1: Int, nP2: Int) {
        odbP0.execSQL(sDeleteDatabase)
        odbP0.execSQL(sDeleteDatabaseBV)
        onCreate(odbP0)
    }

    companion object {
        const val TC_DatabaseName = "AdaExp.SQLite"

        private const val sCreateDatabaseBV = "CREATE TABLE ${CSDataEntryBV.T01VBusEntry} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${CSDataEntryBV.FTNmtBusName} TEXT," +
                "${CSDataEntryBV.FTVelBusValue} TEXT," +
                "${CSDataEntryBV.FTDtcBusDataTime} TEXT)"

        private const val sCreateDatabase = "CREATE TABLE ${CSDataEntryNV.T01VEntry} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${CSDataEntryNV.FTNmtName} TEXT," +
                "${CSDataEntryNV.FTVelValue} TEXT," +
                "${CSDataEntryNV.FTDtcDataTime} TEXT)"

        private const val sDeleteDatabaseBV = "drop table IF exists ${CSDataEntryBV.T01VBusEntry}"

        private const val sDeleteDatabase = "drop table IF exists ${CSDataEntryNV.T01VEntry}"
    }
}

object CSDataEntryNV : BaseColumns {
    const val T01VEntry = "TableEntry"
    const val FTNmtName = "text"
    const val FTVelValue = "value"
    const val FTDtcDataTime = "datetime"
}


object CSDataEntryBV : BaseColumns {
    const val T01VBusEntry = "newTableEntryBV"
    const val FTNmtBusName = "BusText"
    const val FTVelBusValue = "BusValue"
    const val FTDtcBusDataTime = "BusDatetime"
}
//    "yyyyy.MMMMM.dd GGG hh aaa"
