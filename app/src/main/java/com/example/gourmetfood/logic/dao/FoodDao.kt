package com.example.gourmetfood.logic.dao

import androidx.room.*
import com.example.gourmetfood.logic.data.Food
import com.example.gourmetfood.logic.data.Province

@Dao
interface FoodDao {
    @Insert
    fun insertFood(food: Food)

    @Query("select * from Food where name like '%' || :query || '%'")
    fun getFood(query: String): MutableList<Food>

    @Query("select * from Food where id = :query")
    fun getFood(query: Int):Food

    @Query("select * from Food where location like :query ")
    fun getLocationFood(query: String): MutableList<Food>

    @Delete
    fun deleteFood(food: Food)

    @Update
    fun updateFood(food: Food)
}