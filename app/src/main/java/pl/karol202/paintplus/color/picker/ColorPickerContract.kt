package pl.karol202.paintplus.color.picker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.activity.result.contract.ActivityResultContract

class ColorPickerContract : ActivityResultContract<ColorPickerConfig, Int?>()
{
	override fun createIntent(context: Context, input: ColorPickerConfig) =
			Intent(context, ActivityColorSelect::class.java).apply {
				putExtra(ActivityColorSelect.COLOR_KEY, input.initialColor)
				putExtra(ActivityColorSelect.ALPHA_KEY, input.useAlpha)
			}

	override fun parseResult(resultCode: Int, intent: Intent?): Int?
	{
		if(resultCode != Activity.RESULT_OK || intent == null || !intent.hasExtra(ActivityColorSelect.COLOR_KEY)) return null
		return intent.getIntExtra(ActivityColorSelect.COLOR_KEY, Color.BLACK)
	}
}
