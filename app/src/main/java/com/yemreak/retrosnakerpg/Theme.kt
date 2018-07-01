package com.yemreak.retrosnakerpg

import android.content.Context

class Theme(private var ctx: Context) { // DÜZENLE
    var colorBg: Int = 0
    var colorField: Int = 0
    var colorScore: Int = 0
    var colorSnake: Int = 0
    var colorSnakeEyes: Int = 0
    var colorFeed: Int = 0


    private fun create(resId: Int): Theme {
        val colors: IntArray = ctx.resources.getIntArray(resId)
        colorBg = colors[0]
        colorField = colors[1]
        colorScore = colors[2]
        colorSnake = colors[3]
        colorSnakeEyes = colors[4]
        colorFeed = colors[5]

        return this
    }

    companion object {
        fun getTheme(ctx: Context, mode: SnakeEngine.ThemeModes): Theme {
            val resId = when(mode) {
                SnakeEngine.ThemeModes.DEFAULT -> R.array.theme_default
                SnakeEngine.ThemeModes.DARKNESS -> R.array.theme_darkness
                SnakeEngine.ThemeModes.LIGHT -> R.array.theme_light
                SnakeEngine.ThemeModes.AQUA -> R.array.theme_aqua
                SnakeEngine.ThemeModes.PURPLE -> R.array.theme_purple
                SnakeEngine.ThemeModes.HORROR_THEME -> R.array.theme_horror
            }

            return Theme(ctx).create(resId)
        }
    }
}