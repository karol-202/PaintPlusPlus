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

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.curves.ColorChannel
import pl.karol202.paintplus.history.ActivityHistoryHelper
import pl.karol202.paintplus.image.Image
import pl.karol202.paintplus.options.*
import pl.karol202.paintplus.util.collectIn
import pl.karol202.paintplus.viewmodel.PaintViewModel
import pl.karol202.paintplus.viewmodel.PaintViewModel.ImageEvent

class ActivityPaintActions(private val activity: ActivityPaint,
                           private val paintViewModel: PaintViewModel)
{
	private val menuInflater = activity.menuInflater
	private val packageManager = activity.packageManager

	private val image = paintViewModel.image

	fun inflateMenu(menu: Menu?)
	{
		menuInflater.inflate(R.menu.menu_paint, menu)

		paintViewModel.imageEventFlow.collectIn(activity.lifecycleScope) {
			when(it)
			{
				ImageEvent.SELECTION_CHANGED -> activity.invalidateOptionsMenu()
				ImageEvent.HISTORY_CHANGED -> activity.invalidateOptionsMenu()
				else -> {}
			}
		}
	}

	fun prepareMenu(menu: Menu)
	{
		val anyDrawerOpen = activity.isAnyDrawerOpen
		setItemsVisibility(menu, !anyDrawerOpen)
		preparePhotoCaptureOption(menu)
		prepareFileOpenOption(menu)
		prepareFileSaveOption(menu)
		prepareHistoryOptions(menu)
		prepareClipboardOptions(menu)
		prepareSnapOptions(menu)
	}

	private fun setItemsVisibility(menu: Menu, visible: Boolean)
	{
		menu.forEach { it.isVisible = visible }
	}

	private fun preparePhotoCaptureOption(menu: Menu)
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
		menu.findItem(R.id.action_paste).isEnabled = !clipboard.isEmpty && image.layersAmount < Image.MAX_LAYERS
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
	}

	fun handleAction(item: MenuItem): Boolean
	{
		when(item.itemId)
		{
			R.id.action_layers ->
				activity.toggleLayersSheet()
			R.id.action_tool ->
				activity.togglePropertiesDrawer()
			R.id.action_new_image ->
				OptionFileNew(activity, image).execute()
			R.id.action_capture_photo ->
				OptionFileCapturePhoto(activity, image).execute()
			R.id.action_open_image ->
				PermissionRequest(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, {
					OptionFileOpen(activity, image, activity.asyncManager, activity::onFileEdit).execute()
				}).execute()
			R.id.action_save_image ->
				PermissionRequest(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, {
					OptionFileSave(activity, image, activity.asyncManager, activity::onFileEdit)
							.execute(image.lastUri, image.lastFormat)
				}).execute()
			R.id.action_save_image_as ->
				PermissionRequest(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, {
					OptionFileSave(activity, image, activity.asyncManager, activity::onFileEdit).execute()
				}).execute()
			R.id.action_undo ->
				image.undo()
			R.id.action_redo ->
				image.redo()
			R.id.action_history ->
				ActivityHistoryHelper(image, activity).startActivity()
			R.id.action_cut ->
			{
				image.cut()
				activity.invalidateOptionsMenu()
			}
			R.id.action_copy ->
			{
				image.copy()
				activity.invalidateOptionsMenu()
			}
			R.id.action_paste ->
				image.paste()
			R.id.action_set_zoom ->
				OptionSetZoom(activity, image).execute()
			R.id.action_zoom_default ->
				image.zoom = 1f
			R.id.action_image_center ->
				image.centerView()
			R.id.action_grid ->
			{
				item.isChecked = !item.isChecked
				image.helpersManager.grid.isEnabled = item.isChecked
			}
			R.id.action_snap_to_grid ->
			{
				item.isChecked = !item.isChecked
				image.helpersManager.grid.isSnapToGrid = item.isChecked
			}
			R.id.action_resize_image ->
				OptionImageResize(activity, image).execute()
			R.id.action_scale_image ->
				OptionImageScale(activity, image).execute()
			R.id.action_flip_image ->
				OptionImageFlip(activity, image).execute()
			R.id.action_rotate_image ->
				OptionImageRotate(activity, image).execute()
			R.id.action_flatten_image ->
				OptionImageFlatten(activity, image).execute()
			R.id.action_crop_image_by_selection ->
				OptionCropImageBySelection(activity, image).execute()
			R.id.action_new_layer ->
				OptionLayerNew(activity, image).execute()
			R.id.action_open_layer ->
				OptionLayerOpen(activity, image, activity.asyncManager).execute()
			R.id.action_save_layer ->
				OptionLayerSave(activity, image, activity.asyncManager, activity::onFileEdit).execute()
			R.id.action_resize_layer ->
				OptionLayerResize(activity, image).execute()
			R.id.action_scale_layer ->
				OptionLayerScale(activity, image).execute()
			R.id.action_flip_layer ->
				OptionLayerFlip(activity, image).execute()
			R.id.action_rotate_layer ->
				OptionLayerRotate(activity, image).execute()
			R.id.action_drag_layer ->
				OptionLayerDrag(activity, image).execute()
			R.id.action_layer_to_image_size ->
				OptionLayerToImageSize(activity, image).execute()
			R.id.action_crop_layer_by_selection ->
				OptionCropLayerBySelection(activity, image).execute()
			R.id.action_select_all ->
				image.selectAll()
			R.id.action_select_nothing ->
				image.selectNothing()
			R.id.action_revert_selection ->
				image.revertSelection()
			R.id.action_colors_invert ->
				OptionColorsInvert(activity, image).execute()
			R.id.action_colors_brightness ->
				OptionColorsBrightness(activity, image).execute()
			R.id.action_color_curves_rgb ->
				OptionColorCurves(activity, image, ColorChannel.ColorChannelType.RGB).execute()
			R.id.action_color_curves_hsv ->
				OptionColorCurves(activity, image, ColorChannel.ColorChannelType.HSV).execute()
			R.id.action_settings ->
				activity.showSettingsActivity()
			else -> return false
		}
		return true
	}
}
