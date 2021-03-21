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

import android.graphics.Bitmap
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.action.ActionLayerAdd
import pl.karol202.paintplus.image.FlipDirection
import pl.karol202.paintplus.image.layer.Layer
import pl.karol202.paintplus.util.getDisplayName
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionLayerOpen(private val viewModel: PaintViewModel,
                      private val openOption: OptionOpen) : Option
{
	fun execute() = openOption.execute(this::onResult)

	private fun onResult(result: OptionOpen.OpenResult) = when(result)
	{
		is OptionOpen.OpenResult.Success -> addNewLayer(result.uri, result.bitmap, result.exifOrientation)
		is OptionOpen.OpenResult.Failed -> viewModel.showMessage(R.string.message_cannot_open_file)
	}

	private fun addNewLayer(uri: Uri, bitmap: Bitmap, orientation: Int?)
	{
		val layer = Layer(0, 0, createName(uri), bitmap)
		if(!viewModel.image.addLayer(layer, 0)) return viewModel.showMessage(R.string.too_many_layers)
		addHistoryAction(layer)
		openOption.askAboutExifRotation(orientation) { rotateLayer(layer, it) }
	}

	private fun createName(uri: Uri) =
			uri.getDisplayName(viewModel.context) ?: viewModel.context.getString(R.string.opened_layer_name)

	private fun addHistoryAction(layer: Layer) =
			ActionLayerAdd(viewModel.image).apply {
				setLayerAfterAdding(layer)
				applyAction()
			}

	private fun rotateLayer(layer: Layer, exifOrientation: Int) = when(exifOrientation)
	{
		ExifInterface.ORIENTATION_ROTATE_90 -> layer.rotate(90f)
		ExifInterface.ORIENTATION_ROTATE_180 -> layer.rotate(180f)
		ExifInterface.ORIENTATION_ROTATE_270 -> layer.rotate(270f)
		ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> layer.flip(FlipDirection.HORIZONTALLY)
		ExifInterface.ORIENTATION_FLIP_VERTICAL -> layer.flip(FlipDirection.VERTICALLY)
		ExifInterface.ORIENTATION_TRANSPOSE ->
		{
			layer.rotate(90f)
			layer.flip(FlipDirection.HORIZONTALLY)
		}
		ExifInterface.ORIENTATION_TRANSVERSE ->
		{
			layer.rotate(-90f)
			layer.flip(FlipDirection.HORIZONTALLY)
		}
		else -> {}
	}
}
