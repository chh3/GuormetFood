package com.example.gourmetfood

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gourmetfood.logic.dao.CityAndDistrict
import com.example.gourmetfood.ui.district.DistrictAdapter
import com.example.gourmetfood.ui.district.DistrictViewModel
import com.example.gourmetfood.ui.district.LetterListView
import com.example.gourmetfood.ui.district.ResultAdapter
import com.example.gourmetfood.util.CityUtil
import java.util.*
import kotlin.collections.HashMap


class DistrictActivity : AppCompatActivity() {

    companion object {
        private const val RESULT_DISTRICT = 1
        private const val TAG = "districttested"
    }

    private var isScroll = false // 是否正在滚动城市
    // 还需要一个Map存储每一个首字母对应的第一个城市下标
    private val letterToCity = HashMap<String, Int>()
    private var currentCity = ""

    private val mLetterList by lazy {findViewById<LetterListView>(R.id.letter_list)}
    private val recycleView: RecyclerView by lazy {findViewById(R.id.recycleView)}
    private val showTextView: TextView by lazy { findViewById(R.id.current_show) }
    private val searchView: SearchView by lazy {findViewById(R.id.searchView)}
    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar_district) }
    private val resultList: ArrayList<CityAndDistrict> = ArrayList<CityAndDistrict>()
    private val viewModel by lazy { ViewModelProvider(this).get(DistrictViewModel::class.java)}
    private val nothingText by lazy { findViewById<TextView>(R.id.nothing_text) }
    private val districtRResult by lazy { findViewById<RecyclerView>(R.id.district_result) }
    private val constraintLayout by lazy { findViewById<ConstraintLayout>(R.id.show_district_layout) }
    private var clickLetter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_district)

        val upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material)
        if (upArrow != null) {
            CityUtil.setColorFilter(upArrow, ContextCompat.getColor(this, R.color.white))
        }
        setSupportActionBar(toolbar)
        with(supportActionBar) {
            if (this != null) {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(true)//添加默认的返回图标
                // 需要在返回按钮中注册父activity
                setHomeButtonEnabled(true)//设置返回键可用
                setHomeAsUpIndicator(upArrow)
            }
        }

        viewModel.getAllDistricts()


        currentCity = intent.getStringExtra("location")?:""
        showTextView.text = currentCity

        districtRResult.layoutManager = LinearLayoutManager(this)

        districtRResult.adapter = ResultAdapter(resultList, object: ResultAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                currentCity = resultList[position].name
                onBackPressed()
            }
        })

        recycleView.layoutManager = LinearLayoutManager(this)

        recycleView.adapter = DistrictAdapter(viewModel.districtList, letterToCity,
            object: DistrictAdapter.onItemClickListener{
                override fun onItemClick(view: View, position: Int) {
                    currentCity = viewModel.districtList[position].name
                    onBackPressed()
                }
            }
        )
        recycleView.addOnScrollListener(MRecycleScrollListener())


        mLetterList.setOnLetterSelectListener(MLetterSelectListener())


        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

            // 点击搜索按钮时触发
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            // 搜索内容改变时触发
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "CHANGE: $newText")
                if (newText != null && newText != "") {
                    viewModel.searchDistrict(newText)

                } else {
                    constraintLayout.visibility = View.VISIBLE
                    nothingText.visibility = View.GONE
                    districtRResult.visibility = View.GONE
                }
                return true
            }
        })

        viewModel.resultLiveData.observe(this, {
           result ->
            val district = result.getOrNull()
            if (district != null) {
                resultList.clear()
                resultList.addAll(district)
                (districtRResult.adapter as ResultAdapter).notifyDataSetChanged()
                districtRResult.visibility = View.VISIBLE
                constraintLayout.visibility = View.GONE
                nothingText.visibility = View.GONE
                if (district.size == 0) {
                    nothingText.visibility = View.VISIBLE
                }
            } else {
                Log.d(TAG, "NOTHING!")
            }
       })

       viewModel.districtListLiveData.observe(this, {
           result ->
           val district = result.getOrNull()
           if (district != null) {
               viewModel.districtList.clear()
               viewModel.districtList.addAll(district)
               viewModel.districtList.sort()
               (recycleView.adapter as DistrictAdapter).notifyDataSetChanged()

               if (viewModel.districtList.size > 0) {
                   var firstLetter = viewModel.districtList[0].getFirst()
                   var nextLetter: String
                   letterToCity[firstLetter] = 0
                   for(range in 1 until viewModel.districtList.size - 1) {
                       nextLetter = viewModel.districtList[range].getFirst()
                       if (nextLetter != firstLetter) {
                           letterToCity[nextLetter] = range
                           // Log.d(TAG, "$nextLetter: $range")
                           firstLetter = nextLetter
                       }
                   }
               }

           } else {
               Log.d(TAG, "NOTHING!")
           }
       })
    }
    //    //因为在关闭该活动时要返回数据，所以重写onBackPressd方法
    public override  fun onBackPressed() {
        //新建一个intent对象用于封装返回的数据
        Log.d(TAG, "district back")
        val intent = Intent()
        intent.putExtra("location",currentCity)
        //向上一个活动返回处理结果和intent
        setResult(RESULT_DISTRICT, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }



    inner class MRecycleScrollListener:  RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                // 正在拖拽
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    isScroll = true
                }
                /*滑动停止*/
                RecyclerView.SCROLL_STATE_IDLE -> {
                    isScroll = false
                }
                /*惯性滑动中*/
                RecyclerView.SCROLL_STATE_SETTLING -> {
                    isScroll = true
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!isScroll || clickLetter) {
                return
            }
            // 如果是点击字母或者滑动字母触发的滑动事件则不用改变choose才对，否则会有问题

            // dx和dy为x轴和y轴方向上的偏移量
            // 根据dx和dy要知道第一个能看到的城市的首字母信息，根据这个来指定选择的字母
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            // 找到第一个可见的item
            // Log.e("TAG", " PersonalHomepageActivity " + firstItemPosition);
            val adapter = recyclerView.adapter as DistrictAdapter
            val letter: String = adapter.districtList[firstItemPosition].pinyin.substring(0, 1).toUpperCase(
                Locale.ROOT
            )
            mLetterList.setChoose(letter[0] - 'A') // 如果是这样，那么choose就不能这么算了
        }
    }

    inner class MLetterSelectListener: LetterListView.OnLetterSelectListener {
        override fun letterSelect(letter: String) {
            // (recycleView.adapter as DistrictAdapter).notifyDataSetChanged()
            val select = letterToCity[letter]
            Log.d("111", select.toString())
            if (select != null) {
                clickLetter = true
                recycleView.scrollToPosition(select)
                val layoutManager = recycleView.layoutManager as LinearLayoutManager
                layoutManager.scrollToPositionWithOffset(select, 0)
                clickLetter = false
            }
            // 可以把没有的首字母去掉
        }
    }
}