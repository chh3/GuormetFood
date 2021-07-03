package com.example.gourmetfood.ui.district

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gourmetfood.R
import com.example.gourmetfood.logic.dao.CityAndDistrict


class DistrictAdapter(
    val districtList: MutableList<CityAndDistrict>,
    letterToCity: HashMap<String, Int>,
    private val listener: onItemClickListener
): RecyclerView.Adapter<DistrictAdapter.ViewHolder>(){

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val districtName : TextView = view.findViewById(R.id.districtName)
        val letterTextView: TextView = view.findViewById(R.id.tv_letter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_district, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val district = districtList[position]
        holder.districtName.text = district.name

        holder.districtName.setOnClickListener(View.OnClickListener {
            listener.onItemClick(it, position)
        })

        if (position == 0) {
            holder.letterTextView.text = district.getFirst()
            holder.letterTextView.visibility = View.VISIBLE
        } else {
            if (district.getFirst() != districtList[position - 1].getFirst()) {
                holder.letterTextView.text = district.getFirst()
                holder.letterTextView.visibility = View.VISIBLE
            } else {
                holder.letterTextView.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return districtList.size
    }

    public interface onItemClickListener{
        public fun onItemClick(view: View, position: Int)
    }

}