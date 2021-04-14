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
package pl.karol202.paintplus.options

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.*
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.getDisplayName
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionLayerOpen(private val context: Context,
                      private val imageService: ImageService,
                      private val historyService: HistoryService,
                      private val effectsService: EffectsService,
                      private val openOption: OptionOpen) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_layer_add)

	fun execute() = openOption.execute(this::onResult)

	private fun onResult(result: OptionOpen.OpenResult) = when(result)
	{
		is OptionOpen.OpenResult.Success -> onUriSelected(result.uri, result.bitmap, result.exifOrientation)
		is OptionOpen.OpenResult.Failed -> effectsService.showMessage(R.string.message_cannot_open_file)
	}

	private fun onUriSelected(uri: Uri, bitmap: Bitmap, orientation: Int?) =
			openOption.askAboutExifRotation(orientation = orientation,
			                                onRotationApply = { onUriAndRotationSelected(uri, bitmap, it) },
			                                onNoRotation = { onUriAndRotationSelected(uri, bitmap, null) })

	private fun onUriAndRotationSelected(uri: Uri, bitmap: Bitmap, orientation: Int?)
	{
		if(!imageService.image.canAddMoreLayers) return
		historyService.commitAction { commit(uri, bitmap, orientation) }
	}

	private fun commit(uri: Uri, bitmap: Bitmap, orientation: Int?): Action.ToRevert
	{
		val oldImage = imageService.image
		val layer = Layer.create(0, 0, createName(uri), bitmap).rotatedByExif(orientation)
		imageService.editImage { withLayerAdded(layer, autoSelect = true) }
		return actionPreset.toRevert(layer.bitmap) { revert(uri, bitmap, orientation, layer, oldImage) }
	}

	private fun revert(uri: Uri, bitmap: Bitmap, orientation: Int?, newLayer: Layer, oldImage: Image): Action.ToCommit
	{
		imageService.setImage(oldImage)
		return actionPreset.toCommit(newLayer.bitmap) { commit(uri, bitmap, orientation) }
	}

	private fun createName(uri: Uri) =
			uri.getDisplayName(context) ?: context.getString(R.string.opened_layer_name)

	private fun Layer.rotatedByExif(exifOrientation: Int?) = when(exifOrientation)
	{
		ExifInterface.ORIENTATION_ROTATE_90 -> rotated(90f)
		ExifInterface.ORIENTATION_ROTATE_180 -> rotated(180f)
		ExifInterface.ORIENTATION_ROTATE_270 -> rotated(270f)
		ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipped(FlipDirection.HORIZONTALLY)
		ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipped(FlipDirection.VERTICALLY)
		ExifInterface.ORIENTATION_TRANSPOSE -> rotated(90f).flipped(FlipDirection.HORIZONTALLY)
		ExifInterface.ORIENTATION_TRANSVERSE -> rotated(-90f).flipped(FlipDirection.HORIZONTALLY)
		else -> this
	}
}
