package com.example.myapplication.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import kotlinx.android.synthetic.main.w01item.view.*

class CmlAdapter(private val bDisplayKey: Boolean, private val oalItemPassing: ArrayList<CmlBindingData>): RecyclerView.Adapter<CmlAdapter.CAdapter>() {


    override fun onCreateViewHolder(ovgParent: ViewGroup, nViewType: Int): CAdapter {
        return CAdapter(LayoutInflater.from(ovgParent.context).inflate(R.layout.w01item, ovgParent, false))
    }

    override fun getItemCount(): Int {
        return 10;
    }

    override fun onBindViewHolder(ovwHolder: CAdapter, nPosition: Int) {
        ovwHolder.C_IBDxItemBinding(bDisplayKey,oalItemPassing.get(nPosition))
    }

    class CAdapter(var ovwItemView: View) : RecyclerView.ViewHolder(ovwItemView) {
        fun C_IBDxItemBinding(bDisplayKey: Boolean, omlItem: CmlBindingData){
            ovwItemView.apply{
                when (bDisplayKey){
                    true -> otv01DisplayKey.visibility = View.VISIBLE
                    false -> otv01DisplayKey.visibility = View.GONE
                }
                otv01DisplayText.text = omlItem.tTitleData
                otv01DisplayKey.text = omlItem.nKey.toString()
            }
        }
    }
}