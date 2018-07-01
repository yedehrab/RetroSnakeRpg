package com.yemreak.retrosnakerpg

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_snake1.*

class SnakeActivity1 : SubFullScreenActivity() {
    private lateinit var snakeEngine: SnakeEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //immersiveMode(this)
        setContentView(R.layout.activity_snake1)

        snakeEngine = SnakeEngine(this, svGameField.holder)

        initVariables()
        bindEvents()
    }

    private fun initVariables() {
        snakeEngine.fps = intent.getIntExtra("fps", 15)
        snakeEngine.lengthSnake = intent.getIntExtra("lengthSnake", 4)
    }

    private fun bindEvents() {
        // Eğer surface değişirse haberimiz olacak
        svGameField.holder.addCallback(snakeEngine)
        svGameField.setOnClickListener{ snakeEngine.pauseOrStart() }

        ibLeft.setOnClickListener{v: View -> onButtonClick(v)}
        ibUp.setOnClickListener{v: View -> onButtonClick(v)}
        ibDown.setOnClickListener{v: View -> onButtonClick(v)}
        ibRight.setOnClickListener{v: View -> onButtonClick(v)}
    }

    private fun onButtonClick(v: View) {
        snakeEngine.startSensible()

        when (v.id) {
            R.id.ibLeft -> Snake.newDirection = Snake.Directions.LEFT
            R.id.ibRight -> Snake.newDirection = Snake.Directions.RIGHT
            R.id.ibUp -> Snake.newDirection = Snake.Directions.UP
            R.id.ibDown -> Snake.newDirection = Snake.Directions.DOWN
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        MainActivity.mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        snakeEngine.pause()
    }
}
