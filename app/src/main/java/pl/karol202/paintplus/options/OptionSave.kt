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
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogFormatGifBinding
import pl.karol202.paintplus.databinding.DialogFormatJpegBinding
import pl.karol202.paintplus.file.*
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionSave(private val viewModel: PaintViewModel,
                 private val bitmap: Bitmap,
                 private val onResult: (SaveResult) -> Unit) : Option
{
	sealed class SaveResult
	{
		data class Success(val uri: Uri,
		                   val format: SaveFormat) : SaveResult()

		sealed class Failed : SaveResult()
		{
			object CannotSave : Failed()
			object UnsupportedFormat : Failed()
		}
	}

	abstract class FormatDialog<F : SaveFormat, B : ViewBinding>(builder: AlertDialog.Builder,
	                                                             viewBinding: (LayoutInflater) -> B,
	                                                             initialFormat: F,
	                                                             onAccept: (SaveFormat) -> Unit) :
			Option.LayoutDialog<B>(builder, viewBinding)
	{
		protected var format = initialFormat

		init
		{
			builder.setPositiveButton(R.string.save) { _, _ -> onAccept(format) }
			builder.setNegativeButton(R.string.cancel, null)
		}
	}

	class JpegFormatDialog(builder: AlertDialog.Builder,
	                       initialFormat: SaveFormat.Jpeg,
	                       onAccept: (SaveFormat) -> Unit) :
			FormatDialog<SaveFormat.Jpeg, DialogFormatJpegBinding>(builder, DialogFormatJpegBinding::inflate,
			                                                       initialFormat, onAccept)
	{
		init
		{
			builder.setTitle(R.string.dialog_jpeg_save_settings)

			views.seekBarFormatJpegQuality.progress = format.quality
			views.seekBarFormatJpegQuality.setOnValueChangeListener(this::onQualityChanged)

			updateText()
		}

		private fun onQualityChanged(quality: Int)
		{
			format = format.copy(quality = quality)
			updateText()
		}

		private fun updateText()
		{
			views.textFormatJpegQuality.text = format.quality.toString()
		}
	}

	class GifFormatDialog(builder: AlertDialog.Builder,
	                      initialFormat: SaveFormat.Gif,
	                      onAccept: (SaveFormat) -> Unit) :
			FormatDialog<SaveFormat.Gif, DialogFormatGifBinding>(builder, DialogFormatGifBinding::inflate, initialFormat, onAccept)
	{
		init
		{
			builder.setTitle(R.string.dialog_gif_save_settings)

			views.checkFormatGifDithering.isChecked = format.dithering
			views.checkFormatGifDithering.setOnCheckedChangeListener { _, checked ->
				format = format.copy(dithering = checked)
			}
		}
	}

	fun execute() =
			viewModel.makeActionRequest(PaintViewModel.ActionRequest.SaveFile(getSuggestedName(), this::onUriSelected))

	private fun getSuggestedName() = viewModel.image.lastUri?.getDisplayName(viewModel.context) ?: ""

	private fun onUriSelected(uri: Uri?)
	{
		uri?.takePersistablePermission(viewModel.context) ?: return
		executeWithUri(uri)
	}

	fun executeWithUri(uri: Uri)
	{
		val formatType = getFileFormatType(uri) ?: return onError(uri, SaveResult.Failed.UnsupportedFormat)
		executeWithUriAndFormatType(uri, formatType)
	}

	private fun getFileFormatType(uri: Uri) = uri.getDisplayName(viewModel.context)?.let(ImageLoader::getFormatByExtension)

	private fun executeWithUriAndFormatType(uri: Uri, formatType: SaveFormat.Type) = when(formatType)
	{
		SaveFormat.Type.JPEG -> viewModel.showDialog {
			JpegFormatDialog(it, SaveFormat.Jpeg()) { format ->
				executeWithUriAndFormat(uri, format)
			}
		}
		SaveFormat.Type.PNG -> executeWithUriAndFormat(uri, SaveFormat.Png)
		SaveFormat.Type.WEBP -> executeWithUriAndFormat(uri, SaveFormat.Webp())
		SaveFormat.Type.BMP -> executeWithUriAndFormat(uri, SaveFormat.Bmp)
		SaveFormat.Type.GIF -> viewModel.showDialog {
			GifFormatDialog(it, SaveFormat.Gif()) { format ->
				executeWithUriAndFormat(uri, format)
			}
		}
	}

	fun executeWithUriAndFormat(uri: Uri, format: SaveFormat) = viewModel.postLongTask {
		uri.openFileDescriptor(viewModel.context, FileDescriptorMode.WRITE)?.useSuppressingIOException { desc ->
			val result = format.save(viewModel.context, desc.fileDescriptor.toFileOutputStream(), bitmap)
			if(result) Unit else null
		} ?: return@postLongTask onError(uri, SaveResult.Failed.CannotSave)
		onResult(SaveResult.Success(uri, format))
	}

	private fun onError(uriToDelete: Uri?, error: SaveResult.Failed)
	{
		uriToDelete?.delete(viewModel.context)
		onResult(error)
	}
}
