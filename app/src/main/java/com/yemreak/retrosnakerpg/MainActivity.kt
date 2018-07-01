package com.yemreak.retrosnakerpg

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_ex_settings.*

class MainActivity : FullScreenActivity() { // BİRDEN FAZLA DİALOG AÇILMASIN KONTROL EKLE - Yatay dönmeyi kapat.

    private var fps: Int? = null
    private var numBlockX: Int? = null
    private var lengthSnake: Int? = null

    private val delayMillis: Long = 1000
    private val delayMillisShort: Long = 400

    companion object {
        lateinit var mediaPlayer: MediaPlayer
        var mustMediaPLayerPaused = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        createAndStartSounds()
        bindAnims()
        bindEvents()
    }

    private fun createAndStartSounds() {
        mediaPlayer = MediaPlayer.create(this, R.raw.bg_main)
        mediaPlayer.setVolume(1f, 1f)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    private fun bindAnims() {
        rlMain.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)

        ivExOptions.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)

        btnNewGame.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)
        btnGameMode.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)
        btnOptions.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)
    }

    private fun bindEvents() {
        btnNewGame.setOnClickListener { v: View -> onNewGameClicked(v) }
        btnGameMode.setOnClickListener { v: View -> onGameModeClicked(v) }
        ivExOptions.setOnClickListener { v: View -> onExOptionClicked(v) }
        btnOptions.setOnClickListener { v: View -> onOptionsClicked(v) }
    }

    private fun onNewGameClicked(v: View) {
        mustMediaPLayerPaused = true // Müziğin kapanmasını sağlıyoruz.

        v.isClickable = false // Tekrardan basılmasın diye

        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))

        Handler().postDelayed({
            if (mediaPlayer.isPlaying)
                mediaPlayer.stop()

            val option = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)

            val intent: Intent = when (SnakeEngine.controlMode) {
                SnakeEngine.ControlModes.CONTROL_WITH_BUTTONS -> Intent(this, SnakeActivity1::class.java)
                SnakeEngine.ControlModes.CONTROL_WITH_SWIPE -> Intent(this, SnakeActivity2::class.java)
            }

            intent.putExtra("fps", fps)
            intent.putExtra("lengthSnake", lengthSnake)

            startActivity(intent, option.toBundle())
        }, delayMillisShort)

        Handler().postDelayed({ // Süresi daha uzun oluyor ki arada açık olup tekrar basılmasın.
            v.isClickable = true // İşlem sonrası tekrar basılabilir hale alıyoruz
        }, delayMillis)
    }

    private fun onGameModeClicked(v: View) {
        mustMediaPLayerPaused = false // müziği devamını sağlıyoruz.

        v.isClickable = false // Tekrardan basılmasın diye

        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))

        Handler().postDelayed({
            val option = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            val intent = Intent(this, GameModeActivity::class.java)

            startActivity(intent, option.toBundle())
        }, delayMillisShort)

        Handler().postDelayed({ // Süresi daha uzun oluyor ki arada açık olup tekrar basılmasın.
            v.isClickable = true // İşlem sonrası tekrar basılabilir hale alıyoruz
        }, delayMillis)
    }

    private fun onExOptionClicked(v: View) {
        v.isClickable = false // Tekrardan basılmasın diye

        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotata_indefinitely))

        showExOptionsDialog()

        Handler().postDelayed({ // Süresi daha uzun oluyor ki arada açık olup tekrar basılmasın.
            v.isClickable = true // İşlem sonrası tekrar basılabilir hale alıyoruz
        }, delayMillis)
    }

    private fun showExOptionsDialog() { // DÜZENLE BURAYI; ÇOK AZ, AZ ... ÇOK FAZLA
        val settingsDialogBuilder = AlertDialog.Builder(this)
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val settingsView = layoutInflater.inflate(R.layout.dialog_ex_settings, rl_settings, true)

        with(settingsDialogBuilder) {
            setTitle("Ayarlar")
            setIcon(R.drawable.btn_ex_options)
            setView(settingsView)
            setOnCancelListener { _ -> ivExOptions.clearAnimation() }
        }

        val settingsDialog = settingsDialogBuilder.create()

        val tvLength = settingsView.findViewById<TextView>(R.id.tv_snakeLength)
        val sbLength = settingsView.findViewById<SeekBar>(R.id.sb_length)

        sbLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvLength.text = "Yılan Uzunluğu (${progress + 1})"

                when (SnakeEngine.getGameModes()) {
                    SnakeEngine.GameModes.SHRINK_SNAKE -> Snake.maxLength = progress + 1
                    else -> lengthSnake = progress + 1
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        val tvBlockSize = settingsView.findViewById<TextView>(R.id.tv_blockSize)
        tvBlockSize.text = getString(R.string.txt_size_of_block, getString(BlockField.Sizes.getTextId(BlockField.blockSizeMode)))

        val sbBlockSize = settingsView.findViewById<SeekBar>(R.id.sb_blockSize)
        sbBlockSize.progress = BlockField.Sizes.getIndex(BlockField.blockSizeMode)

        sbBlockSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvBlockSize.text = getString(R.string.txt_size_of_block, getString(BlockField.Sizes.getTextId(progress)))
                BlockField.blockSizeMode = BlockField.Sizes.get(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        val tvFps = settingsView.findViewById<TextView>(R.id.tv_fps)
        val sbFps = settingsView.findViewById<SeekBar>(R.id.sb_fps)

        sbFps.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvFps.text = "Oyun Hızı (${progress + 10})"
                fps = progress + 10
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        with(settingsDialog) {
            window.attributes.windowAnimations = R.style.FadeDialog
            show()
        }

    }

    private fun onOptionsClicked(v: View) {
        mustMediaPLayerPaused = false // müziği devamını sağlıyoruz.

        v.isClickable = false // Tekrardan basılmasın diye

        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))

        Handler().postDelayed({
            val option = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            val intent = Intent(this, OptionsActivity::class.java)

            startActivity(intent, option.toBundle())
        }, delayMillisShort)

        Handler().postDelayed({ // Süresi daha uzun oluyor ki arada açık olup tekrar basılmasın.
            v.isClickable = true // İşlem sonrası tekrar basılabilir hale alıyoruz
        }, delayMillis)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onResume() {
        super.onResume()

        mustMediaPLayerPaused = true

        if (!mediaPlayer.isPlaying)
            mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()

        if (mustMediaPLayerPaused && mediaPlayer.isPlaying)
            mediaPlayer.pause()
    }
}
FWTRHXTR43Z