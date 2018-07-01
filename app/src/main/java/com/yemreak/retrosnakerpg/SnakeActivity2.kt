package com.yemreak.retrosnakerpg

import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_snake2.*

class SnakeActivity2 : SubFullScreenActivity() {

    var isTouchable = true

    // Fark değeri tutacağız
    private val p = Point(0, 0)

    private lateinit var snakeEngine: SnakeEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //immersiveMode(this)

        setContentView(R.layout.activity_snake2)

        snakeEngine = SnakeEngine(this, svFullGameField.holder)

        initVariables()
        bindEvents()
    }

    private fun initVariables() {
        snakeEngine.fps = intent.getIntExtra("fps", 15)
        snakeEngine.lengthSnake = intent.getIntExtra("lengthSnake", 4)
    }

    private fun bindEvents() {
        svFullGameField.holder.addCallback(snakeEngine)
        svFullGameField.setOnTouchListener { _, event -> svFullGameFieldOnTouch(event) } // ------------- Ayarla
    }

    inner class MyDetector: GestureDetector.SimpleOnGestureListener(){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return super.onSingleTapUp(e)
        }
    }

    /**
     * Not: Action_DOWN dokununca
     * Not: Action_UP elini kaldırınca
     */
    private fun svFullGameFieldOnTouch(e: MotionEvent): Boolean {
        if (isTouchable) {
            when (e.pointerCount > 1 ) {
                false -> changeSnakeDirection(e)
                true -> stopSnakeEngine()
            }
        }
        return true
    }

    private fun changeSnakeDirection(e: MotionEvent) {
        when (e.action) {
        // Dokunulduğunda "pressed"
            MotionEvent.ACTION_DOWN -> {
                // İlk konumu alıyoruz
                p.x = e.x.toInt()
                p.y = e.y.toInt()

            }
        // Dokunma bırakıldığında "released
            MotionEvent.ACTION_UP -> {
                // Son konumdan ilk konumu çıkarığ fark değerine atıyoruz.
                p.x = e.x.toInt() - p.x
                p.y = e.y.toInt() - p.y

                val horizontal = when {
                    Math.abs(p.x) > Math.abs(p.y) -> true
                    else -> false
                }

                // Ufak kaydırmalar sayılmasın.
                val sensibility = 40

                when (horizontal) {
                    true -> when {
                        p.x > sensibility -> Snake.newDirection = Snake.Directions.RIGHT
                        p.x < -sensibility -> Snake.newDirection = Snake.Directions.LEFT
                    }
                    false -> when {
                        p.y > sensibility -> Snake.newDirection = Snake.Directions.DOWN
                        p.y < -sensibility -> Snake.newDirection = Snake.Directions.UP
                    }
                }

                // Oyun motorunu başlatma
                snakeEngine.startSensible()
            }
        }
    }

    private fun stopSnakeEngine() {
        isTouchable = false

        snakeEngine.pause()

        Handler().postDelayed({
            isTouchable = true
        }, 300)
    }

    override fun onPause() {
        super.onPause()
        snakeEngine.pause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        MainActivity.mediaPlayer.start()
    }
}
