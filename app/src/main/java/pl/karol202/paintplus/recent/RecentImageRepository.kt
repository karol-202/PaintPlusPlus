package pl.karol202.paintplus.recent

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface RecentImageRepository
{
	val allRecentImages: Flow<List<RecentImage>>

	suspend fun insertOrUpdateRecentImage(image: RecentImage)

	suspend fun removeRecentImage(imageUri: Uri)
}
