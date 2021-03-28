package pl.karol202.paintplus.history

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.core.graphics.applyCanvas
import pl.karol202.paintplus.util.*

sealed class Action
{
	class ToCommit(@StringRes override val name: Int,
	               override val thumbnailBitmap: Bitmap,
	               val commit: () -> ToRevert) : Action()

	class ToRevert(@StringRes override val name: Int,
	               override val thumbnailBitmap: Bitmap,
	               val revert: () -> ToCommit) : Action()

	data class NamePreset(@StringRes private val name: Int)
	{
		fun toCommit(thumbnailBitmap: Bitmap, commit: () -> ToRevert) =
				ToCommit(name, ensureThumbnailSmallEnough(thumbnailBitmap), commit)

		fun toRevert(thumbnailBitmap: Bitmap, revert: () -> ToCommit) =
				ToRevert(name, ensureThumbnailSmallEnough(thumbnailBitmap), revert)

		fun withPreview(previewProvider: () -> Bitmap) = NameAndPreviewPreset(this, previewProvider)
	}

	data class NameAndPreviewPreset(private val namePreset: NamePreset,
	                                private val previewProvider: () -> Bitmap)
	{
		object Scope
		{
			fun toCommit(action: () -> ToRevert) = action

			fun toRevert(action: () -> ToCommit) = action
		}

		fun commit(action: Scope.() -> () -> ToCommit): ToRevert
		{
			val thumbnail = createThumbnailBitmap(previewProvider())
			return namePreset.toRevert(thumbnail, Scope.action())
		}

		fun revert(action: Scope.() -> () -> ToRevert): ToCommit
		{
			val thumbnail = createThumbnailBitmap(previewProvider())
			return namePreset.toCommit(thumbnail, Scope.action())
		}
	}

	companion object
	{
		val maxPreviewSize = squareSize(width = 60)
		private val maxPreviewRect = maxPreviewSize.toRect()

		fun namePreset(@StringRes name: Int) = NamePreset(name)

		fun ensureThumbnailSmallEnough(bitmap: Bitmap) =
				if(bitmap.size fitsIn maxPreviewSize) bitmap
				else createThumbnailBitmap(bitmap)

		fun createThumbnailBitmap(bitmap: Bitmap): Bitmap
		{
			val dstRect = bitmap.size.fitInto(maxPreviewSize).toRect().centerInside(maxPreviewRect)
			val dstBitmap = Bitmap.createBitmap(maxPreviewSize.width, maxPreviewSize.height, Bitmap.Config.ARGB_8888)
			return dstBitmap.applyCanvas {
				drawBitmap(bitmap, null, dstRect, null)
			}
		}
	}

	abstract val name: Int
	abstract val thumbnailBitmap: Bitmap
}
