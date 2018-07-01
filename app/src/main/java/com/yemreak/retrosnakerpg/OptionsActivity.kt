package com.yemreak.retrosnakerpg


import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.ToggleButton
import kotlinx.android.synthetic.main.activity_options.*
import com.yemreak.retrosnakerpg.CustomDialogInfos.Dialogs.*

class OptionsActivity : CustomDialogActivity() {

    override val layoutId: Int
        get() = R.layout.activity_options

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setOnlyOneButtonCheckable = true
    }

    override fun bindEvents() {
        ivCancel.setOnClickListener { onBackPressed() }
        // ...
        btnMusics.setOnClickListener { v -> onButtonClick(CustomDialogInfos(MUSICS), v) }
        btnSounds.setOnClickListener { v -> onButtonClick(CustomDialogInfos(SOUNDS), v) }
        btnControls.setOnClickListener { v -> onButtonClick(CustomDialogInfos(CONTROLS), v) }
        btnThemes.setOnClickListener { v -> onButtonClick(CustomDialogInfos(THEMES), v) }
    }

    /**
    override fun onAnyButtonClicked(toggleButton: ToggleButton, alertDialog: AlertDialog) {
        super.onAnyButtonClicked(toggleButton, alertDialog)

        // alertDialog.dismiss() // Buton seçildikten sonra diyalağu kapatıyoruz.
    }
    */

}
