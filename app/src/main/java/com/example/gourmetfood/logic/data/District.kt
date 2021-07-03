package com.example.gourmetfood.logic.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class District(val name: String, val pinyin: String, val city: String){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Entity
data class City(val name: String, val pinyin: String, val province: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Entity
data class Province(@PrimaryKey val name: String) {

}