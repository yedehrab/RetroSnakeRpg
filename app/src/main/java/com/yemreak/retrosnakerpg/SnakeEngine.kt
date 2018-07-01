package com.yemreak.retrosnakerpg

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import com.yemreak.retrosnakerpg.SnakeEngine.ControlModes.*
import com.yemreak.retrosnakerpg.SnakeEngine.MusicModes.*
import com.yemreak.retrosnakerpg.SnakeEngine.SoundsModes.*
import com.yemreak.retrosnakerpg.SnakeEngine.ThemeModes.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class SnakeEngine(private val context: Context, private val holder: SurfaceHolder) : SurfaceHolder.Callback, Runnable {

    /**
     * Ses modu çeşitleri
     * Not: Veriler değiştirilirse alttakilerin de değişmesi lazımdır.
     * @see R.layout.dialog_sounds Aynı sırada olmak zorundalar
     * @see setSoundsMode Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see getSoundsModeIndex Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see CustomDialogInfos.toggleButtonIds Aynı sırada ve sayıda olmak zorundalar.
     */
    enum class SoundsModes {
        NO_SOUNDS,
        CLASSIC_SOUNDS,
        RETRO_SOUNDS,
        GUN_SOUNDS,
        HORROR_SOUNDS
    }

    /**
     * Muzik modu çeşitleri
     * Not: Veriler değiştirilirse alttakilerin de değişmesi lazımdır.
     * @see R.layout.dialog_musics Aynı sırada olmak zorundalar
     * @see setMusicMode Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see getMusicModeIndex Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see CustomDialogInfos.toggleButtonIds Aynı sırada ve sayıda olmak zorundalar.
     */
    enum class MusicModes {
        NO_MUSICS,
        FERAMBIE,
        HORROR_MUSIC
    }

    /**
     * Control modu çeşitleri
     * Not: Veriler değiştirilirse alttakilerin de değişmesi lazımdır.
     * @see R.layout.dialog_controls Aynı sırada olmak zorundalar
     * @see setControlMode Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see getControlModeIndex Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see CustomDialogInfos.toggleButtonIds Aynı sırada ve sayıda olmak zorundalar.
     */
    enum class ControlModes {
        CONTROL_WITH_BUTTONS,
        CONTROL_WITH_SWIPE
    }

    /**
     * Tema modu çeşitleri
     * Not: Veriler değiştirilirse alttakilerin de değişmesi lazımdır.
     * @see R.layout.dialog_themes Aynı sırada olmak zorundalar
     * @see setThemeMode Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see getThemeModeIndex Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see CustomDialogInfos.toggleButtonIds Aynı sırada ve sayıda olmak zorundalar.
     */
    enum class ThemeModes {
        DEFAULT,
        DARKNESS,
        LIGHT,
        AQUA,
        PURPLE,
        HORROR_THEME
    }

    /**
     * Oyun modu çeşitleri
     * Not: Veriler değiştirilirse alttakilerin de değişmesi lazımdır.
     * @see R.layout.dialog_classic_modes Aynı sırada olmak zorundalar
     * @see setGameMode Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see getGameModeIndexes Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see CustomDialogInfos.toggleButtonIds Aynı sırada ve sayıda olmak zorundalar.
     *
     * @sample FRAME Oyun dışındaki çerçeveyi kaldırır. Kenarlara çarpılmaz.
     * @see surfaceCreated
     * @see run
     *
     * @sample MORE_FEED_TYPES Birden fazla yem tipleri olur. 1, 2, 5, 10, 25 puanlık yemler.
     * @see Feed.spawnActTo
     *
     * @sample MORE_FEED Birden fazla yem olur.
     * @see initFeeds
     *
     * @sample SHRINK_SNAKE Yılan yem yedikçe büyümek yerine küçülür.
     * @see initVariables
     *
     */
    enum class GameModes {
        FRAME,
        MORE_FEED_TYPES,
        MORE_FEED,
        SHRINK_SNAKE
    }

    companion object {

        var soundsMode = HORROR_SOUNDS
            private set(value) {
                field = value
            }

        var musicMode = HORROR_MUSIC
            private set(value) {
                field = value
            }

        var controlMode = CONTROL_WITH_SWIPE
            private set(value) {
                field = value
            }

        var themeMode = HORROR_THEME
            private set(value) {
                field = value
            }

        /**
         * Oyun modları
         * @see initVariables
         * @see Feed.spawnActTo
         * @see Snake.eatFeedActTo
         */
        private var gameModes = arrayListOf<GameModes>()

        fun getSoundsModeIndex(): Int {
            return when (soundsMode) {
                NO_SOUNDS -> 0
                CLASSIC_SOUNDS -> 1
                RETRO_SOUNDS -> 2
                GUN_SOUNDS -> 3
                HORROR_SOUNDS -> 4
            }
        }

        fun setSoundsMode(mode: Int) {
            soundsMode = when (mode) {
                0 -> NO_SOUNDS
                1 -> CLASSIC_SOUNDS
                2 -> RETRO_SOUNDS
                3 -> GUN_SOUNDS
                4 -> HORROR_SOUNDS
                else -> {
                    Log.e("Ses ayarlama hatası",
                            "Ses modu ayarlanamadı. Hatalı mod değeri girildi. Lütfen sıralamanın aynı olmasına dikkat et. " +
                                    "\"SnakeEngine.SoundsModes, OptionActivity.showSoundsDialogs\" (SnakeEngine.setSoundsMode)")

                    CLASSIC_SOUNDS
                }
            }
        }

        fun getMusicModeIndex(): Int {
            return when (musicMode) {
                NO_MUSICS -> 0
                FERAMBIE -> 1
                HORROR_MUSIC -> 2
            }
        }

        fun setMusicMode(mode: Int) {
            musicMode = when (mode) {
                0 -> NO_MUSICS
                1 -> FERAMBIE
                2 -> HORROR_MUSIC
                else -> {
                    Log.e("Müzik ayarlama hatası",
                            "Müzik modu ayarlanamadı. Hatalı mod değeri girildi. Lütfen sıralamanın aynı olmasına dikkat et. " +
                                    "\"SnakeEngine.MusicModes, OptionActivity.showMusicsDialog\" (SnakeEngine.setMusicMode)")

                    NO_MUSICS
                }
            }
        }

        fun getThemeModeIndex(): Int {
            return when (themeMode) {
                DEFAULT -> 0
                DARKNESS -> 1
                LIGHT -> 2
                AQUA -> 3
                PURPLE -> 4
                HORROR_THEME -> 5
            }
        }

        fun setThemeMode(mode: Int) {
            themeMode = when (mode) {
                0 -> DEFAULT
                1 -> DARKNESS
                2 -> LIGHT
                3 -> AQUA
                4 -> PURPLE
                5 -> HORROR_THEME
                else -> {
                    Log.e("Tema hatası",
                            "Tema ayarlanamadı. Hatalı mod değeri girildi. Lütfen sıralamanın aynı olmasına dikkat et. " +
                                    "\"SnakeEngine.ThemeModes, OptionActivity.showThemeDialog\" (SnakeEngine.setThemeMode)")

                    DEFAULT
                }
            }
        }

        fun getControlModeIndex(): Int {
            return when (controlMode) {
                CONTROL_WITH_BUTTONS -> 0
                CONTROL_WITH_SWIPE -> 1
            }
        }

        fun setControlMode(mode: Int) {
            controlMode = when (mode) {
                0 -> CONTROL_WITH_BUTTONS
                1 -> CONTROL_WITH_SWIPE
                else -> {
                    Log.e("Kontrol hatası",
                            "Kontrol ayarlanamadı. Hatalı mod değeri girildi. Lütfen sıralamanın aynı olmasına dikkat et. " +
                                    "\"SnakeEngine.ControlMode, CustomDialogActivity.setMode \" (SnakeEngine.setControlMode)")

                    CONTROL_WITH_SWIPE
                }
            }
        }


        fun getGameModes() = gameModes
        fun getGameModeIndexes(): ArrayList<Int> {
            val indexes = arrayListOf<Int>()

            if (gameModes.contains(GameModes.FRAME)) indexes.add(0)
            if (gameModes.contains(GameModes.MORE_FEED_TYPES)) indexes.add(1)
            if (gameModes.contains(GameModes.MORE_FEED)) indexes.add(2)
            if (gameModes.contains(GameModes.SHRINK_SNAKE)) indexes.add(3)

            return indexes
        }

        fun setGameMode(mode: Int) {
            when (mode) {
                0 -> changeGameMode(GameModes.FRAME)
                1 -> changeGameMode(GameModes.MORE_FEED_TYPES)
                2 -> changeGameMode(GameModes.MORE_FEED)
                3 -> changeGameMode(GameModes.SHRINK_SNAKE)
                else -> {
                    Log.e("Oyun Modu Ayarlanmadı", "Verilen index değeri tanımsız. Lütfen kontrol et; \" GameModeActivity.setMode \" . (SnakeEngine.setGameMode)")
                }
            }
        }

        private fun changeGameMode(mode: GameModes) {
            if (gameModes.contains(mode))
                gameModes.remove(mode)
            else
                gameModes.add(mode)
        }
    }


    // Tema renkleri
    private val theme = Theme.getTheme(context, themeMode)

    // Arka plan müziği
    private var mediaPlayer: MediaPlayer? = null

    // Ses efekti değişkenleri
    private lateinit var soundPool: SoundPool
    private var soundEat: Int = -1
    private var soundCrash: Int = -1

    // Yılan değişkenleri
    private lateinit var snake: Snake
    var lengthSnake: Int = 4

    // Yem Değişkenleri
    private val feeds = ArrayList<Feed>()

    // Oyun score değişkeni
    private var score: Int = 0

    /**
     * Bellir bir gecikmeyle run() metodunu çalıştırır. "Tetikleyici"
     * @see start
     * @see run
     */
    private val handler = Handler()


        // Kontrol değişkenleri
        private var isStarted = false
        private var isSurfaceCreated = false
        private var areVariablesDefined = false
        private var isPaused = false
    private var hasFrame = gameModes.contains(GameModes.FRAME)

    /**
     * Saniyelik yenilenme sayısı
     */
    var fps: Int = 10

    init {
        initSounds()
    }

    private fun initSounds() {
        mediaPlayer = when (musicMode) {
            NO_MUSICS -> null
            FERAMBIE -> MediaPlayer.create(context, R.raw.bg_music1)
            HORROR_MUSIC -> MediaPlayer.create(context, R.raw.bg_horror)
        }

        soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 0)

        try {
            val mode = when (soundsMode) {
                NO_SOUNDS -> return // Bu kısmı incele "return" sounds değişkenleri atlanmalı mı?
                CLASSIC_SOUNDS -> "classic"
                RETRO_SOUNDS -> "retro"
                GUN_SOUNDS -> "gun"
                HORROR_SOUNDS -> "horror"
            }

            var descriptor = context.assets.openFd("Sounds/${mode}_eating.wav")
            soundEat = soundPool.load(descriptor, 0)

            descriptor = context.assets.openFd("Sounds/${mode}_crash.wav")
            soundCrash = soundPool.load(descriptor, 0)


        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        isSurfaceCreated = false
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Yüzey kontrolu için
        isSurfaceCreated = true

        if (isPaused) {
            // Ekrana çizme
            draw()
        } else {
            BlockField.initVariables(holder.surfaceFrame, hasFrame)
            prepare()
        }
    }

    /**
     * Oyunu hazır hale getirip, durgun halde bekletme
     * Not: Motor çalışırken kullanılamaz.
     * @see start
     * Not: Yüzey oluşturulmadan çalışmaz.
     * @see surfaceCreated
     */
    private fun prepare() {
        if (!isStarted && isSurfaceCreated) {
            initVariables()
            newGame()
            isPaused = true
        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Zaten çalışan motoru hazır hale getiremeyiz. Yüzey alanı oluşturulmadan çalıştırılamaz, genelde SurfaceCreated içerisinden çağırılır." +
                            "\"surfaceCreated\" metodunu kontrol et. (SnakeEngine.prepare)"
            )
        }
    }

    /**
     * Değişkenlerin tanımlanması
     * Not: Yüzey oluşturulmadan çalışmaz.
     * Not: Oyun moduna göre şekillenir.
     * @see surfaceCreated
     */
    private fun initVariables() {
        if (isSurfaceCreated) {

            // Oyun moduna göre farklı tanımalama şekilleri olacak
            snake = when {
                gameModes.contains(GameModes.SHRINK_SNAKE) -> Snake(Snake.maxLength)
                else -> Snake(lengthSnake)
            }

            initFeeds()

            areVariablesDefined = true

        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Yüzey alanı ve field oluşturulmadan çalıştırılamaz, genelde SurfaceCreated'de  çağırılır." +
                            "\"prepare, surfaceCreated\" metodunu kontrol et. (SnakeEngine.initVariables)"
            )
        }
    }

    /**
     * Not: Sadece bir kere kullanılmalı
     */
    private fun initFeeds() {
        when { // Yemleri oluşturma
            gameModes.contains(GameModes.MORE_FEED) -> {
                for (i in 0..Random().nextInt(8)) // Düzenle
                    feeds.add(Feed())
            }
            else -> {
                feeds.add(Feed())
            }
        }
    }

    private fun spawnFeeds() {
        feeds.forEach { feed -> feed.spawnActTo(snake, feeds, gameModes) }
    }


    /**
     * Yeni oyunun oluşturup, çizilmesi
     * Not: Değişkenler tanımlanmadan çalışmaz.
     * Not: Oyun moduna göre şekillenir.
     * @see initVariables
     */
    private fun newGame() {
        if (areVariablesDefined) {
            snake.create()

            spawnFeeds()

            score = 0

            // Oluşturulan oyunu ekrana çizmek için (Kaldırılabilir)
            draw()

        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Değişkenler tanımlanmadan çalıştırılamaz." +
                            "Metodu kullanmadan önce \"initVariables\" metodunu kullandığından emin ol. (SnakeEngine.newGame)"
            )
        }
    }

    /**
     * Verilerin yüzeye çizdirilmesi
     * Not: Değişkenler tanımlanmadan çalışmaz.
     * @see initVariables
     */
    private fun draw() {
        if (isSurfaceCreated && areVariablesDefined) {
            if (holder.surface.isValid) {
                val canvas: Canvas = holder.lockCanvas()
                val paint = Paint()

                // Kenar boşluklarını çizme
                paint.color = Color.BLACK
                canvas.drawPaint(paint)

                // Eğer varsa çerçeveyi çizme
                if (hasFrame) {
                    paint.color = theme.colorBg
                    canvas.drawRect(BlockField.frame, paint)
                }

                // Oyun ekranını çiziyoruz
                paint.color = theme.colorField
                canvas.drawRect(BlockField.field, paint)

                // Score'u çiziyoruz
                paint.color = theme.colorScore
                paint.textSize = 64f
                canvas.drawText("Score: $score", 30f, 94f, paint)

                /*Dışa doğru çizilmeli içe dğeil
                paint.color = context.resources.getColor(R.color.ultra_violet)
                canvas.drawRect(theBlockFrame(Snake.Directions.LEFT), paint)
                canvas.drawRect(theBlockFrame(Snake.Directions.RIGHT), paint)
                canvas.drawRect(theBlockFrame(Snake.Directions.DOWN), paint)
                canvas.drawRect(theBlockFrame(Snake.Directions.UP), paint)
                */

                // Yılanı çiziyoruz
                paint.color = theme.colorSnake
                snake.drawHead(canvas, paint)
                snake.drawTails(canvas, paint)

                // Yılanın gözünü çizme
                paint.color = theme.colorSnakeEyes
                snake.drawEyes(canvas, paint)

                // Yemi çiziyoruz
                paint.color = theme.colorFeed
                feeds.forEach { feed -> feed.draw(context, canvas, paint) }

                holder.unlockCanvasAndPost(canvas)
            }
        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Değişkenler tanımlanmadan çalıştırılamaz." +
                            "Metodu kullanmadan önce \"initVariables\" metodunu kullandığından emin ol. (SnakeEngine.drawTails)"
            )
        }

    }

    /**
     * Motorun "kontrollü" başlatılması veya devam ettirilmesi
     * Not: Yüzey oluşturulmadan çalışmaz.
     * @see surfaceCreated
     */
    fun startSensible() {
        if (!isStarted || isPaused) {
            start()
        }
    }

    /**
     * Motorun başlatılması veya devam ettirilmesi
     * Not: Yüzey oluşturulmadan çalışmaz.
     * @see surfaceCreated
     */
    private fun start() {
        if (isSurfaceCreated) {
            if (mediaPlayer != null) {
                mediaPlayer!!.setVolume(0.7f, 0.7f)
                mediaPlayer!!.isLooping = true
                mediaPlayer!!.start()
            }


            isStarted = true
            isPaused = false

            handler.postDelayed(this, (1000 / fps).toLong())
        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Yüzey oluşturulmadan çalıştırılamaz." +
                            "Metodu kullanmadan önce \"surfaceCreated\" metodunun kullandığından emin ol. (SnakeEngine.start)"
            )
        }
    }

    /**
     * YılanMotor'unun çalışması
     * Not: Motor çalıştırılmadan çalışmaz.
     * @see start
     * Not: Motor durdulduysa çalışmaz.
     * @see pause
     * Not: Yüzey oluşturulmadan çalışmaz.
     * @see surfaceCreated
     * Not: Değişkenler tanımlanmadan çalışmaz.
     * @see initVariables
     */
    override fun run() {
        if (isStarted && !isPaused && isSurfaceCreated && areVariablesDefined) {
            snake.move(hasFrame)


            feeds.forEach { feed ->
                if (snake.isFeeding(feed.center)) {
                    eatFeed(feed)
                }
            }

            if (snake.isCrashed(hasFrame)) {
                soundPool.play(soundCrash, 1f, 1f, 0, 0, 1f)
                newGame()
            }

            draw()

            handler.postDelayed(this, (1000 / fps).toLong())
        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Motor başlatılmadan, yüzey oluşturulmadan, değişkenler tanımlanmadan çalıştırılamaz." +
                            "Metodu kullanmadan önce \"start, pause, surfaceCreated, initVariables\" metodlarını kullandığından emin ol. (SnakeEngine.run)"
            )
        }
    }

    /**
     * Yem yenildiğinde olacak işlemler
     * Not: Değişkenler tanımlanmadan çalışmaz.
     * Not: Oyun moduna göre şekillenir.
     * @see initVariables
     */
    private fun eatFeed(feed: Feed) {
        if (areVariablesDefined) {
            incScore(feed.getFeedType())

            soundPool.play(soundEat, 1f, 1f, 0, 0, 1f)

            snake.eatFeedActTo(gameModes, feed.getFeedType())

            spawnFeeds()

        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Değişkenler tanımlanmadan çalıştırılamaz." +
                            "Metodu kullanmadan önce \"initVariables\" metodunun kullandığından emin ol. (SnakeEngine.eatFeed)"
            )
        }
    }

    private fun incScore(feedType: Feed.FeedTypes) {
        score += when (feedType) {
            Feed.FeedTypes.NORMAL -> 1
            Feed.FeedTypes.DOUBLE_FEED -> 2
            Feed.FeedTypes.X5_FEED -> 5
            Feed.FeedTypes.X10_FEED -> 10
            Feed.FeedTypes.X25_FEED -> 25
        }
    }

    fun pause() {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying)
                mediaPlayer!!.pause()
        }

        handler.removeCallbacks(this)

        isPaused = true

    }

    fun pauseOrStart() {
        when (isPaused) {
            true -> startSensible()
            else -> pause()
        }
    }

    /* Gerekli değil, start da aynısını yapıyor
    fun resume() {
        mediaPlayer.start()
        handler.postDelayed(this, (1000 / fps).toLong())
    }
    */
}