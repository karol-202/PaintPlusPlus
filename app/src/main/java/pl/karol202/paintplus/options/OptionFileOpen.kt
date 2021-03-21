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
import androidx.appcompat.app.AlertDialog
import androidx.exifinterface.media.ExifInterface
import pl.karol202.paintplus.R
import pl.karol202.paintplus.image.FlipDirection
import pl.karol202.paintplus.image.LegacyImage
import pl.karol202.paintplus.image.RotationAmount
import pl.karol202.paintplus.recent.RecentViewModel
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionFileOpen(private val recentViewModel: RecentViewModel,
                     private val paintViewModel: PaintViewModel,
                     private val openOption: OptionOpen) : Option
{
	class UnsavedDialog(builder: AlertDialog.Builder,
	                    onApply: () -> Unit) : Option.BasicDialog(builder)
	{
		init
		{
			builder.setTitle(R.string.dialog_are_you_sure)
			builder.setMessage(R.string.dialog_unsaved_changes)
			builder.setPositiveButton(R.string.dialog_open_file_positive) { _, _ -> onApply() }
			builder.setNegativeButton(R.string.cancel, null)
		}
	}

	fun execute()
	{
		if(paintViewModel.image.wasModifiedSinceLastSave()) askAboutChanges() else executeWithoutSaving()
	}

	private fun askAboutChanges() = paintViewModel.showDialog {
		UnsavedDialog(it) { executeWithoutSaving() }
	}

	fun executeWithoutSaving() = openOption.execute(this::onResult)

	fun executeWithUri(uri: Uri) = openOption.executeWithUri(this::onResult, uri)

	private fun onResult(result: OptionOpen.OpenResult) = when(result)
	{
		is OptionOpen.OpenResult.Success -> openImageFromBitmap(result.uri, result.bitmap, result.exifOrientation)
		is OptionOpen.OpenResult.Failed -> paintViewModel.showMessage(R.string.message_cannot_open_file)
	}

	private fun openImageFromBitmap(uri: Uri, bitmap: Bitmap, orientation: Int?)
	{
		paintViewModel.image.openImage(bitmap)
		paintViewModel.image.lastUri = uri
		paintViewModel.image.centerView()
		recentViewModel.onFileEdit(uri)
		openOption.askAboutExifRotation(orientation) { rotateImage(paintViewModel.image, it) }
	}

	private fun rotateImage(image: LegacyImage, exifOrientation: Int) = when(exifOrientation)
	{
		ExifInterface.ORIENTATION_ROTATE_90 -> image.rotate(RotationAmount.ANGLE_90)
		ExifInterface.ORIENTATION_ROTATE_180 -> image.rotate(RotationAmount.ANGLE_180)
		ExifInterface.ORIENTATION_ROTATE_270 -> image.rotate(RotationAmount.ANGLE_270)
		ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> image.flip(FlipDirection.HORIZONTALLY)
		ExifInterface.ORIENTATION_FLIP_VERTICAL -> image.flip(FlipDirection.VERTICALLY)
		ExifInterface.ORIENTATION_TRANSPOSE ->
		{
			image.rotate(RotationAmount.ANGLE_90)
			image.flip(FlipDirection.HORIZONTALLY)
		}
		ExifInterface.ORIENTATION_TRANSVERSE ->
		{
			image.rotate(RotationAmount.ANGLE_270)
			image.flip(FlipDirection.HORIZONTALLY)
		}
		else -> {}
	}
}
