package com.example.myapplication.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import kotlinx.android.synthetic.main.w01item.view.*

class CmlAdapter(private val pbDisplayKey: Boolean,
                 private val paoItemPassing: ArrayList<CmlData>,
                 val poListener: CSelectItemListener): RecyclerView.Adapter<CmlAdapter.CAdapter>() {

    override fun onCreateViewHolder(povgParent: ViewGroup, pnViewType: Int): CAdapter {
        return CAdapter(LayoutInflater.from(povgParent.context).inflate(R.layout.w01item, povgParent, false))
    }

    override fun getItemCount(): Int {
        return paoItemPassing.size;
    }

    override fun onBindViewHolder(povwHolder: CAdapter, pnPosition: Int) {
        if (paoItemPassing.size > 0)
            povwHolder.C_IBDxItemBinding(pbDisplayKey,paoItemPassing.get(pnPosition))
        povwHolder.povwItemView.ocm01SelectButton.setOnClickListener {
            poListener.onSelected(paoItemPassing.get(pnPosition))
        }
    }

    class CAdapter(var povwItemView: View) : RecyclerView.ViewHolder(povwItemView) {
        fun C_IBDxItemBinding(pbDisplayKey: Boolean, poItem: CmlData){
            povwItemView.apply{
                when (pbDisplayKey){
                    true -> otv01DisplayKey.visibility = View.VISIBLE
                    false -> otv01DisplayKey.visibility = View.GONE
                }
                otv01DisplayText.text = poItem.tTitleData
                otv01DisplayDateTime.text = poItem.date
                otv01Value.text = poItem.value
                otv01DisplayKey.text = poItem.nKey.toString()
            }
        }
    }

    interface CSelectItemListener{
        fun onSelected(poItem : CmlData)
    }
}