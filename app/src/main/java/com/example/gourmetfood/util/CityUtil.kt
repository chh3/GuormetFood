package com.example.gourmetfood.util

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.annotation.ColorInt
import com.example.gourmetfood.logic.dao.DistrictResponse
import com.example.gourmetfood.logic.data.AppDatabase
import com.example.gourmetfood.logic.data.City
import com.example.gourmetfood.logic.data.District
import com.example.gourmetfood.logic.data.Province
import com.google.gson.Gson
import opensource.jpinyin.PinyinFormat
import opensource.jpinyin.PinyinHelper
import java.io.BufferedReader
import java.io.InputStreamReader


object CityUtil {
    fun getPinYin(name: String):String {
        val separator=""
        // 各汉字之间的分隔符
        return PinyinHelper.convertToPinyinString(name, separator, PinyinFormat.WITHOUT_TONE)
    }

    fun initCity(context: Context)
    {
        /** 资源分为两种:
         *  第一种:res目录下的资源(该资源不会被编译,但是会生成id)
         *  第二种:Assets文件夹下的资源文件,又叫原始资源文件(不会被编译,也不会生成id)
         */
        val districtDao = AppDatabase.getDatabase(context).districtDao()
        // val assetManager = assets
        try {
            val bufferReader = BufferedReader(InputStreamReader(context.assets.open("location.json")))
            val jsonString = with(StringBuilder()) {
                var next = bufferReader.readLine()
                while (next != null) {
                    append(next)
                    next = bufferReader.readLine()
                }
                toString().trim()
            }
            val gson: Gson = Gson()
            val fromJson: DistrictResponse = gson.fromJson(
                jsonString,
                DistrictResponse::class.java
            )
            for (province in fromJson.districts[0].districts) {
                districtDao.insertProvince(Province(province.name))
                for (city in province.districts) {
                    districtDao.insertCity(
                        City(
                            city.name,
                            getPinYin(city.name),
                            province.name
                        )
                    )
                    for(district in city.districts) {
                        districtDao.insertDistrict(
                            District(
                                district.name, getPinYin(
                                    district.name
                                ), city.name
                            )
                        )
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("tested", e.toString())
        }
    }

    fun setColorFilter(drawable: Drawable, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
}