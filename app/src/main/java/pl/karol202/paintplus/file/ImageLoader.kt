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

import pl.karol202.paintplus.ErrorHandler.report
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import pl.karol202.paintplus.file.BitmapSaveFormat.JPEGSaveFormat
import pl.karol202.paintplus.file.BitmapSaveFormat.PNGSaveFormat
import pl.karol202.paintplus.file.BitmapSaveFormat.WEBPSaveFormat
import pl.karol202.paintplus.file.BitmapSaveFormat.BMPSaveFormat
import pl.karol202.paintplus.file.BitmapSaveFormat.GIFSaveFormat
import android.graphics.Bitmap.CompressFormat
import android.graphics.Point
import android.util.Size
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import com.ultrasonic.android.image.bitmap.util.AndroidBmpUtil
import kotlin.Throws
import com.waynejo.androidndkgif.GifEncoder
import pl.karol202.paintplus.util.fitInto
import pl.karol202.paintplus.util.fitsIn
import pl.karol202.paintplus.util.size
import java.io.*
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.math.floor
import kotlin.math.max

object ImageLoader
{
	private var temporaryFile: File? = null

	// LEGACY
	fun setTemporaryFileLocation(location: File?)
	{
		temporaryFile = File(location, "tmp")
	}

	@JvmStatic
	fun openBitmap(fileDescriptor: FileDescriptor): Bitmap? = BitmapFactory.decodeFileDescriptor(fileDescriptor)

	@JvmStatic
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

	@JvmStatic
	fun getFormat(name: String): BitmapSaveFormat?
	{
		val parts = name.split("\\.".toRegex()).toTypedArray()
		val extension = parts[parts.size - 1].toLowerCase()
		return when(extension)
		{
			"jpg", "jpeg" -> JPEGSaveFormat()
			"png" -> PNGSaveFormat()
			"webp" -> WEBPSaveFormat()
			"bmp" -> BMPSaveFormat()
			"gif" -> GIFSaveFormat()
			else -> null
		}
	}

	@JvmStatic
	fun saveBitmap(bitmap: Bitmap, fileDescriptor: FileDescriptor?, format: BitmapSaveFormat): BitmapSaveResult.Result
	{
		var fos: FileOutputStream? = null
		try
		{
			fos = FileOutputStream(fileDescriptor)
			if(!compressBitmap(bitmap, fos, format)) throw RuntimeException("Cannot compress bitmap.")
		}
		catch(e: Exception)
		{
			report(e)
			return BitmapSaveResult.Result.ERROR
		}
		finally
		{
			try
			{
				fos?.close()
			}
			catch(e: IOException)
			{
				report(e)
			}
		}
		return BitmapSaveResult.Result.SUCCESSFUL
	}

	private fun compressBitmap(bitmap: Bitmap, outputStream: FileOutputStream, format: BitmapSaveFormat): Boolean
	{
		if(format is JPEGSaveFormat) return bitmap.compress(CompressFormat.JPEG, format.quality, outputStream) else if(format is PNGSaveFormat) return bitmap.compress(CompressFormat.PNG, 100, outputStream) else if(format is WEBPSaveFormat) return bitmap.compress(CompressFormat.WEBP, 100, outputStream) else if(format is BMPSaveFormat) return compressToBmp(bitmap, outputStream) else if(format is GIFSaveFormat) return tryToCompressToGif(bitmap, outputStream, format)
		return false
	}

	private fun compressToBmp(bitmap: Bitmap, outputStream: FileOutputStream): Boolean
	{
		val util = AndroidBmpUtil()
		return util.save(bitmap, outputStream)
	}

	private fun tryToCompressToGif(bitmap: Bitmap, outputStream: FileOutputStream, format: GIFSaveFormat): Boolean
	{
		return try
		{
			compressToGif(bitmap, outputStream, format)
		}
		catch(e: IOException)
		{
			report(e)
			false
		}
	}

	@Throws(IOException::class)
	private fun compressToGif(bitmap: Bitmap, outputStream: FileOutputStream, format: GIFSaveFormat): Boolean
	{
		val result = compressToGif(bitmap, temporaryFile!!.absolutePath, format)
		if(result)
		{
			val inputStream = FileInputStream(temporaryFile)
			copyStream(inputStream, outputStream)
			inputStream.close()
		}
		temporaryFile!!.delete()
		return result
	}

	@Throws(FileNotFoundException::class)
	private fun compressToGif(bitmap: Bitmap, path: String, format: GIFSaveFormat): Boolean
	{
		val result: Boolean
		val encoder = GifEncoder()
		encoder.setDither(format.dithering)
		encoder.init(bitmap.width, bitmap.height, path, GifEncoder.EncodingType.ENCODING_TYPE_FAST)
		result = encoder.encodeFrame(bitmap, 0)
		encoder.close()
		return result
	}

	@Throws(IOException::class)
	private fun copyStream(`is`: InputStream, os: OutputStream)
	{
		val buffer = ByteArray(1024)
		var length: Int
		while(`is`.read(buffer).also { length = it } > 0) os.write(buffer, 0, length)
	}
}
