package com.yemreak.retrosnakerpg

import android.graphics.*
import android.util.Log
import com.yemreak.retrosnakerpg.Snake.Directions.*

class Snake() {
    /**
     * Yılanın başı
     */
    var head: Point = Point()

    /**
     * Yılanın kuyruğu
     */
    val tails = ArrayList<Point>()

    private var defaultLength = 4

    /**
     * Yön belirteci classı
     */
    enum class Directions {
        LEFT, UP, RIGHT, DOWN
    }

    companion object {

        /**
         * Başlangıç yönü
         */
        private var direction = RIGHT

        /**
         * Yeni başlangıç yönü
         * Hatırlatma: Yılanın yönü bir adımda birden fazla değiştirilmesin diye yapıldı. Sağ'a giden bir yılana *yukarı - sol* verileri verince (yukarı dönmeden sol verisi veriliyor) ölmüş sayılıyordu engellendi.
         * @see SnakeActivity1.onButtonClick
         * @see SnakeActivity2.svFullGameFieldOnTouch
         */
        var newDirection = RIGHT

        /**
         * Shrink game modundaki yılanın başlangıç uzunluğu
         * @see SnakeEngine.GameModes
         */
        var maxLength = 100
    }

    constructor(length: Int) : this() {
        defaultLength = length
    }

    /**
     * Yılanı oyun alanında oluşturmak
     */
    fun create() {
        // Yönü sıfırlıyoruz
        direction = RIGHT

        // Kuyruğu temizliyoruz
        tails.clear()

        // Başı merkezde oluşturuyoruz. *Kopyasını* atıyoruz.
        head = Point(BlockField.centerPoint())

        // Her bir kuyruğu oluşturuyoruz.
        for (i in 1 until defaultLength) {
            tails.add(BlockField.point(head, -i, 0))
        }
    }

    fun move(hasFrame: Boolean) {
        for (i in tails.lastIndex downTo 0) {
            if (i == 0) {
                tails[i] = Point(head) // Not: Direk atama işlemi yaparsak adresi kopyalanır.
                continue
            }
            tails[i] = Point(tails[i - 1]) // Not: Direk atama işlemi yaparsak adresi kopyalanır.
        }

        refreshDireciton() // Yeni hareket için yönümüzü güncelliyoruz
        head = BlockField.besidePoint(head, hasFrame, direction)
    }

    /**
     * Yılanın yönünü yeni yön istediğine göre günceller.
     * @see newDirection
     */
    private fun refreshDireciton() {
        when (direction) {
            LEFT ->
                if (newDirection != RIGHT)
                    direction = newDirection
            UP ->
                if (newDirection != DOWN)
                    direction = newDirection
            RIGHT ->
                if (newDirection != LEFT)
                    direction = newDirection
            DOWN ->
                if (newDirection != UP)
                    direction = newDirection
        }
    }

    fun isFeeding(feed: Point): Boolean {
        return feed.equals(head.x, head.y)
    }

    /**
     * Oyun moduna göre yılanın yem yeme durumunda yapılan işlemleri yapar.
     * @param gameModes Oyun Modları Listesi
     * @param feedType Yinelen yemin tipi
     */
    fun eatFeedActTo(gameModes: ArrayList<SnakeEngine.GameModes>, feedType: Feed.FeedTypes) {
        // Oyun moduna göre değişecek kısım
        if (gameModes.contains(SnakeEngine.GameModes.SHRINK_SNAKE)) {
            when (feedType) {
                Feed.FeedTypes.NORMAL -> grownDown()
                Feed.FeedTypes.DOUBLE_FEED -> grownDown(2)
                Feed.FeedTypes.X5_FEED -> grownDown(5)
                Feed.FeedTypes.X10_FEED -> grownDown(10)
                Feed.FeedTypes.X25_FEED -> grownDown(25)
            }
        } else {
            when (feedType) {
                Feed.FeedTypes.NORMAL -> growUp()
                Feed.FeedTypes.DOUBLE_FEED -> growUp(2)
                Feed.FeedTypes.X5_FEED -> growUp(5)
                Feed.FeedTypes.X10_FEED -> growUp(10)
                Feed.FeedTypes.X25_FEED -> growUp(25)
            }
        }
    }

    private fun growUp(num: Int) {
        for (i in 0 until num)
            growUp()
    }

    private fun growUp() {
        when (tails.size) {
            0 -> tails.add(BlockField.behindPoint(head, direction))
            else -> tails.add(tails.last())
        }
    }

    private fun grownDown(num: Int) {
        for (i in 0 until num)
            grownDown()
    }

    private fun grownDown() {
        when (tails.size) {
            0 -> return
            else -> tails.remove(tails.last())
        }
    }

    fun drawTails(canvas: Canvas, paint: Paint) {
        tails.forEach { tail ->
            canvas.drawRect(tail, paint)
        }
    }

    private fun Canvas.drawRect(center: Point, paint: Paint) {
        this.drawRect(BlockField.getBlock(center), paint)
    }

    fun drawHead(canvas: Canvas, paint: Paint) {
        canvas.drawRect(head, paint)
    }


    fun drawEyes(canvas: Canvas, paint: Paint) {
        val angle: Float = when (direction) {
            LEFT -> 180f
            UP -> 270f
            RIGHT -> 0f
            DOWN -> 90f
        }

        canvas.save()
        canvas.rotate(angle, BlockField.getBlock(head).exactCenterX(), BlockField.getBlock(head).exactCenterY())

        val leftEyes = RectF(
                BlockField.getBlock(head).right - 3 * BlockField.blockSize / 7f,
                BlockField.getBlock(head).top + BlockField.blockSize / 7f,
                BlockField.getBlock(head).right - BlockField.blockSize / 7f,
                BlockField.getBlock(head).top + 3 * BlockField.blockSize / 7f
        )

        val rightEyes = RectF(
                BlockField.getBlock(head).right - 3 * BlockField.blockSize / 7f,
                BlockField.getBlock(head).bottom - 3 * BlockField.blockSize / 7f,
                BlockField.getBlock(head).right - BlockField.blockSize / 7f,
                BlockField.getBlock(head).bottom - BlockField.blockSize / 7f
        )

        canvas.drawRect(leftEyes, paint)
        canvas.drawRect(rightEyes, paint)

        canvas.restore()
    }

    /**
     * Çerçeve durumuna göre yılanın çarpışma durumunu bulma
     * @param hasFrame Çerçeve varsa true, yoksa false
     * @return Çarpışma varsa true, yoksa false
     */
    fun isCrashed(hasFrame: Boolean): Boolean {
        return when (hasFrame) {
            true -> when {
                head.x < 0 -> true
                head.x >= BlockField.blockNum.x-> true
                head.y < 0 -> true
                head.y >= BlockField.blockNum.y -> true
                isTail() -> true
                else -> false
            }
            false -> when {
                isTail() -> true
                else -> false
            }

        }
    }

    private fun isTail(): Boolean {
        return tails.contains(head)
    }


}