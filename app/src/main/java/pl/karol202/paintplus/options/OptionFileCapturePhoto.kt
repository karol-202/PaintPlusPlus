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

import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import pl.karol202.paintplus.R
import pl.karol202.paintplus.file.ImageLoader
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.viewmodel.PaintViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class OptionFileCapturePhoto(private val viewModel: PaintViewModel) : Option
{
	private val maxSize = squareSize(GraphicsHelper.getMaxTextureSize())

	fun execute()
	{
		if(viewModel.image.wasModifiedSinceLastSave()) askAboutChanges() else capturePhoto()
	}

	private fun askAboutChanges() = viewModel.showDialog {
		OptionFileOpen.UnsavedDialog(it) { capturePhoto() }
	}

	private fun capturePhoto()
	{
		val photoFile = createPhotoFile() ?: return onError()
		val photoUri = FileProvider.getUriForFile(viewModel.context, viewModel.context.packageName, photoFile)
		viewModel.makeActionRequest(PaintViewModel.ActionRequest.CapturePhoto(photoUri) { onCaptureResult(photoUri, it) })
	}

	private fun createPhotoFile() = try
	{
		val directory = viewModel.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
		File.createTempFile("captured", ".jpeg", directory)
	}
	catch(e: IOException)
	{
		e.printStackTrace()
		null
	}

	private fun onCaptureResult(photoUri: Uri, captureResult: Boolean)
	{
		val result = photoUri.takeIf { captureResult }
				?.openFileDescriptor(viewModel.context, FileDescriptorMode.READ)
				?.useSuppressingIOException { openBitmap(it) }
				?: false
		photoUri.delete(viewModel.context)
		if(!result) onError()
	}

	private fun openBitmap(fileDescriptor: ParcelFileDescriptor): Boolean
	{
		val bitmap = ImageLoader.openBitmap(fileDescriptor.fileDescriptor)?.fitInto(maxSize) ?: return false
		viewModel.image.openImage(bitmap)
		viewModel.image.centerView()
		return true
	}

	private fun onError() = viewModel.showMessage(R.string.message_cannot_capture_photo)
}
