/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package pl.karol202.paintplus.tool.pan

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.core.view.GestureDetectorCompat
import pl.karol202.paintplus.R
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.tool.Tool
import kotlin.math.roundToInt

class ToolPan(context: Context,
              private val viewService: ViewService) : Tool
{
	private inner class ScrollGestureListener : GestureDetector.SimpleOnGestureListener()
	{
		override fun onDown(e: MotionEvent) = true

		override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean
		{
			viewService.offsetView((distanceX / viewService.zoom).roundToInt(),
			                       (distanceY / viewService.zoom).roundToInt())
			return true
		}
	}

	private inner class ScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener()
	{
		var isScaling = false
			private set
		private var lastFocusX = 0f
		private var lastFocusY = 0f

		override fun onScaleBegin(detector: ScaleGestureDetector): Boolean
		{
			isScaling = true
			lastFocusX = detector.focusX
			lastFocusY = detector.focusY
			return true
		}

		override fun onScale(detector: ScaleGestureDetector): Boolean
		{
			viewService.setZoom(detector.scaleFactor * viewService.zoom, detector.focusX, detector.focusY)
			viewService.offsetView(((lastFocusX - detector.focusX) / viewService.zoom).roundToInt(),
			                       ((lastFocusY - detector.focusY) / viewService.zoom).roundToInt())
			lastFocusX = detector.focusX
			lastFocusY = detector.focusY
			return true
		}

		override fun onScaleEnd(detector: ScaleGestureDetector)
		{
			isScaling = false
		}
	}

	override val name get() = R.string.tool_pan
	override val icon get() = R.drawable.ic_tool_pan_black_24dp
	override val propertiesFragmentClass get() = PanProperties::class.java

	private var scrollGestureListener = ScrollGestureListener()
	private var scaleGestureListener = ScaleGestureListener()
	private var scrollGestureDetector = GestureDetectorCompat(context, scrollGestureListener)
	private var scaleGestureDetector = ScaleGestureDetector(context, scaleGestureListener)

	override fun onTouch(event: MotionEvent): Boolean
	{
		scaleGestureDetector.onTouchEvent(event)
		if(!scaleGestureListener.isScaling) scrollGestureDetector.onTouchEvent(event)
		return true
	}
}
