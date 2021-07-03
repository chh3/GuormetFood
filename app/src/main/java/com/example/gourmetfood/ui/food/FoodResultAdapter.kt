package com.example.gourmetfood.ui.food


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gourmetfood.R
import com.example.gourmetfood.logic.data.Food

class FoodResultAdapter(
    private val resultList: MutableList<Food>,
    private val listener: OnItemClickListener
): RecyclerView.Adapter<FoodResultAdapter.ViewHolder>(){

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val foodName : TextView = view.findViewById(R.id.districtName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_district, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = resultList[position]
        holder.foodName.text = food.name

        holder.foodName.setOnClickListener {
            listener.onItemClick(it, position) // 去到对应的showActivity
        }
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    interface OnItemClickListener{
        fun onItemClick(view: View, position: Int)
    }

}