package com.example.gourmetfood.ui.food

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.gourmetfood.logic.Repository
import com.example.gourmetfood.logic.data.Food


class FoodViewModel: ViewModel()
{
    private val searchLiveData = MutableLiveData<String>()
    private val currentLocation = MutableLiveData("博白县")

    var init = false
    val foodList = ArrayList<Food>()
    val resultList = ArrayList<Food>()

    val foodLiveData = Transformations.switchMap(searchLiveData) {
        query->
        Repository.searchFood(query)
    }

    val locationFoodLiveData = Transformations.switchMap(currentLocation) {
        query->
        Repository.searchLocationFood(query)
    }



    fun searchFood(query: String) {
        searchLiveData.value = query
    }

    fun searchLocationFood(query: String) {
        currentLocation.value = query
    }


}