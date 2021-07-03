package com.example.gourmetfood.ui.food

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gourmetfood.R
import com.example.gourmetfood.logic.data.Food

class FoodAdapter(private val foodList: MutableList<Food>, private val mOnShowListener: OnItemShowListener,
                    private val mOnDeleteListener: OnItemDeleteListener): RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val foodName : TextView = view.findViewById(R.id.food_name)
        val location: TextView = view.findViewById(R.id.food_location)
        val describe: TextView = view.findViewById(R.id.food_describe)
        val image: ImageView = view.findViewById(R.id.food_image)
        val showButton: Button = view.findViewById(R.id.button_show)
        val deleteButton: Button = view.findViewById(R.id.button_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = foodList[position]
        holder.foodName.text = food.name
        holder.location.text = food.location
        holder.describe.text = food.describe
        holder.showButton.setOnClickListener {
            mOnShowListener.onShow(position)
        }
        holder.deleteButton.setOnClickListener {
            mOnDeleteListener.onDelete(position)
        }
        val bm = BitmapFactory.decodeFile(food.image)
        holder.image.setImageBitmap(bm)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    public interface OnItemShowListener{
        fun onShow(position: Int)
    }

    public interface OnItemDeleteListener {
        fun onDelete(position: Int)
    }

}