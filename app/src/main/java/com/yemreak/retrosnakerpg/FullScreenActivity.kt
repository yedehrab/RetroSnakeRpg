package com.yemreak.retrosnakerpg

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View

@SuppressLint("Registered")
abstract class FullScreenActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        immersiveMode()
    }

    private fun immersiveMode(){
        // Navigation Bar'ı kaldırma
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.decorView.setOnSystemUiVisibilityChangeListener { hideSystemUi() }
    }

    private fun hideSystemUi(){
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    // Bu kısım olmazsa tıklandığında navigation bar yine gelir.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            hideSystemUi()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Animasyonlu geçiş için.
        overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast)
    }


}