package com.example.gourmetfood.logic

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.liveData
import com.example.gourmetfood.FoodApplication
import com.example.gourmetfood.logic.dao.CityAndDistrict
import com.example.gourmetfood.logic.data.AppDatabase
import com.example.gourmetfood.logic.data.City
import com.example.gourmetfood.logic.data.Food
import kotlinx.coroutines.Dispatchers

object Repository {
    fun searchFood(query: String) = liveData(Dispatchers.IO) {
        // 保证数据库的访问运行在子线程中
        val result = try {
            val foodDao = AppDatabase.getDatabase(FoodApplication.context).foodDao()
            val food = foodDao.getFood(query)
            Result.success(food)

        } catch (e: Exception) {
            Log.e("exception", e.toString())
            Result.failure(e)
        }
        emit(result)
    }

    fun searchLocationFood(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val foodDao = AppDatabase.getDatabase(FoodApplication.context).foodDao()
            val food = foodDao.getLocationFood(query)
            Result.success(food)
        } catch (e: Exception) {
            Log.e("exception: ", e.toString())
            Result.failure(e)
        }
        emit(result)
    }

    fun searchDistrict(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val districtDao = AppDatabase.getDatabase(FoodApplication.context).districtDao()
            val district = districtDao.getAllCityAndDistrict(query)
            Log.d("tested: ", district.toString())
            Result.success(district)
        } catch (e: Exception) {
            Log.e("tested: ", e.toString())
            // 这是thread, 不能showtoast
            Result.failure(e)
        }
        emit(result)
    }

    fun getAllDistrict() = liveData(Dispatchers.IO) {
        val result = try {
            val districtDao = AppDatabase.getDatabase(FoodApplication.context).districtDao()
            val district = districtDao.getAllCityAndDistrict()
            Log.d("tested: ", district.toString())
            Result.success(district)
        } catch (e: Exception) {
            Log.e("tested: ", e.toString())
            Result.failure(e)
        }
        emit(result)
    }

}