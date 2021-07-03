package com.example.gourmetfood.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.gourmetfood.AddFoodActivity
import com.example.gourmetfood.FoodApplication
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


object ImageUtil {
    private const val TAG = "ImageUtil_tested"

    fun saveBitMap(context: Context, bitmap: Bitmap): String {

        val dirPath = context.filesDir
            .absolutePath + "/database"// data/data目录
        try {
            val dir = File(dirPath)
            if (!dir.exists()) {
                // dir.mkdirs() // 会把不存在的父目录也给建了
                dir.mkdir()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        val time: String = SimpleDateFormat("yyyyMMddHHmmSS", Locale.CHINA).format(Date())
        val fileName = "image_$time"
        var path = "${context.filesDir
            .absolutePath}/database/${fileName}.jpeg"
        // 根据时间生成一个唯一的路径
        val file = File(path)

        try {
            if (file.exists()) {
                file.deleteOnExit()
            }
            //如果文件不存在，则创建文件
            if(!file.exists()){
                file.createNewFile();
            }
            //输出流
            val out = FileOutputStream(file);
            /** mBitmap.compress 压缩图片
             *
             *  Bitmap.CompressFormat.PNG   图片的格式
             *   100  图片的质量（0-100）
             *   out  文件输出流
             */
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(context,file.absolutePath.toString(),Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.toString())
            path = ""
        }
        catch (e: IOException) {
            Log.e(TAG, e.toString())
            path = ""
        } finally {
            return path
        }
    }
}

