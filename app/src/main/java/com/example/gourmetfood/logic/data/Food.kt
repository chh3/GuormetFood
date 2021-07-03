package com.example.gourmetfood.logic.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Food(var name: String, var location: String, var describe: String,
                var history: String?, var image: String,
                var procedure: String?) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}