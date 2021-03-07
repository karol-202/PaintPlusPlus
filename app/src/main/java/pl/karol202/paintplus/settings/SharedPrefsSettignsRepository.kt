package pl.karol202.paintplus.settings

import android.content.SharedPreferences
import kotlinx.coroutines.flow.combine

private const val KEY_SMOOTH_VIEW = "preference_view_smooth"

class SharedPrefsSettingsRepository(private val sharedPreferences: SharedPreferences) : SettingsRepository
{
	private val smoothView
		get() = sharedPreferences.observeBoolean(KEY_SMOOTH_VIEW, true)

	override val settings
		get() = combine(smoothView) { (smoothView) ->
			Settings(smoothView)
		}
}
