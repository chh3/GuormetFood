// ┏┓　　　┏┓
// ┏┛┻━━━┛┻┓
// ┃　　　　　　　┃ 　
// ┃　　　━　　　┃
// ┃　┳┛　┗┳　┃
// ┃　　　　　　　┃
// ┃　　　┻　　　┃
// ┃　　　　　　　┃
// ┗━┓　　　┏━┛
// ┃　　　┃ 神兽保佑　　　　　　　　
// ┃　　　┃ 代码无BUG！
// ┃　　　┗━━━┓
// ┃　　　　　　　┣┓
// ┃　　　　　　　┏┛
// ┗┓┓┏━┳┓┏┛
// ┃┫┫　┃┫┫
// ┗┻┛　┗┻┛

package com.example.gourmetfood

import android.content.*
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gourmetfood.logic.dao.DistrictResponse
import com.example.gourmetfood.logic.data.AppDatabase
import com.example.gourmetfood.logic.data.City
import com.example.gourmetfood.logic.data.District
import com.example.gourmetfood.logic.data.Province
import com.example.gourmetfood.ui.Loading
import com.example.gourmetfood.ui.food.FoodAdapter
import com.example.gourmetfood.ui.food.FoodResultAdapter
import com.example.gourmetfood.ui.food.FoodViewModel
import com.example.gourmetfood.util.CityUtil
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "tested"
        private const val ADD_RESULT_CODE = 0
        private const val DISTRICT_RESULT_CODE = 1
        private const val SHOW_RESULT_CODE = 3
    }

    lateinit var toolbar: Toolbar

    var currentCity = "博白县" // 还要保存 当前选择好的城市

    private val viewModel by lazy { ViewModelProvider(this).get(FoodViewModel::class.java)}
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.food_recyclerView) }
    private val addFood by lazy { findViewById<TextView>(R.id.addFood) }
    private val districtView by lazy { findViewById<TextView>(R.id.district) }
    private val searchView by lazy { findViewById<SearchView>(R.id.search_food) }
    private val nothingText by lazy { findViewById<TextView>(R.id.nothing_text) }
    private val resultRecyclerView by lazy { findViewById<RecyclerView>(R.id.food_result_recyclerView) }
    private lateinit var startActivityLaunch: ActivityResultLauncher<Intent>
    private val noResultText by lazy {findViewById<TextView>(R.id.no_result_text)}
    private val loading by lazy { Loading(this) }
    // 需要放到这里

    lateinit var initBinder: InitService.InitBinder
    private val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            initBinder = service as InitService.InitBinder
            Log.d(TAG, "CONNECT")
            initBinder.setInit()
            val intent = Intent(this@MainActivity, DistrictActivity::class.java)
            intent.putExtra("location", currentCity)
            Log.d(TAG, "launch")
            startActivityLaunch.launch(intent)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // service销毁时 自动解绑

        }
    }


    private val sharedPrefFile = "com.example.food"
    private val mPreferences: SharedPreferences by lazy {
        getSharedPreferences(
            sharedPrefFile,
            MODE_PRIVATE
        ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        loading.show()

        recyclerView.layoutManager = LinearLayoutManager(this)
        resultRecyclerView.layoutManager = LinearLayoutManager(this)

        val that = this
        recyclerView.adapter = FoodAdapter(
            viewModel.foodList,
            object : FoodAdapter.OnItemShowListener {
                override fun onShow(position: Int) {
                    val intent = Intent(that, ShowFoodActivity::class.java)
                    intent.putExtra("id", viewModel.foodList[position].id)
                    Log.d(TAG, "launch")
                    startActivityLaunch.launch(intent)
                }
            },
            object : FoodAdapter.OnItemDeleteListener {
                override fun onDelete(position: Int) {
                    thread {
                        val foodDao = AppDatabase.getDatabase(FoodApplication.context).foodDao()
                        foodDao.deleteFood(viewModel.foodList[position])
                    }
                    viewModel.searchLocationFood(currentCity)
                }
            })

        resultRecyclerView.adapter = FoodResultAdapter(
            viewModel.resultList,
            object : FoodResultAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    val intent = Intent(that, ShowFoodActivity::class.java)
                    intent.putExtra("id", viewModel.resultList[position].id)
                    Log.d(TAG, position.toString())
                    Log.d(TAG, viewModel.resultList[position].id.toString())
                    startActivityLaunch.launch(intent)
                }
            })


        // viewModel.currentLocation.value = mPreferences.getString("currentCity", currentCity)
        currentCity = mPreferences.getString("currentCity", currentCity)!!
        districtView.text = currentCity
        putCurrent()
        viewModel.searchLocationFood(currentCity)

        try {
            // 需要把注册写在这里
            startActivityLaunch =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    // Log.d(TAG, "activityResult -> $it")
                    // acitivity返回时
                    resultRecyclerView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    loading.hide()
                    when(it.resultCode) {
                        ADD_RESULT_CODE -> {
                            viewModel.searchLocationFood(currentCity)
                        }
                        DISTRICT_RESULT_CODE -> {
                            currentCity = it.data?.getStringExtra("location") ?: currentCity
                            districtView.text = currentCity
                            putCurrent()
                            viewModel.searchLocationFood(currentCity)
                        }
                        SHOW_RESULT_CODE -> {
                            viewModel.searchLocationFood(currentCity)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.d(TAG, "exception: ${e.toString()}")
        }

        addFood.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            intent.putExtra("location", currentCity)
            startActivityLaunch.launch(intent)
        }

        districtView.setOnClickListener {
            init()
        }

        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            // 点击搜索按钮时触发
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            // 搜索内容改变时触发
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "CHANGE: $newText")
                if (newText != null && newText != "") {
                    viewModel.searchFood(newText)
                    resultRecyclerView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    resultRecyclerView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    // constraintLayout.visibility = View.VISIBLE
                    nothingText.visibility = View.GONE
                    // districtRResult.visibility = View.GONE
                }
                return true
            }
        })

        viewModel.foodLiveData.observe(this, Observer { result ->
            val food = result.getOrNull()
            if (food != null) {
                // 将recycleView设为可见
                noResultText.visibility = View.GONE
                nothingText.visibility = View.GONE
                viewModel.resultList.clear()
                viewModel.resultList.addAll(food)
                (resultRecyclerView.adapter as FoodResultAdapter).notifyDataSetChanged()
                if (viewModel.foodList.size == 0) {
                    Toast.makeText(this, "未查询到任何美食数据", Toast.LENGTH_SHORT).show()
                    noResultText.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(this, "未查询到任何美食数据", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
                noResultText.visibility = View.VISIBLE
            }
        })

        viewModel.locationFoodLiveData.observe(this, Observer { result ->
            val food = result.getOrNull()
            if (food != null) {
                // 将recycleView设为可见
                nothingText.visibility = View.GONE
                noResultText.visibility = View.GONE
                viewModel.foodList.clear()
                viewModel.foodList.addAll(food)
                recyclerView.adapter?.notifyDataSetChanged()
                if (viewModel.foodList.size == 0) {
                    Toast.makeText(this, "未查询到任何美食数据", Toast.LENGTH_SHORT).show()
                    nothingText.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(this, "未查询到任何美食数据", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
                nothingText.visibility = View.VISIBLE
            }
        })
    }

    private fun init() {
        loading.show()
        val init = mPreferences.getBoolean("init", false)
        if (!init) {
            val preferencesEditor = mPreferences.edit()
            preferencesEditor.putBoolean("init", true)
            Log.d(TAG, viewModel.init.toString())
            preferencesEditor.apply()
            val intent = Intent(this, InitService:: class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        } else {
            val intent = Intent(this, DistrictActivity::class.java)
            intent.putExtra("location", currentCity)
            Log.d(TAG, "launch")
            startActivityLaunch.launch(intent)
        }
    }

    private fun putCurrent() {
        val preferencesEditor = mPreferences.edit()
        preferencesEditor.putString("currentCity", currentCity)
        preferencesEditor.apply()
    }
}
