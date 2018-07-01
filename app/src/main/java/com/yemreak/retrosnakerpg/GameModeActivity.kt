package com.yemreak.retrosnakerpg

import android.view.View
import com.yemreak.retrosnakerpg.CustomDialogInfos.Dialogs.CLASSIC_MODES
import kotlinx.android.synthetic.main.activity_game_mode.*

class GameModeActivity : CustomDialogActivity() {

    override val layoutId: Int
        get() = R.layout.activity_game_mode

    override fun bindEvents() {
        ivCancel.setOnClickListener { onBackPressed() }
        btnClassicModes.setOnClickListener { v: View -> onButtonClick(CustomDialogInfos(CLASSIC_MODES), v) }
    }
}
