package com.example.gourmetfood.logic.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gourmetfood.logic.dao.DistrictDao
import com.example.gourmetfood.logic.dao.FoodDao

@Database(version = 1, entities = [District::class, City::class, Province::class, Food::class], exportSchema = false) // 暂时不export
abstract class AppDatabase: RoomDatabase() {

    abstract fun districtDao():DistrictDao
    abstract fun foodDao(): FoodDao

    companion object {
        private var instance:AppDatabase? = null
        @Synchronized
        fun getDatabase(context: Context):AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "app_database")
                .fallbackToDestructiveMigration() // 测试阶段如此而已
                .allowMainThreadQueries() // 仅在测试环境如此
                .build().apply {
                    instance = this
                }
        }
    }
}