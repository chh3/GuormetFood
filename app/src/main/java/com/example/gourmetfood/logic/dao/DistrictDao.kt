package com.example.gourmetfood.logic.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gourmetfood.logic.data.City
import com.example.gourmetfood.logic.data.District
import com.example.gourmetfood.logic.data.Province
import java.util.*

@Dao
interface DistrictDao {
    @Insert
    fun insertProvince(province: Province)

    @Insert
    fun insertCity(city: City)

    @Insert
    fun insertDistrict(district: District)

    @Query("select name, pinyin from City")
    fun getAllCity(): MutableList<CityAndDistrict>

    @Query("select name, pinyin from District")
    fun getAllDistrict(): MutableList<CityAndDistrict>

    // 只从头到尾匹配 '%' || :query || '%'
    @Query("select name, pinyin from City where name like :query || '%' or pinyin like :query || '%'")
    fun getAllCity(query: String): MutableList<CityAndDistrict>

    @Query("select name, pinyin from District where name like :query || '%' or pinyin like :query || '%'")
    fun getAllDistrict(query: String): MutableList<CityAndDistrict>

    fun getAllCityAndDistrict(): MutableList<CityAndDistrict> {
        return with(getAllCity()) {
            addAll(getAllDistrict())
            this
        }
    }


    fun getAllCityAndDistrict(query: String):MutableList<CityAndDistrict> {
        return with(getAllCity(query)) {
            addAll(getAllDistrict(query))
            this
        }
    }

}

class CityAndDistrict: Comparable<CityAndDistrict>{
    @ColumnInfo
    lateinit var name: String

    @ColumnInfo
    lateinit var pinyin: String

    override fun compareTo(other: CityAndDistrict): Int {
        return pinyin.first().compareTo(other.pinyin.first())
        // return pinyin.substring(0, 1).compareTo(other.pinyin.substring(0, 1))
    }

    public fun getFirst():String {
        return pinyin.substring(0, 1).toUpperCase(Locale.ROOT)
    }

}