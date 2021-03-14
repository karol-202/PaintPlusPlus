package pl.karol202.paintplus.file

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.ultrasonic.android.image.bitmap.util.AndroidBmpUtil
import com.waynejo.androidndkgif.GifEncoder
import pl.karol202.paintplus.R
import java.io.*
import java.util.*

sealed class SaveFormat(val type: Type)
{
	enum class Type
	{
		JPEG, PNG, WEBP, BMP, GIF;

		companion object
		{
			fun fromExtension(fileName: String) = when(fileName.split('.').lastOrNull()?.toLowerCase(Locale.ROOT))
			{
				"jpg", "jpeg" -> JPEG
				"png" -> PNG
				"webp" -> WEBP
				"bmp" -> BMP
				"gif" -> GIF
				else -> null
			}
		}
	}

	abstract class Builtin(type: Type,
	                       private val format: Bitmap.CompressFormat,
	                       private val quality: Int) : SaveFormat(type)
	{
		override fun save(context: Context, outputStream: FileOutputStream, bitmap: Bitmap) =
				bitmap.compress(format, quality, outputStream)
	}

	data class Jpeg(val quality: Int = 95) :
			Builtin(Type.JPEG, Bitmap.CompressFormat.JPEG, quality)

	object Png :
			Builtin(Type.PNG, Bitmap.CompressFormat.PNG, 100)

	@Suppress("DEPRECATION")
	data class Webp(val lossless: Boolean = false) :
			Builtin(Type.WEBP, Bitmap.CompressFormat.WEBP, if(lossless) 100 else 0)

	object Bmp :
			SaveFormat(Type.BMP)
	{
		override fun save(context: Context, outputStream: FileOutputStream, bitmap: Bitmap) =
				AndroidBmpUtil().save(bitmap, outputStream)
	}

	data class Gif(val dithering: Boolean = false) :
			SaveFormat(Type.GIF)
	{
		override fun save(context: Context, outputStream: FileOutputStream, bitmap: Bitmap): Boolean
		{
			val tempFile = context.cacheDir.resolve("temp.gif")
			val result = saveToPath(bitmap, tempFile.absolutePath)
			if(result) tempFile.inputStream().use { it.copyTo(outputStream) }
			tempFile.delete()
			return result
		}

		private fun saveToPath(bitmap: Bitmap, path: String) = GifEncoder().run {
			setDither(dithering)
			init(bitmap.width, bitmap.height, path, GifEncoder.EncodingType.ENCODING_TYPE_FAST)
			encodeFrame(bitmap, 0).also { close() }
		}
	}

	abstract fun save(context: Context, outputStream: FileOutputStream, bitmap: Bitmap): Boolean
}
