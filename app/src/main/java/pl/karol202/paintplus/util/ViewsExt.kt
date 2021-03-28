package pl.karol202.paintplus.util

import android.util.Size
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Spinner

val View.size get() = Size(width, height)

fun SeekBar.setOnValueChangeListener(listener: (Int) -> Unit) =
		setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) = listener(progress)

			override fun onStartTrackingTouch(seekBar: SeekBar?) { }

			override fun onStopTrackingTouch(seekBar: SeekBar?) { }
		})

fun Spinner.setOnItemSelectedListener(listener: (Int) -> Unit)
{
	onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
		override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = listener(position)

		override fun onNothingSelected(parent: AdapterView<*>?) { }
	}
}
