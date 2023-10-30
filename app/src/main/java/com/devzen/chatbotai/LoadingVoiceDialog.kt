package com.devzen.chatbotai

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.devzen.chatbotai.R

class LoadingVoiceDialog(context: Context) : Dialog(context){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progressloadingvoice)

        setCancelable(false)


        val runing_animation = findViewById<View>(R.id.homeAnimation2) as LottieAnimationView
        val voicetxt = findViewById<View>(R.id.voicetxt) as TextView

        if (runing_animation != null) {
            runing_animation.setAnimation("loading_sound.json")
            runing_animation.loop(true)
            runing_animation.playAnimation()
        }
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
    fun updateText(text: String) {
        val voicetxt = findViewById<View>(R.id.voicetxt) as TextView
        voicetxt.text = text
    }

}