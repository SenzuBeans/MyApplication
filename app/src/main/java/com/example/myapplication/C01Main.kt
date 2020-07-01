package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.W01mainBinding
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class C01Main : AppCompatActivity(),
    AdapterView.OnItemSelectedListener{

    private lateinit var oC_Binding: W01mainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.w01main)
        oC_Binding = DataBindingUtil.setContentView(this, R.layout.w01main)

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

        val cal = Calendar.getInstance()
        val otpPick = TimePickerDialog(this,  TimePickerDialog.OnTimeSetListener { otpP0, nP1, nP2 ->
            cal.set(Calendar.HOUR,nP1)
            cal.set(Calendar.MINUTE, nP2)
            oC_Binding.otv01DateTime.text = SimpleDateFormat("dd.MM.yyyy GGG hh aaa").format(cal.time)
        }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), false)
        val odpPick = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{odpP0, nP1, nP2, nP3->
            cal.set(nP1,nP2,nP3)
            otpPick.show()
        }, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DATE))

        oC_Binding.osn01AgregateFunction.onItemSelectedListener = this
        oC_Binding.org01SortType.setOnCheckedChangeListener{orgP0, nP1 ->
            //        Log.d("TAGG", "onItemSelected: " + orgP0?.id + " : "+nP1)
            /* when(nP1){
                 R.id.orb01Sort -> //TODO
                 R.id.orb01SortReverse ->
             }*/
        }
        oC_Binding.ocb01ShowKey.setOnCheckedChangeListener{ocmP0: CompoundButton?, bP1: Boolean ->
            if (bP1) {

            } else {

            }
        }
        oC_Binding.ocm01SetDateTime.setOnClickListener { view ->
            odpPick.show()
        }


     }


    override fun onItemSelected(oavP0: AdapterView<*>?, ovwP1: View?, nP2: Int, nP3: Long) {
//        oC_Binding.otv01ShowText.text = oavP0?.getItemAtPosition(nP2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}