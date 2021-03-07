package pl.karol202.paintplus.recent

import android.net.Uri
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentImageDao
{
	@Query("SELECT * FROM RecentImage ORDER BY date DESC")
	fun getAllRecentImages(): Flow<List<RecentImage>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrUpdateRecentImage(image: RecentImage)

	@Query("DELETE FROM RecentImage WHERE uri = :imageUri")
	suspend fun deleteRecentImageByUri(imageUri: Uri)
}
