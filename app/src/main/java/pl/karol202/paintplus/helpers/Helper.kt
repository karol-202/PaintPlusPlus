package pl.karol202.paintplus.helpers

import android.graphics.Canvas
import kotlinx.coroutines.flow.Flow

interface Helper
{
	fun onScreenDraw(canvas: Canvas)
}
