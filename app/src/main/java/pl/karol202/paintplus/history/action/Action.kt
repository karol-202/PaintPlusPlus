package pl.karol202.paintplus.history.action

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

	data class Preset(@StringRes val name: Int,
	                  val previewProvider: () -> Bitmap)
	{
		object Scope
		{
			fun toCommit(action: () -> ToRevert) = action

			fun toRevert(action: () -> ToCommit) = action
		}

		fun commit(action: Scope.() -> () -> ToCommit): ToRevert
		{
			val thumbnail = createThumbnailBitmap(previewProvider())
			return toRevert(thumbnail, Scope.action())
		}

		fun revert(action: Scope.() -> () -> ToRevert): ToCommit
		{
			val thumbnail = createThumbnailBitmap(previewProvider())
			return toCommit(thumbnail, Scope.action())
		}

		fun toCommit(thumbnailBitmap: Bitmap, revert: () -> ToRevert) =
				ToCommit(name, thumbnailBitmap, revert)

		fun toRevert(thumbnailBitmap: Bitmap, commit: () -> ToCommit) =
				ToRevert(name, thumbnailBitmap, commit)
	}

	companion object
	{
		private val maxPreviewSize = squareSize(width = 60)
		private val maxPreviewRect = maxPreviewSize.toRect()

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
