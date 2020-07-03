package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.W01mainBinding
import com.example.myapplication.model.CmlAdapter
import com.example.myapplication.model.CmlData
import com.example.myapplication.model.db.CSDataEntryBV
import com.example.myapplication.model.db.CSDataEntryNV
import com.example.myapplication.model.db.CmlDbHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class C01Main : AppCompatActivity(),
    AdapterView.OnItemSelectedListener,
    CmlAdapter.CSelectItemListener {

    private lateinit var oC_Binding: W01mainBinding
    private lateinit var odbC_Helper: CmlDbHelper

    private var oC_Cal = Calendar.getInstance()
    private var oC_Items = ArrayList<CmlData>()
    private var bC_ShowKey = false
    private var bC_SetTime = false
    private var nC_IdSelect = -1
    private var tC_SortType = ""
    private var tC_QuerySql = ""


    override fun onCreate(poSavedInstanceState: Bundle?) {
        super.onCreate(poSavedInstanceState)
//        this.deleteDatabase(CmlDbHelper.TC_DatabaseName)
        oC_Binding = DataBindingUtil.setContentView(this, R.layout.w01main)
        odbC_Helper = CmlDbHelper(this)

        C_PGDxPlayground(poSavedInstanceState)
    }

    private fun C_PGDxPlayground(savedInstanceState: Bundle?) {

        ArrayAdapter.createFromResource(
            this,
            R.array.atAgregate,
            android.R.layout.simple_spinner_item
        ).also { aoArrayAdapter ->
            aoArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            oC_Binding.osn01AgregateFunction.adapter = aoArrayAdapter
        }

        val otpPick = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { otpP0, nP1, nP2 ->
            oC_Cal.set(Calendar.HOUR, nP1)
            oC_Cal.set(Calendar.MINUTE, nP2)
            oC_Binding.otv01DateTime.text =
                SimpleDateFormat("dd.MM.yyyy GGG hh:mm aaa").format(oC_Cal.time)
            bC_SetTime = true
        }, oC_Cal.get(Calendar.HOUR), oC_Cal.get(Calendar.MINUTE), false)
        val odpPick = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { odpP0, nP1, nP2, nP3 ->
                oC_Cal.set(nP1, nP2, nP3)
                otpPick.show()
            }, oC_Cal.get(Calendar.YEAR), oC_Cal.get(Calendar.MONTH), oC_Cal.get(Calendar.DATE))

        //SQLite 1st column
        oC_Binding.apply {
            orv01RecyclerMain.layoutManager = LinearLayoutManager(this@C01Main)
            osn01AgregateFunction.onItemSelectedListener = this@C01Main
            org01SortType.setOnCheckedChangeListener { orgP0, nP1 ->
                when (nP1) {
                    R.id.orb01Sort -> tC_SortType = "ASC"
                    R.id.orb01SortReverse -> tC_SortType = "DESC"
                }
                C_REDxReadDatabase()
            }
            ocb01ShowKey.setOnCheckedChangeListener { ocmP0: CompoundButton?, bP1: Boolean ->
                bC_ShowKey = bP1
                C_UPDxUpdateView()
            }
            ocm01SetDateTime.setOnClickListener { view ->
                odpPick.show()
            }
            ocm01Create.setOnClickListener { view ->
                C_ISTxInsertIntoDB()
            }
            ocm01Select.setOnClickListener { view ->
                C_REDxReadDatabase()
            }
            ocm01Update.setOnClickListener { view ->
                if (nC_IdSelect != -1)
                    C_ADBxAlertDialog()
                else
                    Toast.makeText(this@C01Main, "Please select item to update", Toast.LENGTH_SHORT)
                        .show()
            }
            ocm01Delete.setOnClickListener { view ->
                if (nC_IdSelect != -1)
                    C_DETxDeleteItem()
                else
                    Toast.makeText(this@C01Main, "Please select item to delete", Toast.LENGTH_SHORT)
                        .show()
            }
            ocm01Search.setOnClickListener { view ->
                if (oC_Binding.oet01EnterText.text.length > 0)
                    C_SEHxSearchItem(false, 0)
                else
                    Toast.makeText(
                        this@C01Main,
                        "Please enter fill beside \'Text on hold\'",
                        Toast.LENGTH_SHORT
                    ).show()
            }
            ocm01SearchByDate.setOnClickListener { view ->
                C_SEHxSearchItem(true, 1)
            }
            ocm01SearchByTime.setOnClickListener { view ->
                C_SEHxSearchItem(true, 2)
            }
            ocm01Active.setOnClickListener { view ->
                C_ATAxActiveAgregate()
            }
        }

        //SQLite 2nd column
        oC_Binding.apply{
            ocm01InsertInto.setOnClickListener { view ->
                val odbHelper =  odbC_Helper.writableDatabase
                var tSql:String = "insert into ${CSDataEntryBV.T01VBusEntry} values (null ," +
                        "\"${oC_Binding.oet01EnterText.text.toString()}\" ," +
                        "\"${Random().nextInt(100).toString()}\" ," +
                        "\"${oC_Cal.time.toString()}\")"

                try{
                    odbHelper.execSQL(tSql)
                    Toast.makeText(this@C01Main, "Insert data to BV complete.", Toast.LENGTH_SHORT).show()
                }catch (e: SQLiteException){
                    Toast.makeText(this@C01Main, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
            ocm01SelectBV.setOnClickListener { view ->
                var tSql:String = "select * from " + CSDataEntryBV.T01VBusEntry
                val odbHelper =  odbC_Helper.readableDatabase
                try{
                    val ocsCursor = odbHelper.rawQuery(tSql, null)
                    oC_Items = ArrayList()
                    with(ocsCursor) {

                        while (moveToNext()) {
                            val oItem = CmlData(
                                getString(getColumnIndex(CSDataEntryBV.FTNmtBusName)),
                                getString(getColumnIndex(CSDataEntryBV.FTVelBusValue)),
                                getString(getColumnIndex(CSDataEntryBV.FTDtcBusDataTime)),
                                getLong(getColumnIndexOrThrow(BaseColumns._ID))
                            )
                            oC_Items.add(oItem)
                        }
                    }

                    C_UPDxUpdateView()

                }catch (e: SQLiteException){
                    Toast.makeText(this@C01Main, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            ocm01SelectInto.setOnClickListener { view ->
                val odbHelper =  odbC_Helper.writableDatabase

                var tSqlSInto = "create table TempTable as select * from " + CSDataEntryBV.T01VBusEntry +" where _id > 5"
                var tSql = "select * from TempTable"
                try{
                    odbHelper.execSQL("drop table if exists TempTable")
                    odbHelper.execSQL(tSqlSInto)

                    val ocsCursor = odbHelper.rawQuery(tSql, null)
                    oC_Items = ArrayList()
                    with(ocsCursor) {
                        while (moveToNext()) {
                            val oItem = CmlData(
                                getString(getColumnIndex(CSDataEntryBV.FTNmtBusName)),
                                getString(getColumnIndex(CSDataEntryBV.FTVelBusValue)),
                                getString(getColumnIndex(CSDataEntryBV.FTDtcBusDataTime)),
                                getLong(getColumnIndexOrThrow(BaseColumns._ID))
                            )
                            oC_Items.add(oItem)
                        }
                    }
                    C_UPDxUpdateView()

                }catch (e: SQLiteException){
                    Toast.makeText(this@C01Main, e.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("TAGG", "C_PGDxPlayground: " +e.toString() )
                }
            }
            ocm01Join.setOnClickListener { view ->
                var tSql:String = "select " +
                        "${CSDataEntryNV.T01VEntry}.${BaseColumns._ID} as id, " +
                        "${CSDataEntryNV.T01VEntry}.${CSDataEntryNV.FTNmtName} as name, " +
                        "${CSDataEntryBV.T01VBusEntry}.${CSDataEntryBV.FTNmtBusName} as bName, " +
                        "${CSDataEntryNV.T01VEntry}.${CSDataEntryNV.FTVelValue} as value " +
                        "from ${CSDataEntryNV.T01VEntry} " +
                        "join ${CSDataEntryBV.T01VBusEntry} " +
                        "on ${CSDataEntryNV.T01VEntry}.${BaseColumns._ID} = ${CSDataEntryBV.T01VBusEntry}.${BaseColumns._ID}"
                val odbHelper =  odbC_Helper.readableDatabase
                try{
                    val ocsCursor = odbHelper.rawQuery(tSql, null)
                    oC_Items = ArrayList()
                    with(ocsCursor) {

                        while (moveToNext()) {
                            val oItem = CmlData(
                                getString(getColumnIndex("name")),
                                getString(getColumnIndex("value")),
                                getString(getColumnIndex("bName")),
                                getLong(getColumnIndexOrThrow("id"))
                            )
                            oC_Items.add(oItem)
                        }
                    }

                    C_UPDxUpdateView()

                }catch (e: SQLiteException){
                    Toast.makeText(this@C01Main, e.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("TAGG", "C_PGDxPlayground: " +e.toString() )
                }
            }
            ocm01SubQuery.setOnClickListener { view ->
                var tSql:String = "select * " +
                        "from ${CSDataEntryNV.T01VEntry} " +
                        "where ${BaseColumns._ID} = (" +
                        "select ${BaseColumns._ID} " +
                        "from ${CSDataEntryBV.T01VBusEntry} " +
                        "where ${CSDataEntryBV.FTVelBusValue} > 50)"
                val odbHelper =  odbC_Helper.readableDatabase
                try{
                    val ocsCursor = odbHelper.rawQuery(tSql, null)
                    oC_Items = ArrayList()
                    with(ocsCursor) {

                        while (moveToNext()) {
                            val oItem = CmlData(
                                getString(getColumnIndex(CSDataEntryNV.FTNmtName)),
                                getString(getColumnIndex(CSDataEntryNV.FTVelValue)),
                                getString(getColumnIndex(CSDataEntryNV.FTDtcDataTime)),
                                getLong(getColumnIndexOrThrow(BaseColumns._ID))
                            )
                            oC_Items.add(oItem)
                        }
                    }

                    C_UPDxUpdateView()

                }catch (e: SQLiteException){
                    Toast.makeText(this@C01Main, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            ocm01DeleteBV.setOnClickListener { view ->
                var tSqlDelete:String = "delete from ${CSDataEntryBV.T01VBusEntry} " +
                        "where ${BaseColumns._ID} = ${nC_IdSelect}"
                var tSql:String = "select * from " + CSDataEntryBV.T01VBusEntry

                val odbHelper =  odbC_Helper.readableDatabase
                try{
                    odbHelper.execSQL(tSqlDelete)

                    val ocsCursor = odbHelper.rawQuery(tSql, null)
                    oC_Items = ArrayList()
                    with(ocsCursor) {
                        while (moveToNext()) {
                            val oItem = CmlData(
                                getString(getColumnIndex(CSDataEntryBV.FTNmtBusName)),
                                getString(getColumnIndex(CSDataEntryBV.FTVelBusValue)),
                                getString(getColumnIndex(CSDataEntryBV.FTDtcBusDataTime)),
                                getLong(getColumnIndexOrThrow(BaseColumns._ID))
                            )
                            oC_Items.add(oItem)
                        }
                    }

                    C_UPDxUpdateView()

                }catch (e: SQLiteException){
                    Toast.makeText(this@C01Main, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }

        }

        C_REDxReadDatabase()
    }

    fun C_ISTxInsertIntoDB() {
        val odbHelper = odbC_Helper.writableDatabase
        val oclValues = ContentValues().apply {
            put(CSDataEntryNV.FTNmtName, oC_Binding.oet01EnterText.text.toString())
            put(CSDataEntryNV.FTVelValue, Random().nextInt(100).toString())
            put(CSDataEntryNV.FTDtcDataTime, oC_Cal.time.toString())
        }
        val newRowId = odbHelper.insert(CSDataEntryNV.T01VEntry, null, oclValues)
        
        C_REDxReadDatabase()
        odbHelper.close()
        Toast.makeText(this, "Insert data complete.", Toast.LENGTH_SHORT).show()
    }

    fun C_REDxReadDatabase() {
        val odbHelper = odbC_Helper.readableDatabase
        var tSortOrder: String? = null
        if (tC_SortType != "")
            tSortOrder = "${CSDataEntryNV.FTNmtName} " + tC_SortType
        
        val ocsCursor = odbHelper.query(
            CSDataEntryNV.T01VEntry,
            null,
            null,
            null,
            null,
            null,
            tSortOrder
        )

        oC_Items = ArrayList()
        with(ocsCursor) {
            while (moveToNext()) {
                val oItem = CmlData(
                    getString(getColumnIndex(CSDataEntryNV.FTNmtName))
                    , getString(getColumnIndex(CSDataEntryNV.FTVelValue))
                    , getString(getColumnIndex(CSDataEntryNV.FTDtcDataTime))
                    , getLong(getColumnIndexOrThrow(BaseColumns._ID))
                )
                oC_Items.add(oItem)
            }
        }
        ocsCursor.close()
        odbHelper.close()

        C_UPDxUpdateView()
    }

    fun C_UPDxUpdateDatabase(pbRandom: Boolean) {
        val oclValues = ContentValues().apply {
            put(CSDataEntryNV.FTNmtName, oC_Binding.oet01EnterText.text.toString())
            if (pbRandom)
                put(CSDataEntryNV.FTVelValue, Random().nextInt(100).toString())
        }

        val tSelection = "${BaseColumns._ID} LIKE ?"
        val atSelectionArgs = arrayOf(nC_IdSelect.toString())
        odbC_Helper.writableDatabase.update(
            CSDataEntryNV.T01VEntry,
            oclValues,
            tSelection,
            atSelectionArgs
        )

        C_REDxReadDatabase()
    }

    fun C_DETxDeleteItem() {
        val tSelection = "${BaseColumns._ID} LIKE ?"
        val atSelectionArgs = arrayOf(nC_IdSelect.toString())
        odbC_Helper.writableDatabase.delete(CSDataEntryNV.T01VEntry, tSelection, atSelectionArgs)
        Toast.makeText(this, "Delete successfully.", Toast.LENGTH_SHORT).show()
        nC_IdSelect = -1
        oC_Binding.otv01ShowText.text = "Deleted"
        C_REDxReadDatabase()
    }

    fun C_SEHxSearchItem(pbDTSearch: Boolean, pnKindOfKey: Int) {
        val odbHelper = odbC_Helper.readableDatabase
        var tSortOrder: String? = null
        if (tC_SortType != "")
            tSortOrder = "${CSDataEntryNV.FTNmtName} " + tC_SortType

        var tSelection =
            if (!pbDTSearch)
                "${CSDataEntryNV.FTNmtName} LIKE ? "
            else
                "${CSDataEntryNV.FTDtcDataTime} LIKE ? or "+ "${CSDataEntryNV.FTDtcDataTime} LIKE ? or " + "${CSDataEntryNV.FTDtcDataTime} LIKE ?"

        val atSelectionArgs =
            if (pnKindOfKey == 0)
                arrayOf("%" + oC_Binding.oet01EnterText.text.toString() + "%")
            else if (pnKindOfKey == 1)
                arrayOf(
                    "%" + SimpleDateFormat("dd").format(oC_Cal.time) + "%",
                    "%" + SimpleDateFormat("MMM").format(oC_Cal.time) + "%",
                    "%" + SimpleDateFormat("yyyy").format(oC_Cal.time) + "%"
                )
            else
                arrayOf(
                    "%" + SimpleDateFormat("hh").format(oC_Cal.time) + "%",
                    "%" + SimpleDateFormat("mm").format(oC_Cal.time) + "%",
                    "%" + SimpleDateFormat("aaa").format(oC_Cal.time) + "%"
                )

        val ocsCursor = odbHelper.query(
            CSDataEntryNV.T01VEntry,
            null,
            tSelection,
            atSelectionArgs,
            null,
            null,
            tSortOrder
        )

        oC_Items = ArrayList()
        with(ocsCursor) {
            while (moveToNext()) {
                val oItem = CmlData(
                    getString(getColumnIndex(CSDataEntryNV.FTNmtName))
                    , getString(getColumnIndex(CSDataEntryNV.FTVelValue))
                    , getString(getColumnIndex(CSDataEntryNV.FTDtcDataTime))
                    , getLong(getColumnIndexOrThrow(BaseColumns._ID))
                )
                oC_Items.add(oItem)
            }
        }
        ocsCursor.close()
        odbHelper.close()

        C_UPDxUpdateView()
    }

    fun C_UPDxUpdateView() {
        val nTopPosition =
            (oC_Binding.orv01RecyclerMain.layoutManager as LinearLayoutManager)?.findFirstVisibleItemPosition()

        oC_Binding.apply {
            orv01RecyclerMain.adapter = CmlAdapter(bC_ShowKey, oC_Items, this@C01Main)
            orv01RecyclerMain.scrollToPosition(nTopPosition)
            oet01EnterText.text = "".toEditable()
            otv01ShowText.text = "Text on hold"
        }
    }

    fun C_ADBxAlertDialog() {
        val oDialogListener = DialogInterface.OnClickListener { dialogInterface, nId ->
            when (nId) {
                DialogInterface.BUTTON_POSITIVE -> C_UPDxUpdateDatabase(true)
                DialogInterface.BUTTON_NEGATIVE -> C_UPDxUpdateDatabase(false)
                DialogInterface.BUTTON_NEUTRAL -> Log.d("TAGG", "C_ADBxAlertDialog: ")
            }
        }

        val oBuilder = AlertDialog.Builder(this)
            .setMessage("WARNING!?!! you want to update with random new value for data.")
            .setPositiveButton("Yes", oDialogListener)
            .setNegativeButton("No, Don't random", oDialogListener)
            .setNeutralButton("Cancel", oDialogListener)

        oBuilder.show()
    }

    fun C_ATAxActiveAgregate() {
        val ocsCursor = odbC_Helper.readableDatabase.rawQuery(tC_QuerySql, null)

        with(ocsCursor) {
            while (moveToNext()) {
                val oItem = getLong(getColumnIndex("answer"))
                oC_Binding.oet01EnterText.text = ("Answer is " + oItem.toString()).toEditable()
            }
        }
    }

    override fun onItemSelected(pavP0: AdapterView<*>?, povwP1: View?, pnP2: Int, pnP3: Long) {
        oC_Binding.otv01ShowText.text =
            (pavP0?.getItemAtPosition(pnP2).toString() + " function").toEditable()
        var tFunction = ""
        when (pnP2) {
            1 -> tFunction = "SUM"
            2 -> tFunction = "COUNT"
            3 -> tFunction = "MIN"
            4 -> tFunction = "MAX"
        }

        tC_QuerySql =
            "select " + tFunction + "(" + CSDataEntryNV.FTVelValue + ") as answer from " + CSDataEntryNV.T01VEntry

    }

    override fun onNothingSelected(pavP0: AdapterView<*>?) {}

    override fun onSelected(poItem: CmlData) {
        nC_IdSelect = poItem.nKey.toInt()
        oC_Binding.apply {
            otv01ShowText.text = "Select key : " + poItem.nKey
            oet01EnterText.text = poItem.tTitleData.toEditable()
        }
        Toast.makeText(this, "Select at item : " + nC_IdSelect, Toast.LENGTH_SHORT).show()
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}