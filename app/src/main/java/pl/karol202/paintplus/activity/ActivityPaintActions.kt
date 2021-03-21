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

import android.content.pm.PackageManager
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.forEach
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.curves.ColorChannel
import pl.karol202.paintplus.image.LegacyImage
import pl.karol202.paintplus.options.*
import pl.karol202.paintplus.options.legacy.*
import pl.karol202.paintplus.recent.RecentViewModel
import pl.karol202.paintplus.viewmodel.PaintViewModel

class ActivityPaintActions(private val activity: ActivityPaint,
                           private val paintViewModel: PaintViewModel)
{
	private val menuInflater = activity.menuInflater
	private val packageManager = activity.packageManager

	//private val image = paintViewModel.image

	fun inflateMenu(menu: Menu?)
	{
		menuInflater.inflate(R.menu.menu_paint, menu)

		// TODO
		/*paintViewModel.imageEventFlow.collectIn(activity.lifecycleScope) {
			when(it)
			{
				ImageEvent.SELECTION_CHANGED -> activity.invalidateOptionsMenu()
				ImageEvent.HISTORY_CHANGED -> activity.invalidateOptionsMenu()
				else -> {}
			}
		}*/
	}

	fun prepareMenu(menu: Menu)
	{
		val anyDrawerOpen = activity.isAnyDrawerOpen
		setItemsVisibility(menu, !anyDrawerOpen)
		/*preparePhotoCaptureOption(menu)
		prepareFileOpenOption(menu)
		prepareFileSaveOption(menu)
		prepareHistoryOptions(menu)
		prepareClipboardOptions(menu)
		prepareSnapOptions(menu)*/
	}

	private fun setItemsVisibility(menu: Menu, visible: Boolean)
	{
		menu.forEach { it.isVisible = visible }
	}

	/*private fun preparePhotoCaptureOption(menu: Menu)
	{
		val hasCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
		menu.findItem(R.id.action_capture_photo).isEnabled = hasCamera
	}

	private fun prepareFileOpenOption(menu: Menu)
	{
		val state = Environment.getExternalStorageState()
		val enable = state == Environment.MEDIA_MOUNTED || state == Environment.MEDIA_MOUNTED_READ_ONLY
		menu.findItem(R.id.action_open_image).isEnabled = enable
		menu.findItem(R.id.action_open_layer).isEnabled = enable
	}

	private fun prepareFileSaveOption(menu: Menu)
	{
		val state = Environment.getExternalStorageState()
		val savingAs = state == Environment.MEDIA_MOUNTED
		menu.findItem(R.id.action_save_image_as).isEnabled = savingAs
		menu.findItem(R.id.action_save_layer).isEnabled = savingAs
		val knownPath = image.lastUri != null
		menu.findItem(R.id.action_save_image).isEnabled = savingAs && knownPath
	}

	private fun prepareHistoryOptions(menu: Menu)
	{
		val history = image.history
		menu.findItem(R.id.action_undo).isEnabled = history.canUndo()
		menu.findItem(R.id.action_redo).isEnabled = history.canRedo()
	}

	private fun prepareClipboardOptions(menu: Menu)
	{
		val selection = image.selection
		menu.findItem(R.id.action_cut).isEnabled = !selection.isEmpty
		menu.findItem(R.id.action_copy).isEnabled = !selection.isEmpty
		val clipboard = image.clipboard
		menu.findItem(R.id.action_paste).isEnabled = !clipboard.isEmpty && image.layersAmount < LegacyImage.MAX_LAYERS
	}

	private fun prepareSnapOptions(menu: Menu)
	{
		val helpersManager = image.helpersManager
		val grid: Boolean = helpersManager.grid.isEnabled
		val snapToGrid: Boolean = helpersManager.grid.isSnapToGrid
		val gridItem = menu.findItem(R.id.action_grid)
		gridItem.isChecked = grid
		val snapToGridItem = menu.findItem(R.id.action_snap_to_grid)
		snapToGridItem.isChecked = grid && snapToGrid
		snapToGridItem.isEnabled = grid
	}*/

	fun handleAction(item: MenuItem): Boolean
	{
		when(item.itemId)
		{
			R.id.action_layers -> activity.toggleLayersSheet()
			R.id.action_tool -> activity.togglePropertiesDrawer()
			R.id.action_new_image -> paintViewModel.newImage()
			R.id.action_capture_photo -> paintViewModel.openImageFromCamera()
			R.id.action_open_image -> paintViewModel.openImage()
			R.id.action_save_image -> paintViewModel.saveImage()
			R.id.action_save_image_as -> paintViewModel.saveImageAs()
			R.id.action_undo -> paintViewModel.undo()
			R.id.action_redo -> paintViewModel.redo()
			R.id.action_history -> activity.showHistoryActivity()
			R.id.action_cut -> paintViewModel.cut()
			R.id.action_copy -> paintViewModel.copy()
			R.id.action_paste -> paintViewModel.paste()
			R.id.action_set_zoom -> paintViewModel.changeZoom()
			R.id.action_zoom_default -> paintViewModel.changeZoomToDefault()
			R.id.action_image_center -> paintViewModel.centerImage()
			R.id.action_grid -> paintViewModel.toggleGrid()
			R.id.action_snap_to_grid -> paintViewModel.toggleSnapToGrid()
			R.id.action_resize_image -> paintViewModel.resizeImage()
			R.id.action_scale_image -> paintViewModel.scaleImage()
			R.id.action_flip_image -> paintViewModel.flipImage()
			R.id.action_rotate_image -> paintViewModel.rotateImage()
			R.id.action_flatten_image -> paintViewModel.flattenImage()
			R.id.action_crop_image_by_selection -> paintViewModel.cropImageBySelection()
			R.id.action_new_layer -> paintViewModel.newLayer()
			R.id.action_open_layer -> paintViewModel.openLayer()
			R.id.action_save_layer -> paintViewModel.saveLayer()
			R.id.action_resize_layer -> paintViewModel.resizeLayer()
			R.id.action_scale_layer -> paintViewModel.scaleLayer()
			R.id.action_flip_layer -> paintViewModel.flipLayer()
			R.id.action_rotate_layer -> paintViewModel.rotateLayer()
			R.id.action_drag_layer -> paintViewModel.dragLayer()
			R.id.action_layer_to_image_size -> paintViewModel.fitLayerToImage()
			R.id.action_crop_layer_by_selection -> paintViewModel.cropLayerBySelection()
			R.id.action_select_all -> paintViewModel.selectAll()
			R.id.action_select_nothing -> paintViewModel.selectNothing()
			R.id.action_revert_selection -> paintViewModel.invertSelection()
			R.id.action_colors_invert -> paintViewModel.invertColors()
			R.id.action_colors_brightness -> paintViewModel.changeBrightness()
			R.id.action_color_curves_rgb -> paintViewModel.changeColorCurves(ColorChannel.ColorChannelType.RGB)
			R.id.action_color_curves_hsv -> paintViewModel.changeColorCurves(ColorChannel.ColorChannelType.HSV)
			R.id.action_settings -> activity.showSettingsActivity()
			else -> return false
		}
		return true
	}
}
