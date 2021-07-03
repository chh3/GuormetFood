package com.example.gourmetfood.ui.district

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.gourmetfood.logic.Repository
import com.example.gourmetfood.logic.dao.CityAndDistrict

class DistrictViewModel: ViewModel()
{
    private val searchLiveData = MutableLiveData<String>()
    private val districtLiveData = MutableLiveData<Any?>()

    val districtList = ArrayList<CityAndDistrict>()

    val resultLiveData = Transformations.switchMap(searchLiveData) {
            query->
        Repository.searchDistrict(query)
    }

    val districtListLiveData = Transformations.switchMap(districtLiveData) { _ ->
        Repository.getAllDistrict()
    }

    fun searchDistrict(query: String) {
        searchLiveData.value = query
    }

    fun getAllDistricts() {
        districtLiveData.value = null
    }
}