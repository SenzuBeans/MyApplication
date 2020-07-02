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

    private var tC_SortType = ""
    private var bC_ShowKey = false
    private var bC_SetTime = false
    private var oC_Cal = Calendar.getInstance()
    private var oC_Items = ArrayList<CmlData>()
    private var nC_IdSelect = -1
    private var tC_QuerySql = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.deleteDatabase(CmlDbHelper.TC_DatabaseName)
        oC_Binding = DataBindingUtil.setContentView(this, R.layout.w01main)
        odbC_Helper = CmlDbHelper(this)

        C_PGDxPlayground(savedInstanceState)
    }

    private fun C_PGDxPlayground(savedInstanceState: Bundle?) {

        ArrayAdapter.createFromResource(
            this,
            R.array.atAgregate,
            android.R.layout.simple_spinner_item
        ).also { oavArrayAdapter ->
            oavArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            oC_Binding.osn01AgregateFunction.adapter = oavArrayAdapter
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
                var tSql:String = "insert into ${CSDataEntryBV.T01VBusEntry} values (null ,? ,? ,?)"

                try{
                   odbHelper.rawQuery(tSql, arrayOf(oC_Binding.oet01EnterText.text.toString(),Random().nextInt(100).toString(),oC_Cal.time.toString()))
                    Toast.makeText(this@C01Main, "Insert data to BV complete.", Toast.LENGTH_SHORT).show()
                }catch (e: SQLiteException){
                    Toast.makeText(this@C01Main, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
            ocm01SelectInBV.setOnClickListener { view ->
                var tSql:String = "select * from " + CSDataEntryBV.T01VBusEntry + " where "+BaseColumns._ID+ " = ?"
                val odbHelper =  odbC_Helper.readableDatabase
                try{
                    val oCursor = odbHelper.rawQuery(tSql, arrayOf("1"))
                    oC_Items = ArrayList()
                    with(oCursor) {
                        Log.d("TAGG", "C_PGDxPlayground: "+ this.count.toString())

                        while (moveToNext()) {
                            val oItem = CmlData(
                                getString(getColumnIndex(CSDataEntryBV.FTNmtBusName)),
                                getString(getColumnIndex(CSDataEntryBV.FTVelBusValue)),
                                getString(getColumnIndex(CSDataEntryBV.FTDtcBusDataTime)),
                                getLong(getColumnIndexOrThrow(BaseColumns._ID))

                            )
                            oC_Items.add(oItem)
                            Log.d("TAGG", "C_PGDxPlayground: "+ oItem.tTitleData)
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
        val db = odbC_Helper.writableDatabase
        val values = ContentValues().apply {
            put(CSDataEntryNV.FTNmtName, oC_Binding.oet01EnterText.text.toString())
            put(CSDataEntryNV.FTVelValue, Random().nextInt(100).toString())
            put(CSDataEntryNV.FTDtcDataTime, oC_Cal.time.toString())
        }
        val newRowId = db.insert(CSDataEntryNV.T01VEntry, null, values)

        Log.d("TAGG", "C_ISTxInsertIntoDB: " + newRowId)

        C_REDxReadDatabase()
        db.close()
        Toast.makeText(this, "Insert data complete.", Toast.LENGTH_SHORT).show()
    }

    fun C_REDxReadDatabase() {
        val db = odbC_Helper.readableDatabase
        var tSortOrder: String? = null
        if (tC_SortType != "")
            tSortOrder = "${CSDataEntryNV.FTNmtName} " + tC_SortType


        val oCursor = db.query(
            CSDataEntryNV.T01VEntry,
            null,
            null,
            null,
            null,
            null,
            tSortOrder
        )

        oC_Items = ArrayList()
        with(oCursor) {
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
        oCursor.close()
        db.close()

        C_UPDxUpdateView()
    }

    fun C_UPDxUpdateDatabase(bRandom: Boolean) {
        val values = ContentValues().apply {
            put(CSDataEntryNV.FTNmtName, oC_Binding.oet01EnterText.text.toString())
            if (bRandom)
                put(CSDataEntryNV.FTVelValue, Random().nextInt(100).toString())
        }

        val tSelection = "${BaseColumns._ID} LIKE ?"
        val oatSelectionArgs = arrayOf(nC_IdSelect.toString())
        val count = odbC_Helper.writableDatabase.update(
            CSDataEntryNV.T01VEntry,
            values,
            tSelection,
            oatSelectionArgs
        )

        C_REDxReadDatabase()
    }

    fun C_DETxDeleteItem() {
        val tSelection = "${BaseColumns._ID} LIKE ?"
        val oatSelectionArgs = arrayOf(nC_IdSelect.toString())
        val oDeletedRows =
            odbC_Helper.writableDatabase.delete(CSDataEntryNV.T01VEntry, tSelection, oatSelectionArgs)
        Toast.makeText(this, "Delete successfully.", Toast.LENGTH_SHORT).show()
        nC_IdSelect = -1
        oC_Binding.otv01ShowText.text = "Deleted"
        C_REDxReadDatabase()
    }

    fun C_SEHxSearchItem(bDTSearch: Boolean, nKindOfKey: Int) {
        val db = odbC_Helper.readableDatabase
        var tSortOrder: String? = null
        if (tC_SortType != "")
            tSortOrder = "${CSDataEntryNV.FTNmtName} " + tC_SortType

        var tSelection =
            if (!bDTSearch)
                "${CSDataEntryNV.FTNmtName} LIKE ? "
            else
                "${CSDataEntryNV.FTDtcDataTime} LIKE ? or "+ "${CSDataEntryNV.FTDtcDataTime} LIKE ? or " + "${CSDataEntryNV.FTDtcDataTime} LIKE ?"

        val oatSelectionArgs =
            if (nKindOfKey == 0)
                arrayOf("%" + oC_Binding.oet01EnterText.text.toString() + "%")
            else if (nKindOfKey == 1)
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


        val oCursor = db.query(
            CSDataEntryNV.T01VEntry,
            null,
            tSelection,
            oatSelectionArgs,
            null,
            null,
            tSortOrder
        )

        oC_Items = ArrayList()
        with(oCursor) {
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
        oCursor.close()
        db.close()

        C_UPDxUpdateView()
    }

    fun C_UPDxUpdateView() {
        val nTopPosition =
            (oC_Binding.orv01RecyclerMain.layoutManager as LinearLayoutManager)?.findFirstVisibleItemPosition()

        oC_Binding.orv01RecyclerMain.adapter = CmlAdapter(bC_ShowKey, oC_Items, this)
        oC_Binding.orv01RecyclerMain.scrollToPosition(nTopPosition)

    }

    fun C_ADBxAlertDialog() {
        val oDialogListener = DialogInterface.OnClickListener { dialogInterface, nId ->
            when (nId) {
                DialogInterface.BUTTON_POSITIVE -> C_UPDxUpdateDatabase(true)
                DialogInterface.BUTTON_NEGATIVE -> C_UPDxUpdateDatabase(false)
                DialogInterface.BUTTON_NEUTRAL -> Log.d("TAGG", "C_ADBxAlertDialog: ")
            }
        }

        val oadBuilder = AlertDialog.Builder(this)
            .setMessage("WARNING!?!! you want to update with random new value for data.")
            .setPositiveButton("Yes", oDialogListener)
            .setNegativeButton("No, Don't random", oDialogListener)
            .setNeutralButton("Cancel", oDialogListener)

        oadBuilder.show()

    }

    fun C_ATAxActiveAgregate() {
        val oCursor = odbC_Helper.readableDatabase.rawQuery(tC_QuerySql, null)

        with(oCursor) {
            while (moveToNext()) {
                val oItem = getLong(getColumnIndex("answer"))
                oC_Binding.oet01EnterText.text = ("Answer is " + oItem.toString()).toEditable()
            }
        }
    }

    override fun onItemSelected(oavP0: AdapterView<*>?, ovwP1: View?, nP2: Int, nP3: Long) {
        oC_Binding.otv01ShowText.text =
            (oavP0?.getItemAtPosition(nP2).toString() + " function").toEditable()
        var tFunction = ""
        when (nP2) {
            1 -> tFunction = "SUM"
            2 -> tFunction = "COUNT"
            3 -> tFunction = "MIN"
            4 -> tFunction = "MAX"
        }

        tC_QuerySql =
            "select " + tFunction + "(" + CSDataEntryNV.FTVelValue + ") as answer from " + CSDataEntryNV.T01VEntry

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onSelected(oalItem: CmlData) {
        nC_IdSelect = oalItem.nKey.toInt()
        oC_Binding.apply {
            otv01ShowText.text = "Select key : " + oalItem.nKey
            oet01EnterText.text = oalItem.tTitleData.toEditable()
        }
        Toast.makeText(this, "Select at item : " + nC_IdSelect, Toast.LENGTH_SHORT).show()
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}