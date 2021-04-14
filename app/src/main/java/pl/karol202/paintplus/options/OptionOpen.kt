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
import android.util.Size
import androidx.appcompat.app.AlertDialog
import androidx.exifinterface.media.ExifInterface
import pl.karol202.paintplus.R
import pl.karol202.paintplus.file.ImageLoader
import pl.karol202.paintplus.image.EffectsService
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.viewmodel.PaintViewModel.ActionRequest

private const val MIME_FILTER = "image/*"

class OptionOpen(private val context: Context,
                 private val effectsService: EffectsService) : Option
{
	sealed class OpenResult
	{
		data class Success(val uri: Uri,
		                   val bitmap: Bitmap,
		                   val exifOrientation: Int?) : OpenResult()

		object Failed : OpenResult()
	}

	private class ScaleDialog(builder: AlertDialog.Builder,
	                          targetSize: Size,
	                          onAccept: () -> Unit) : Option.BasicDialog(builder)
	{
		init
		{
			builder.setTitle(R.string.dialog_image_too_big)
			builder.setMessage(context.getString(R.string.dialog_image_too_big_question, targetSize.width, targetSize.height))
			builder.setPositiveButton(R.string.scale_down) { _, _ -> onAccept() }
			builder.setNegativeButton(R.string.cancel, null)
		}
	}

	private class RotationDialog(builder: AlertDialog.Builder,
	                             onApply: () -> Unit,
	                             onDismiss: () -> Unit) : Option.BasicDialog(builder)
	{
		init
		{
			builder.setTitle(R.string.dialog_exif_rotation)
			builder.setPositiveButton(R.string.rotate) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.no) { _, _ -> onDismiss() }
			builder.setOnDismissListener { onDismiss() }
		}
	}

	private val maxSize = squareSize(GraphicsHelper.maxTextureSize)

	fun execute(onResult: (OpenResult) -> Unit) =
			effectsService.makeActionRequest(ActionRequest.OpenFile(listOf(MIME_FILTER)) { onUriSelected(onResult, it) })

	private fun onUriSelected(onResult: (OpenResult) -> Unit, uri: Uri?)
	{
		uri?.takePersistablePermission(context) ?: return
		executeWithUri(onResult, uri)
	}

	fun executeWithUri(onResult: (OpenResult) -> Unit, uri: Uri)
	{
		val bitmapSize = uri.openFileDescriptor(context, FileDescriptorMode.READ)?.useSuppressingIOException {
			ImageLoader.getBitmapSize(it.fileDescriptor)
		} ?: return onResult(OpenResult.Failed)

		if(bitmapSize fitsIn maxSize)
			openBitmap(onResult, uri)
		else effectsService.showDialog { builder, _ ->
			ScaleDialog(builder, bitmapSize.fitInto(maxSize)) {
				openBitmap(onResult, uri)
			}
		}
	}

	private fun openBitmap(onResult: (OpenResult) -> Unit, uri: Uri) = effectsService.postLongTask {
		val result = uri.openFileDescriptor(context, FileDescriptorMode.READ)?.useSuppressingIOException { desc ->
			val bitmap = ImageLoader.openBitmap(desc.fileDescriptor)?.fitInto(maxSize)
			val exifOrientation = ImageLoader.getExifOrientation(desc.fileDescriptor)

			bitmap?.let { OpenResult.Success(uri, it, exifOrientation) }
		}
		onResult(result ?: OpenResult.Failed)
	}

	fun askAboutExifRotation(orientation: Int?, onRotationApply: (Int) -> Unit, onNoRotation: () -> Unit = {})
	{
		if(orientation != null && orientation != ExifInterface.ORIENTATION_NORMAL)
			showExifDialog({ onRotationApply(orientation) }, onNoRotation)
		else onNoRotation()
	}

	private fun showExifDialog(onApply: () -> Unit, onNoRotation: () -> Unit) =
			effectsService.showDialog { builder, _ ->
				RotationDialog(builder, onApply, onNoRotation)
			}
}
