package com.example.myapplication.model.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class CmlDbHelper(poctContext: Context) : SQLiteOpenHelper(poctContext, TC_DatabaseName, null, 1) {
    override fun onCreate(podbP0: SQLiteDatabase) {
        podbP0.execSQL(sCreateDatabase)
        podbP0.execSQL(sCreateDatabaseBV)
    }

    override fun onUpgrade(podbP0: SQLiteDatabase, pnP1: Int, pnP2: Int) {
        podbP0.execSQL(sDeleteDatabase)
        podbP0.execSQL(sDeleteDatabaseBV)
        onCreate(podbP0)
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
