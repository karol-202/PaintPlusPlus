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
package pl.karol202.paintplus.image.layer

import android.graphics.PointF
import android.view.View
import android.view.ViewGroup
import pl.karol202.paintplus.activity.ActivityPaint
import pl.karol202.paintplus.util.MathUtils.dpToPixels
import pl.karol202.paintplus.util.translated
import kotlin.math.roundToInt

class LayerHandle(private val activity: ActivityPaint,
                  private val viewHolders: Map<Int, LayerViewHolder>,
                  private val onMove: (layerIndex: Int, target: Int) -> Unit)
{
	private val viewHeight = dpToPixels(activity, LayerViewHolder.HEIGHT_DP).toInt()
	private val mainContainer = activity.mainContainer

	private var layer: Layer? = null
	private var layerId = 0
	private var viewHolder: LayerViewHolder? = null
	private var oldTouchX = 0f
	private var oldTouchY = 0f
	private var oldOffsetX = 0f
	private var oldOffsetY = 0f
	private var currentPos = 0

	fun onTouchStart(x: Float, y: Float)
	{
		val viewHolder = viewHolder ?: return

		val view = viewHolder.rootView
		val originalViewPos = findPointInMainContainer(view)
		val parent = view.parent as ViewGroup
		parent.removeView(view)
		viewHolder.setGhost()
		viewHolder.setViewOffset(originalViewPos.x, originalViewPos.y)
		mainContainer.addView(view)
		activity.setScrollingBlocked(true)
		oldOffsetX = originalViewPos.x
		oldOffsetY = originalViewPos.y
		oldTouchX = x
		oldTouchY = y
	}

	private fun findPointInMainContainer(view: View, point: PointF = PointF()): PointF =
			if(view === mainContainer) point
			else findPointInMainContainer(
					view = view.parent as? View ?: throw RuntimeException("Unexpected end of hierarchy."),
					point = point.translated(view.x, view.y))

	fun onTouchMove(x: Float, y: Float)
	{
		val viewHolder = viewHolder ?: return

		val deltaX = x - oldTouchX
		val deltaY = y - oldTouchY
		viewHolder.setViewOffset(oldOffsetX + deltaX, oldOffsetY + deltaY)
		moveOtherLayers(getCurrentPosition(y))
	}

	fun onTouchStop(x: Float, y: Float)
	{
		val viewHolder = viewHolder ?: return

		currentPos = getCurrentPosition(y)
		val targetGhostPos = oldOffsetY + (currentPos - layerId) * viewHeight
		viewHolder.setViewOffsetWithAnimation(0f, targetGhostPos, this::dropLayer)
		moveOtherLayers(currentPos)
		activity.setScrollingBlocked(false)
	}

	fun onTouchCancel()
	{
		val viewHolder = viewHolder ?: return

		viewHolder.setViewOffsetWithAnimation(0f, oldOffsetY, this::dropLayer)
		activity.setScrollingBlocked(false)
	}

	private fun getCurrentPosition(y: Float): Int
	{
		val deltaY = y - oldTouchY
		val deltaPos = (deltaY / viewHeight).roundToInt()
		return (layerId + deltaPos).coerceIn(0, viewHolders.size - 1)
	}

	private fun moveOtherLayers(currentPos: Int)
	{
		val one = if(currentPos > layerId) 1 else -1
		var firstIndex = layerId + one
		var lastIndex = currentPos
		if(firstIndex > lastIndex)
		{
			val temp = firstIndex
			firstIndex = lastIndex
			lastIndex = temp
		}
		val holderReplacement = viewHolders[layerId]
		holderReplacement!!.hide()
		for((key, holder) in viewHolders)
		{
			val inRange = key in firstIndex..lastIndex
			if(inRange && currentPos != layerId) holder.setViewOffsetWithAnimation(0f, (-one * viewHeight).toFloat())
			else holder.setViewOffsetWithAnimation(0f, 0f)
		}
	}

	private fun dropLayer()
	{
		mainContainer.removeView(viewHolder?.rootView)
		onMove(layerId, currentPos)
		layer = null
		viewHolder = null
	}

	fun setLayer(layer: Layer, viewHolder: LayerViewHolder)
	{
		this.layer = layer
		this.viewHolder = viewHolder
		layerId = findViewHolderId(viewHolder)
	}

	private fun findViewHolderId(holder: LayerViewHolder) =
			viewHolders.entries.find { (_, h) -> h == holder }?.key
					?: throw NullPointerException("There is no such view holder in holders list.")
}
