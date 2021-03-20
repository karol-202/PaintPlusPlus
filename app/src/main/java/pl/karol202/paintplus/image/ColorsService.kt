package pl.karol202.paintplus.image

import android.graphics.Color
import androidx.annotation.ColorInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ColorsService
{
	private val _currentColorFlow = MutableStateFlow(Color.BLACK)

	val currentColorFlow: StateFlow<Int> = _currentColorFlow

	val currentColor get() = _currentColorFlow.value

	fun setCurrentColor(@ColorInt color: Int)
	{
		_currentColorFlow.value = color
	}
}
