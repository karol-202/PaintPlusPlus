package pl.karol202.paintplus.recent

import android.net.Uri

class RoomRecentImageRepository(private val dao: RecentImageDao) : RecentImageRepository
{
	override val allRecentImages = dao.getAllRecentImages()

	override suspend fun insertOrUpdateRecentImage(image: RecentImage) = dao.insertOrUpdateRecentImage(image)

	override suspend fun removeRecentImage(imageUri: Uri) = dao.deleteRecentImageByUri(imageUri)
}
