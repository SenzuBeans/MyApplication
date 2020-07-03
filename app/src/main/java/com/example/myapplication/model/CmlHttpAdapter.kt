package com.example.myapplication.model

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import kotlinx.android.synthetic.main.w02item.view.*

class CmlHttpAdapter(var paItemPassing: ArrayList<CmlJObj>) : RecyclerView.Adapter<CmlHttpAdapter.CHttpHolder>() {

    override fun onCreateViewHolder(povgParent: ViewGroup, viewType: Int): CHttpHolder {
        return CHttpHolder( LayoutInflater.from(povgParent.context).inflate(R.layout.w02item, povgParent, false))
    }

    override fun getItemCount(): Int {
        return paItemPassing.size
    }

    override fun onBindViewHolder(oHolder: CHttpHolder, nPosition: Int) {
        oHolder.C_IBDxItemBinding(paItemPassing.get(nPosition))
    }

    class CHttpHolder(var povwItem: View) : RecyclerView.ViewHolder(povwItem) {
        fun C_IBDxItemBinding(oItem: CmlJObj){
            var oBmp = BitmapFactory.decodeByteArray(oItem.aImgObj,0, oItem.aImgObj.size)

            povwItem.apply {
                otv02DisplayCode.text = oItem.tRolCode
                otv02DisplayName.text = oItem.tRolName
                oiv02Image.setImageBitmap(oBmp)
            }
        }
    }
}