package com.yemreak.retrosnakerpg

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import com.yemreak.retrosnakerpg.BlockField.Sizes.*
import java.util.*

abstract class BlockField { // STATİk YAp (çok mantıklı çünkü her class bunun fonksiyonları kullanıyor.) GÜvenli hale getir.

    /**
     * Ekranın büyklüğü
     *
     * Not: Değişikliklerin aşağıdaki yerlere de uygulanması gerekmektedir;
     * @see R.layout.dialog_ex_settings android:progress sayısı ile aynı sayıda olmalılar. (6 ise progress = 5 çünkü [0,5])
     * @see R.string "Oyun Büyüklüğü Metinleri"
     * @see setBlockSize Aynı sayıda olmalılar.
     */
    enum class Sizes {
        VERY_SMALL,
        SMALL,
        NORMAL,
        LARGE,
        VERY_LARGE,
        EXTREMELY_LARGE;

        companion object {
            fun get(index: Int): Sizes {
                return when (index) {
                    0 -> VERY_SMALL
                    1 -> SMALL
                    3 -> LARGE
                    4 -> VERY_LARGE
                    5 -> EXTREMELY_LARGE
                    else -> NORMAL // Farklı değerler ve 2 için normal gelsin
                }
            }

            fun getIndex(size: Sizes): Int {
                return when (size) {
                    VERY_SMALL -> 0
                    SMALL -> 1
                    NORMAL -> 2
                    LARGE -> 3
                    VERY_LARGE -> 4
                    EXTREMELY_LARGE -> 5
                }
            }

            fun getTextId(index: Int): Int {
                return when (index) {
                    0 -> R.string.txt_block_very_small
                    1 -> R.string.txt_block_small
                    3 -> R.string.txt_block_large
                    4 -> R.string.txt_block_very_large
                    5 -> R.string.txt_block_extremely_large
                    else -> R.string.txt_block_normal // Farklı değerler ve 2 için normal gelsin
                }
            }

            fun getTextId(size: Sizes): Int {
                return when (size) {
                    VERY_SMALL -> R.string.txt_block_very_small
                    SMALL -> R.string.txt_block_small
                    LARGE -> R.string.txt_block_large
                    VERY_LARGE -> R.string.txt_block_very_large
                    EXTREMELY_LARGE -> R.string.txt_block_extremely_large
                    else -> R.string.txt_block_normal // Farklı değerler ve 2 için normal gelsin
                }
            }
        }
    }

    companion object {

        var blockSizeMode: Sizes = NORMAL

        private lateinit var surfaceField: Rect

        /**
         * Çerçevelerin ve boşluğun çıkartıldığı yüzeyin adresi
         * Not: Yüzey oluşturulup, çerçeve çıkarılınca değerini alır.
         * @see SnakeEngine.surfaceCreated
         */
        var field = Rect()
            private set(value) {
                field = value
            }

        /**
         * Çerçevenin dörtgensel koordinatları
         * Not: Eğer çerçeve yoksa eşdeğeri 0 'dır.
         */
        var frame = Rect()
            private set(value) {
                field = value
            }

        /**
         * Çerçevenin noktasal büyüklüğü
         * Not: Eğer çerçeve yoksa 0 alır
         */
        var frameSize = Point()
            private set(value) {
                field = value
            }

        /**
         * Kenar boşluklarının noktasal değeri
         */
        var gap: Point = Point()
            private set(value) {
                field = value
            }

        /**
         * Ekrandaki block geniliği
         * Not: Dışarıdan değiştirmek için, modu değiştirin.
         * @see blockSizeMode
         *
         */
        var blockSize: Int = 0
            private set(value) {
                field = value
            }

        private var maxNumBlock: Point = Point(0, 0)

        var blockNum: Point = Point(0, 0)
            private set(value) {
                field = value
            }

        const val FRAME_POINT_SIZE = 1
        private const val ERROR_CODE_INT: Int = -1
        private const val EXTENDED_SIZE = 10

        /**
         * Sık kullanılan değerlerin oluşturulması
         * Not: Surface tanımlanmadan kullanılmamalı
         * @see SnakeEngine.surfaceCreated
         */
        fun initVariables(surfaceField: Rect, hasFrame: Boolean) {
            this.surfaceField = surfaceField

            setBlockSize() // Blok büyüklüğünü oluşturuyoruz

            if (surfaceField.right != 0 && surfaceField.bottom != 0) { // FRAME_POINT_SIZE kadar çerçeveye gidiyor. (Tam bölünmesi lazım)
                maxNumBlock.x = surfaceField.right / blockSize
                maxNumBlock.y = surfaceField.bottom / blockSize

                gap.x = (surfaceField.right % blockSize) / 2
                gap.y = (surfaceField.bottom % blockSize) / 2

                field = Rect(surfaceField)
                field.inset(gap.x, gap.y)

                if (hasFrame) {
                    frame = Rect(field)
                    frameSize = Point((FRAME_POINT_SIZE * blockSize), (FRAME_POINT_SIZE * blockSize))
                    field.inset(frameSize.x, frameSize.y)
                }

                blockNum.x = numBlockOf(field.width())
                blockNum.y = numBlockOf(field.height())


            } else {
                Log.e("Oyun alanı mevcut değil", "Lütfen oyun alanının oluşturulduğundan emin ol (BlockField.reInitVariables)")
            }
        }

        private fun setBlockSize() {
            blockSize = when (blockSizeMode) {
                VERY_SMALL -> 20
                SMALL -> 30
                NORMAL -> 40
                LARGE -> 60
                VERY_LARGE -> 80
                EXTREMELY_LARGE -> 100
            }
        }

        fun numBlockOf(x: Int): Int {
            return x / blockSize
        }

        fun step(x: Int): Int {
            return x * blockSize
        }

        fun increase(x: Int): Int {
            return x + blockSize
        }

        fun point(centerX: Int, centerY: Int): Rect { // 0 0 0 0- 4
            return Rect(
                    centerX - blockSize / 2,
                    centerY - blockSize / 2,
                    centerX + blockSize / 2,
                    centerY + blockSize / 2
            )
        }

        /**
         * Ortadaki noktayı döndürür,
         * Not: ortası yoksa *soldakini* döndürür.
         */
        fun centerPoint(): Point {
            return Point(
                    blockNum.x / 2 + blockNum.x % 2 - 1,
                    blockNum.y / 2 + blockNum.y % 2 - 1
            )

        }

        /**
         * Ekrana göre kaydırılmış blok döndürür.
         * Not: Eğer en sağda iken bir sağını istiyorsan, soldan verir. (Mod işlemi)
         * Not: Negatif (-) olma durumun kaldırmak için mod değerini ekliyoruz.
         */
        fun point(point: Point, hasFrame: Boolean, offsetX: Int, offsetY: Int): Point {
            return when (hasFrame) {
                true -> Point(
                        point.x + offsetX,
                        point.y + offsetY
                )
                false -> Point(
                        (point.x + offsetX + blockNum.x) % blockNum.x,
                        (point.y + offsetY + blockNum.y) % blockNum.y
                )
            }
        }

        /**
         * Ekrana göre kaydırılmış blok döndürür.
         * Not: Eğer en sağda iken bir sağını istiyorsan, ekran *dışında* nokta değeri verir.
         */
        fun point(point: Point, offsetX: Int, offsetY: Int): Point {
            return Point(
                    point.x + offsetX,
                    point.y + offsetY
            )
        }


        /**
         * ALınan dörtgenin 1 blok yanındaki dörtgeni döndürür. Eğer ekran dışında olursa
         * ters doğrultudan verir. (mod işlemi gibi, en sağa gitmek en soldan çıkmaktır.)
         * @param direction Yön bilgisi
         * @param center Dörtgenin merkez noktası
         */
        fun besidePoint(center: Point, hasFrame: Boolean, direction: Snake.Directions): Point {
            return when (direction) {
                Snake.Directions.UP -> point(center, hasFrame, 0, -1)
                Snake.Directions.DOWN -> point(center, hasFrame, 0, 1)
                Snake.Directions.RIGHT -> point(center, hasFrame, 1, 0)
                Snake.Directions.LEFT -> point(center, hasFrame, -1, 0)
            }
        }

        /**
         * ALınan dörtgenin 1 blok arkasındaki dörtgeni döndürür
         * @param center Dörtgenin merkez noktası
         * @param hasFrame Çerçeve varsa true, yoksa false
         * @param direction Yön bilgisi
         */
        fun behindPoint(center: Point, hasFrame: Boolean, direction: Snake.Directions): Point {
            return when (direction) {
                Snake.Directions.UP -> point(center, hasFrame, 0, 1)
                Snake.Directions.DOWN -> point(center, hasFrame, 0, -1)
                Snake.Directions.RIGHT -> point(center, hasFrame, -1, 0)
                Snake.Directions.LEFT -> point(center, hasFrame, 1, 0)
            }
        }

        /**
         * ALınan dörtgenin 1 blok arkasındaki dörtgeni döndürür
         * @param direction Yön bilgisi
         * @param center Dörtgenin merkez noktası
         */
        fun behindPoint(center: Point, direction: Snake.Directions): Point {
            return when (direction) {
                Snake.Directions.UP -> point(center, 0, 1)
                Snake.Directions.DOWN -> point(center, 0, -1)
                Snake.Directions.RIGHT -> point(center, -1, 0)
                Snake.Directions.LEFT -> point(center, 1, 0)
            }
        }

        fun getBlock(center: Point): Rect {
            return Rect( // Px = x.s + s / 2
                    center.x * blockSize + gap.x + frameSize.x,
                    center.y * blockSize + gap.y + frameSize.y,
                    (center.x + 1) * blockSize + gap.x + frameSize.x,
                    (center.y + 1) * blockSize + gap.y + frameSize.y
            )
        }

        /**
         * Oyun alanı içerisinden rastgele merkez noktası döndürür.
         * @return Rastgele merkez noktası
         */
        fun randomCenter(): Point {
            return Point(
                    Random().nextInt(blockNum.x),
                    Random().nextInt(blockNum.y)
            )
        }

        /**
         * Düzenlenmedi
         */
        private fun randomOffset(index: String): Int {
            return when (index) {
                "x" -> {
                    when (blockNum.x % 2) {
                        0 -> Random().nextInt(blockNum.x) - (blockNum.x / 2 - 1)
                        else -> Random().nextInt(blockNum.x) - (blockNum.x / 2) // Değişti kontrol et
                    }
                }
                "y" -> {
                    when (blockNum.y % 2) {
                        0 -> Random().nextInt(blockNum.y) - (blockNum.y / 2 - 1)
                        else -> Random().nextInt(blockNum.y) - (blockNum.y / 2) // Değişti kontrol et
                    }
                }
                else -> 0
            }
        }

        /**
         * @param index Koordinat indexi "x" "y"
         * @return En büyük blok merkez verisi
         */
        fun maxBlockCenter(index: String): Int {
            return when (index) {
                "x" ->
                    (blockNum.x - 1 * blockSize + blockSize / 2 + gap.x)
                "y" ->
                    (blockNum.y - 1 * blockSize + blockSize / 2 + gap.y)
                else -> {
                    Log.e("İndex hatası", "Tanımsız bir index verisi girdin lütfen kontrol et (maxBlockCenter)")
                    ERROR_CODE_INT
                }
            }
        }

        /**
         * @param index Koordinat indexi "x" "y"
         * @return En küçük blok merkez verisi
         */
        fun minBlockCenter(index: String): Int {
            return when (index) {
                "x" ->
                    blockSize / 2 + gap.x
                "y" ->
                    blockSize / 2 + gap.y
                else -> {
                    Log.e("İndex hatası", "Tanımsız bir index verisi girdin lütfen kontrol et (minBlockCenter)")
                    ERROR_CODE_INT
                }
            }
        }

        /**
         * @param direction İstenen yön " Snake.Directions.RIGHT "
         * @return Merkez doğrultusunda en "istenen yön" öğeyi döndürür.
         */
        fun theMostBlock(direction: Snake.Directions): Rect {
            return when (direction) {
                Snake.Directions.RIGHT -> point(
                        maxBlockCenter("x"),
                        field.centerY()
                )
                Snake.Directions.DOWN -> point(
                        field.centerX(),
                        maxBlockCenter("y")
                )
                Snake.Directions.LEFT -> point(
                        minBlockCenter("x"),
                        field.centerY()
                )
                Snake.Directions.UP -> point(
                        field.centerX(),
                        minBlockCenter("y")
                )
            }
        }

        fun theBlockFrame(direction: Snake.Directions): Rect {
            return when (direction) {
                Snake.Directions.RIGHT ->
                    Rect(field.right - gap.x, field.top, field.right, field.bottom)
                Snake.Directions.DOWN ->
                    Rect(field.left, field.bottom - gap.y, field.right, field.bottom)
                Snake.Directions.LEFT ->
                    Rect(field.left, field.top, field.left + gap.x, field.bottom)
                Snake.Directions.UP ->
                    Rect(field.left, field.top, field.right, field.top + gap.y)
            }
        }

        fun bitmapBlock(res: Resources, resId: Int): Bitmap {
            return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, resId), blockSize, blockSize, true) // Resim kaynağını bitmap'e dönüştürüyoruz, boyutlandırıp ortalıyoruz.

        }

        fun extBitmapBlock(res: Resources, resId: Int): Bitmap {
            return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, resId), blockSize + EXTENDED_SIZE, blockSize + EXTENDED_SIZE, true) // Resim kaynağını bitmap'e dönüştürüyoruz, boyutlandırıp ortalıyoruz.
        }
    }
}