package com.devzen.chatbotai

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.devzen.chatbotai.R

class LoadingDialog(context: Context) : Dialog(context){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progressloading)
        setCancelable(false)


        val runing_animation = findViewById<View>(R.id.homeAnimation2) as LottieAnimationView

        if (runing_animation != null) {
            runing_animation.setAnimation("robot_loading.json")
            runing_animation.loop(true)
            runing_animation.playAnimation()
        }
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
}