package pl.karol202.paintplus.recent

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

private const val DATABASE_NAME = "paintplusplus.local"
private const val DATABASE_VERSION = 2

@Database(entities = [RecentImage::class], version = DATABASE_VERSION, exportSchema = false)
@TypeConverters(UriTypeConverters::class)
abstract class LocalDatabase : RoomDatabase()
{
	companion object
	{
		fun create(context: Context) =
				Room.databaseBuilder(context.applicationContext, LocalDatabase::class.java, DATABASE_NAME)
						.fallbackToDestructiveMigration() //TODO To be removed
						.build()
	}

	abstract fun recentImageDao(): RecentImageDao
}
