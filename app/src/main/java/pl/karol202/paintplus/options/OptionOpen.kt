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
import android.os.ParcelFileDescriptor
import android.util.Size
import androidx.appcompat.app.AlertDialog
import androidx.exifinterface.media.ExifInterface
import pl.karol202.paintplus.R
import pl.karol202.paintplus.file.FileDescriptorMode
import pl.karol202.paintplus.file.ImageLoader
import pl.karol202.paintplus.file.closeSafely
import pl.karol202.paintplus.file.openFileDescriptor
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.viewmodel.PaintViewModel
import pl.karol202.paintplus.viewmodel.PaintViewModel.ActionRequest

class OptionOpen(private val viewModel: PaintViewModel,
                 private val onBitmapLoaded: (OpenResult?) -> Unit) : Option
{
	data class OpenResult(val uri: Uri,
	                      val bitmap: Bitmap,
	                      val exifOrientation: Int?)

	class ScaleDialog(builder: AlertDialog.Builder,
	                  targetSize: Size,
	                  onAccept: () -> Unit) : Option.SimpleDialog(builder)
	{
		init
		{
			builder.setTitle(R.string.dialog_image_too_big)
			builder.setMessage(context.getString(R.string.dialog_image_too_big_question, targetSize.width, targetSize.height))
			builder.setPositiveButton(R.string.scale_down) { _, _ -> onAccept() }
			builder.setNegativeButton(R.string.cancel, null)
		}
	}

	class RotationDialog(builder: AlertDialog.Builder,
	                     onApply: () -> Unit) : Option.SimpleDialog(builder)
	{
		init
		{
			builder.setTitle(R.string.dialog_exif_rotation)
			builder.setPositiveButton(R.string.rotate) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.no, null)
		}
	}

	private val maxSize = squareSize(GraphicsHelper.getMaxTextureSize())

	override fun execute() = viewModel.makeActionRequest(ActionRequest.OpenFile(this::executeWithUri))

	fun executeWithUri(uri: Uri?)
	{
		val descriptor = uri?.openFileDescriptor(viewModel.context, FileDescriptorMode.READ) ?: return onBitmapLoaded(null)

		val bitmapSize = ImageLoader.getBitmapSize(descriptor.fileDescriptor)
		if(bitmapSize fitsIn maxSize)
			openBitmap(uri, descriptor)
		else viewModel.showDialog {
			ScaleDialog(it, bitmapSize.fitInto(maxSize)) {
				openBitmap(uri, descriptor)
			}
		}
	}

	private fun openBitmap(uri: Uri, fileDescriptor: ParcelFileDescriptor) = viewModel.postLongTask {
		val bitmap = ImageLoader.openBitmap(fileDescriptor.fileDescriptor)?.fitInto(maxSize)
		val exifOrientation = ImageLoader.getExifOrientation(fileDescriptor.fileDescriptor)
		fileDescriptor.closeSafely()
		onBitmapLoaded(bitmap?.let { OpenResult(uri, it, exifOrientation) })
	}

	fun askAboutExifRotation(orientation: Int?, onRotationApply: (Int) -> Unit)
	{
		if(orientation != null && orientation != ExifInterface.ORIENTATION_NORMAL)
			showExifDialog { onRotationApply(orientation) }
	}

	private fun showExifDialog(onApply: () -> Unit) = viewModel.showDialog { RotationDialog(it, onApply) }
}
