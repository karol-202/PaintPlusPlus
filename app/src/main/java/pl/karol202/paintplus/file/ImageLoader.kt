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
package pl.karol202.paintplus.file

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Size
import androidx.exifinterface.media.ExifInterface
import java.io.FileDescriptor
import java.io.IOException
import java.util.*

object ImageLoader
{
	@JvmStatic
	fun openBitmap(fileDescriptor: FileDescriptor): Bitmap? = BitmapFactory.decodeFileDescriptor(fileDescriptor)

	fun getBitmapSize(fileDescriptor: FileDescriptor): Size
	{
		val options = BitmapFactory.Options()
		options.inJustDecodeBounds = true
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
		return Size(options.outWidth, options.outHeight)
	}

	fun getExifOrientation(fileDescriptor: FileDescriptor) =
			try
			{
				ExifInterface(fileDescriptor)
						.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
						.takeIf { it != ExifInterface.ORIENTATION_UNDEFINED }
			}
			catch(e: IOException)
			{
				e.printStackTrace()
				null
			}

	fun getFormatByExtension(fileName: String) = when(fileName.split('.').lastOrNull()?.toLowerCase(Locale.ROOT))
	{
		"jpg", "jpeg" -> SaveFormat.Type.JPEG
		"png" -> SaveFormat.Type.PNG
		"webp" -> SaveFormat.Type.WEBP
		"bmp" -> SaveFormat.Type.BMP
		"gif" -> SaveFormat.Type.GIF
		else -> null
	}
}
