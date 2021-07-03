package com.example.gourmetfood.ui

import android.app.Dialog
import android.content.Context
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.example.gourmetfood.R

class Loading(context: Context): Dialog(context) {

    init {
        setContentView(R.layout.loading_dialog)
        // 设置旋转动画
        val circleAim = AnimationUtils.loadAnimation(context, R.anim.anim_round_rotate)
        circleAim.interpolator = LinearInterpolator()
        val imageView = findViewById<ImageView>(R.id.refresh_image)
        imageView.animation = circleAim
    }
}


// 参考https://blog.csdn.net/qq_42865331/article/details/103099700