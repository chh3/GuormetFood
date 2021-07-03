package com.example.gourmetfood.logic.dao

data class DistrictResponse(val status: String, val districts: List<Districts>)

data class Districts(val name: String,  val districts: List<Districts>)