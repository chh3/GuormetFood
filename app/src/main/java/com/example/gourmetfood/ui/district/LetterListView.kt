package com.example.gourmetfood.ui.district

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.gourmetfood.R

class LetterListView(context: Context, attrs: AttributeSet): View(context, attrs) {

    // 用于判断是否点击了view，可以实现点击背景变色
    private var clickBackground = false

    //每个字母所在矩形的宽和高
    private var mViewWidth = 0
    private var mViewHeight = 0


    // 城市都存在拼音，所以不需要#，事实上，只要知道首字母即可
    companion object {
        private val letterList = listOf( "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
        // 城市没有I、U、V未首字母开头的，但需要，方便计算坐标
    }

    private val mPaint = Paint()
    private var mChoose = -1
    private lateinit var  mOnLetterSelectListener: OnLetterSelectListener

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val singleHeight = mViewHeight / letterList.size
        for (i in letterList.indices) {
            mPaint.color = Color.BLACK
            mPaint.typeface = Typeface.DEFAULT_BOLD
            mPaint.textSize = singleHeight / 1.5F
            mPaint.isAntiAlias = true
            if (i == mChoose) {
                mPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
                // 让字母占字母所在方格的2/3
                mPaint.textSize = singleHeight * 1.5F;
                mPaint.isFakeBoldText = true;
            }
            val text = letterList[i]
            val bound = Rect()
            mPaint.getTextBounds(text, 0, text.length, bound)
            // 计算应该绘制的坐标
            val x = mViewWidth / 2.0F - mPaint.measureText(text) / 2.0F
            val y = singleHeight / 2.0F + singleHeight * i + bound.height() / 2.0F
            canvas?.drawText(text, x, y, mPaint)
            mPaint.reset()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    public fun setChoose(choose: Int) {
        if (mChoose == choose) {
            return
        }
        mChoose = choose
        invalidate()
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        /*获取当前 View的测量模式*/
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val size = MeasureSpec.getSize(widthMeasureSpec)
        mViewWidth = if (mode == MeasureSpec.EXACTLY) {
            size
        } else {
            /*否则的话我们就需要结合padding的值来确定*/
            val desire = size + paddingLeft + paddingRight
            if (mode == MeasureSpec.AT_MOST) {
                desire.coerceAtMost(size)
            } else {
                desire
            }
        }
        return mViewWidth
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        /*获取当前 View的测量模式*/
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val size = MeasureSpec.getSize(heightMeasureSpec)
        // mViewWidth = width
        mViewHeight =  if (mode == MeasureSpec.EXACTLY) {
            size
        } else {
            /*否则的话我们就需要结合padding的值来确定*/
            val desire = size + paddingLeft + paddingRight
            if (mode == MeasureSpec.AT_MOST) {
                desire.coerceAtMost(size)
            } else {
                desire
            }
        }
        return mViewHeight
    }

    /**
     * 用于处理事件，返回值决定当前空间是否消费了这个事件，返回true表示自己要后处理
     * 返回false表示自己不处理交给父类进行处理
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    /**
     * 处理触摸事件分发,事件(多数情况)是从dispatchTouchEvent开始的。
     * 执行super.dispatchTouchEvent(ev)，事件向下分发。
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val currentTouch = (event.y / height * letterList.size).toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                clickBackground = true
                if (currentTouch >= 0
                    && currentTouch < letterList.size) {
                    if (currentTouch != mChoose) {
                        mOnLetterSelectListener.letterSelect(letterList[currentTouch])
                    }
                    mChoose = currentTouch
                }
            }

            MotionEvent.ACTION_UP -> {
                clickBackground = false
                // mChoose = -1
            }

            MotionEvent.ACTION_MOVE -> {
                if (currentTouch >= 0
                    && currentTouch < letterList.size) {
                    if (currentTouch != mChoose) {
                        mOnLetterSelectListener.letterSelect(letterList[currentTouch])
                    }
                    mChoose = currentTouch
                }
            }

        }
        invalidate()
        return true
    }

    // 监听字母的变化
    public interface OnLetterSelectListener {
        fun letterSelect(letter: String)
    }

    public fun getOnLetterSelectListener():OnLetterSelectListener{
        return mOnLetterSelectListener
    }

    public fun setOnLetterSelectListener(onLetterSelectListener: OnLetterSelectListener) {
        mOnLetterSelectListener = onLetterSelectListener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }


}


/**
 * 参考 https://www.jb51.net/article/101484.htm
 */