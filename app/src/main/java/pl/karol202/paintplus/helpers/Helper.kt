package pl.karol202.paintplus.helpers

import android.graphics.Canvas
import kotlinx.coroutines.flow.Flow

interface Helper
{
	val updateEventFlow: Flow<Unit>? get() = null

	fun onScreenDraw(canvas: Canvas)
}
