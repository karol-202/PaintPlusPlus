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
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.DialogFormatGifBinding
import pl.karol202.paintplus.databinding.DialogFormatJpegBinding
import pl.karol202.paintplus.file.*
import pl.karol202.paintplus.image.FileService
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionSave(private val context: Context,
                 private val viewModel: PaintViewModel,
                 private val fileService: FileService) : Option
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

	private abstract class FormatDialog<F : SaveFormat, B : ViewBinding>(builder: AlertDialog.Builder,
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

	private class JpegFormatDialog(builder: AlertDialog.Builder,
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

	private class GifFormatDialog(builder: AlertDialog.Builder,
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

	fun execute(bitmap: Bitmap, onResult: (SaveResult) -> Unit) =
			viewModel.makeActionRequest(PaintViewModel.ActionRequest.SaveFile(getSuggestedName()) {
				onUriSelected(bitmap, onResult, it)
			})

	private fun getSuggestedName() = fileService.lastUri?.getDisplayName(context) ?: ""

	private fun onUriSelected(bitmap: Bitmap, onResult: (SaveResult) -> Unit, uri: Uri?)
	{
		uri?.takePersistablePermission(context) ?: return
		executeWithUri(bitmap, onResult, uri)
	}

	fun executeWithUri(bitmap: Bitmap, onResult: (SaveResult) -> Unit, uri: Uri)
	{
		val formatType = getFileFormatType(uri) ?: return onError(onResult, uri, SaveResult.Failed.UnsupportedFormat)
		executeWithUriAndFormatType(bitmap, onResult, uri, formatType)
	}

	private fun getFileFormatType(uri: Uri) = uri.getDisplayName(context)?.let(SaveFormat.Type::fromExtension)

	private fun executeWithUriAndFormatType(bitmap: Bitmap, onResult: (SaveResult) -> Unit, uri: Uri,
	                                        formatType: SaveFormat.Type) = when(formatType)
	{
		SaveFormat.Type.JPEG -> viewModel.showDialog {
			JpegFormatDialog(it, SaveFormat.Jpeg()) { format ->
				executeWithUriAndFormat(bitmap, onResult, uri, format)
			}
		}
		SaveFormat.Type.PNG -> executeWithUriAndFormat(bitmap, onResult, uri, SaveFormat.Png)
		SaveFormat.Type.WEBP -> executeWithUriAndFormat(bitmap, onResult, uri, SaveFormat.Webp())
		SaveFormat.Type.BMP -> executeWithUriAndFormat(bitmap, onResult, uri, SaveFormat.Bmp)
		SaveFormat.Type.GIF -> viewModel.showDialog {
			GifFormatDialog(it, SaveFormat.Gif()) { format ->
				executeWithUriAndFormat(bitmap, onResult, uri, format)
			}
		}
	}

	fun executeWithUriAndFormat(bitmap: Bitmap, onResult: (SaveResult) -> Unit, uri: Uri, format: SaveFormat) = viewModel.postLongTask {
		uri.openFileDescriptor(context, FileDescriptorMode.WRITE)?.useSuppressingIOException { desc ->
			val result = format.save(context, desc.fileDescriptor.toFileOutputStream(), bitmap)
			if(result) Unit else null
		} ?: return@postLongTask onError(onResult, uri, SaveResult.Failed.CannotSave)
		onResult(SaveResult.Success(uri, format))
	}

	private fun onError(onResult: (SaveResult) -> Unit, uriToDelete: Uri?, error: SaveResult.Failed)
	{
		uriToDelete?.delete(context)
		onResult(error)
	}
}
