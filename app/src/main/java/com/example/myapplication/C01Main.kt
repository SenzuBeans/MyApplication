package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.W01mainBinding
import com.example.myapplication.model.CmlAdapter
import com.example.myapplication.model.CmlData
import com.example.myapplication.model.db.CSDataEntry
import com.example.myapplication.model.db.CmlDbHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class C01Main : AppCompatActivity(),
    AdapterView.OnItemSelectedListener{

    private lateinit var oC_Binding: W01mainBinding
    private lateinit var odbC_Helper: CmlDbHelper

    private var tC_SortType = ""
    private var bC_ShowKey = false
    private var bC_SetTime = false
    private var oC_Cal = Calendar.getInstance()
    private var oC_Items = ArrayList<CmlData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.w01main)
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

        val otpPick = TimePickerDialog(this,  TimePickerDialog.OnTimeSetListener { otpP0, nP1, nP2 ->
            oC_Cal.set(Calendar.HOUR,nP1)
            oC_Cal.set(Calendar.MINUTE, nP2)
            oC_Binding.otv01DateTime.text = SimpleDateFormat("dd.MM.yyyy GGG hh aaa").format(oC_Cal.time)
            bC_SetTime = true
        }, oC_Cal.get(Calendar.HOUR), oC_Cal.get(Calendar.MINUTE), false)
        val odpPick = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{odpP0, nP1, nP2, nP3->
            oC_Cal.set(nP1,nP2,nP3)
            otpPick.show()
        }, oC_Cal.get(Calendar.YEAR),oC_Cal.get(Calendar.MONTH), oC_Cal.get(Calendar.DATE))

        oC_Binding.apply {
            orv01RecyclerMain.layoutManager = LinearLayoutManager(this@C01Main)
            osn01AgregateFunction.onItemSelectedListener = this@C01Main
            org01SortType.setOnCheckedChangeListener { orgP0, nP1 ->
                 when(nP1) {
                     R.id.orb01Sort -> tC_SortType = "ASC"
                     R.id.orb01SortReverse -> tC_SortType = "DESC"
                 }
            }
            ocb01ShowKey.setOnCheckedChangeListener { ocmP0: CompoundButton?, bP1: Boolean ->
                bC_ShowKey = bP1
            }
            ocm01SetDateTime.setOnClickListener { view ->
                odpPick.show()
            }
            ocm01Create.setOnClickListener { view ->
                C_ISTxInsertIntoDB()
                Log.d("TAGG", "C_PGDxPlayground: ")
            }
        }

        C_REDxReadDatabase()
     }

    fun C_UPDxUpdateView(){
        oC_Binding.orv01RecyclerMain.adapter = CmlAdapter(bC_ShowKey, oC_Items)
    }

    fun C_ISTxInsertIntoDB(){
        val db = odbC_Helper.writableDatabase
        val values = ContentValues().apply{
            put(CSDataEntry.FTNmtName, oC_Binding.oet01EnterText.text.toString())
            put(CSDataEntry.FDDtcDataTime, oC_Cal.time.toString())
            put(CSDataEntry.FNVelValue, Random().nextInt(100))
        }

        val newRowId = db?.insert(CSDataEntry.T01VEntry, null, values)
        C_REDxReadDatabase()
        db.close()
        Toast.makeText(this, "Insert data complete." , Toast.LENGTH_SHORT).show()
    }

    fun C_REDxReadDatabase(){
        val db = odbC_Helper.readableDatabase

//        val sortOrder = "${CSDataContract.CSDataEntry.FTNmtName} ASC"

        val oCursor = db.query(
            CSDataEntry.T01VEntry,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(oCursor) {
            Log.d("TAGG", "C_REDxReadDatabase: "+ oCursor.count.toString())

            while (moveToNext()) {

                val oItem = CmlData(getString(getColumnIndex(CSDataEntry.FTNmtName))
                    ,getInt(getColumnIndex(CSDataEntry.FNVelValue))
                    ,getLong(getColumnIndexOrThrow(BaseColumns._ID)))
                oC_Items.add(oItem)
            }
        }
        oCursor.close()
        db.close()

        C_UPDxUpdateView()
    }

    fun C_UPDxUpdateDatabase(){

    }

    fun C_DETxDeleteItem(){

    }


    override fun onItemSelected(oavP0: AdapterView<*>?, ovwP1: View?, nP2: Int, nP3: Long) {
//        oC_Binding.otv01ShowText.text = oavP0?.getItemAtPosition(nP2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}