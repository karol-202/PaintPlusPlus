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
package pl.karol202.paintplus.activity

import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import pl.karol202.paintplus.databinding.ActivityPaintBinding
import pl.karol202.paintplus.image.layer.LayersAdapter
import pl.karol202.paintplus.options.OptionLayerNew
import pl.karol202.paintplus.util.LayersSheetBehavior
import pl.karol202.paintplus.util.MathUtils
import pl.karol202.paintplus.util.collectIn
import pl.karol202.paintplus.viewmodel.PaintViewModel

private const val KEYLINE_3_2 = 3f / 2f
private const val SHEET_PANEL_SIZE_DP = 56f

class ActivityPaintLayers(private val activity: ActivityPaint,
                          private val views: ActivityPaintBinding,
                          private val paintViewModel: PaintViewModel)
{

	private val layersAdapter = LayersAdapter(activity)
	private val bottomSheetBehaviour = BottomSheetBehavior.from(views.bottomSheet) as LayersSheetBehavior<*>

	private val sheetPanelSizePx = MathUtils.dpToPixels(activity, SHEET_PANEL_SIZE_DP).toInt()

	fun initLayers()
	{
		layersAdapter.setImage(paintViewModel.image)

		bottomSheetBehaviour.skipCollapsed = true
		bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN

		views.recyclerLayers.adapter = layersAdapter
		views.buttonAddLayer.setOnClickListener {
			OptionLayerNew(activity, paintViewModel.image) { layersAdapter.notifyDataSetChanged() }.execute()
		}

		paintViewModel.imageEventFlow.collectIn(activity.lifecycleScope) {
			if(it == PaintViewModel.ImageEvent.LAYERS_CHANGED) updateData()
		}
	}

	private fun updateData() = layersAdapter.notifyDataSetChanged()

	fun updateView()
	{
		val activityHeight = activity.window.decorView.height
		val maxSheetHeight = (activityHeight / KEYLINE_3_2).toInt()
		val maxRecyclerHeight = maxSheetHeight - sheetPanelSizePx
		views.recyclerLayers.setMaxHeight(maxRecyclerHeight)
	}

	fun toggleLayersSheet() = when(bottomSheetBehaviour.state)
	{
		BottomSheetBehavior.STATE_HIDDEN -> bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED)
		else -> bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN)
	}

	fun closeLayersSheet()
	{
		bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
	}

	fun setScrollingBlocked(blocked: Boolean)
	{
		views.recyclerLayers.setAllowScrolling(!blocked)
		bottomSheetBehaviour.allowDragging = !blocked
	}
}
