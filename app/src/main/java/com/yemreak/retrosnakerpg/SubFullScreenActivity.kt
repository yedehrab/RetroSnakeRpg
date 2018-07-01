package com.yemreak.retrosnakerpg

import android.annotation.SuppressLint
import android.app.Activity

@SuppressLint("Registered")
abstract class SubFullScreenActivity: FullScreenActivity() {

    val delayMillis: Long = 400
    val delayMillisShort: Long = 250


    override fun onBackPressed() {
        super.onBackPressed()
        // Animasyonlu geçiş için.
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onPause() {
        super.onPause()

        if(MainActivity.mediaPlayer.isPlaying)
            MainActivity.mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()

        if(!MainActivity.mediaPlayer.isPlaying)
            MainActivity.mediaPlayer.start()
    }
}