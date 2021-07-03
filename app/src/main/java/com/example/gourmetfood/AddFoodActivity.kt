package com.example.gourmetfood

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gourmetfood.logic.data.AppDatabase
import com.example.gourmetfood.logic.data.Food
import com.example.gourmetfood.util.ImageUtil


class AddFoodActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "tested"
        const val REQUEST_CODE_SELECT_PIC = 120
        private const val RESULT_ADD = 0
    }

    private val food:Food = Food("", "", "", null, "", null)
    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar_add) }
    private val addImage by lazy { findViewById<ImageView>(R.id.image_add) }
    private val editLocation by lazy { findViewById<TextView>(R.id.location_edit) }

    private val editName: EditText by lazy {findViewById(R.id.edit_name)}
    private val editDescribe: EditText by lazy {findViewById(R.id.edit_describe)}
    private val editHistory: EditText by lazy {findViewById(R.id.edit_history)}
    private val editStep: EditText by lazy {findViewById(R.id.edit_step)}
    private val changeButton: Button by lazy { findViewById(R.id.location_button) }
    private val addButton: Button by lazy { findViewById(R.id.button_add) }

    private var currentCity = ""

    private lateinit var startActivityLaunch: ActivityResultLauncher<String>
    private lateinit var selectActivityLaunch: ActivityResultLauncher<Intent>
    private var myBitMap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        setSupportActionBar(toolbar)
        with(supportActionBar) {
            if (this != null) {
                // setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(true)//添加默认的返回图标
                // 需要在返回按钮中注册父activity
                setHomeButtonEnabled(true)//设置返回键可用
                title = "添加美食"
            }

        }

        currentCity = intent.getStringExtra("location")?:""
        editLocation.text = currentCity

        try {
            // 需要把注册写在这里
            startActivityLaunch = registerForActivityResult(ActivityResultContracts.GetContent()){
                Log.d(TAG,"选择回来后")
                try {
                    val  inputStream = contentResolver.openInputStream(it);
                    //从输入流中解码位图
                    myBitMap = BitmapFactory.decodeStream(inputStream);
                    //保存位图
                    addImage.setImageBitmap(myBitMap)
                    inputStream?.close()
                } catch (e: NullPointerException) {
                    Log.e(TAG, e.toString())
                    Toast.makeText(this, "未选择任何图片!", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "exception: ${e.toString()}")
        }

        selectActivityLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            currentCity = it.data?.getStringExtra("location") ?:currentCity
            editLocation.text = currentCity
        }

        // 点击添加图片
        addImage.setOnClickListener(View.OnClickListener {
            try {
                startActivityLaunch.launch("image/*")
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            }

        })

        changeButton.setOnClickListener {
            changeDistrict(it)
        }

        addButton.setOnClickListener{
            returnFood(it)
        }
    }

    override  fun onBackPressed() {
        Log.d(TAG, "back")
        val intent = Intent()
        setResult(RESULT_ADD, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fillFood(): Boolean {
        if (myBitMap == null || editName.text == null || editDescribe.text == null
            || editName.text.toString().trim() == "" || editDescribe.text.toString().trim() == "") {
            return false
        }
        food.image = ImageUtil.saveBitMap(this, myBitMap!!)
        food.name = editName.text.toString()
        food.describe = editDescribe.text.toString()
        food.location = currentCity
        food.history = editHistory.text?.toString()
        food.procedure = editStep.text?.toString()
        return true
    }

    private fun returnFood(view: View) {
        // 首先判断是否该填的都填了,必须要有名字和地点和图片
        if (!fillFood()) {
            Toast.makeText(applicationContext, "请填完必填项", Toast.LENGTH_SHORT).show()
        } else {
            val foodDao = AppDatabase.getDatabase(FoodApplication.context).foodDao()
            foodDao.insertFood(food)
            onBackPressed()
        }
    }

    private fun changeDistrict(view: View) {
        val intent = Intent(this, DistrictActivity::class.java)
        intent.putExtra("location", currentCity)
        selectActivityLaunch.launch(intent)
    }
}