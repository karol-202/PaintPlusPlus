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
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import pl.karol202.paintplus.R
import pl.karol202.paintplus.file.ImageLoader
import pl.karol202.paintplus.image.FileService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.viewmodel.PaintViewModel
import java.io.File
import java.io.IOException

class OptionImageCapturePhoto(private val context: Context,
                              private val imageService: ImageService,
                              private val viewService: ViewService,
                              private val viewModel: PaintViewModel,
                              private val fileService: FileService) : Option
{
	private val maxSize = squareSize(GraphicsHelper.maxTextureSize)

	fun execute()
	{
		if(fileService.wasModifiedSinceSave) askAboutChanges() else capturePhoto()
	}

	private fun askAboutChanges() = viewModel.showDialog { builder, _ ->
		OptionImageOpen.UnsavedDialog(builder) { capturePhoto() }
	}

	private fun capturePhoto()
	{
		val photoFile = createPhotoFile() ?: return onError()
		val photoUri = FileProvider.getUriForFile(context, context.packageName, photoFile)
		viewModel.makeActionRequest(PaintViewModel.ActionRequest.CapturePhoto(photoUri) { onCaptureResult(photoUri, it) })
	}

	private fun createPhotoFile() = try
	{
		val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
				?.openFileDescriptor(context, FileDescriptorMode.READ)
				?.useSuppressingIOException { openBitmap(it) }
				?: false
		photoUri.delete(context)
		if(!result) onError()
	}

	private fun openBitmap(fileDescriptor: ParcelFileDescriptor): Boolean
	{
		val bitmap = ImageLoader.openBitmap(fileDescriptor.fileDescriptor)?.fitInto(maxSize) ?: return false
		imageService.openImage(bitmap)
		viewService.centerView()
		fileService.onFileReset()
		return true
	}

	private fun onError() = viewModel.showMessage(R.string.message_cannot_capture_photo)
}
