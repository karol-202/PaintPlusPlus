package pl.karol202.paintplus.util

import android.widget.SeekBar

fun SeekBar.setOnValueChangeListener(listener: (Int) -> Unit) =
		setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) = listener(progress)

			override fun onStartTrackingTouch(seekBar: SeekBar?) { }

			override fun onStopTrackingTouch(seekBar: SeekBar?) { }
		})
