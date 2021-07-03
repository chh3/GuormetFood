package com.example.gourmetfood.ui.district

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gourmetfood.R
import com.example.gourmetfood.logic.dao.CityAndDistrict

class ResultAdapter(
    private val resultList: MutableList<CityAndDistrict>,
    private val listener: OnItemClickListener
): RecyclerView.Adapter<ResultAdapter.ViewHolder>(){

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val districtName : TextView = view.findViewById(R.id.districtName)
        val lineView: View = view.findViewById(R.id.line_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_district, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val district = resultList[position]
        holder.districtName.text = district.name

        holder.districtName.setOnClickListener(View.OnClickListener {
            listener.onItemClick(it, position)
        })
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    public interface OnItemClickListener{
        public fun onItemClick(view: View, position: Int)
    }

}