package pl.karol202.paintplus.recent

import android.net.Uri
import androidx.room.TypeConverter

class UriTypeConverters
{
	@TypeConverter
	fun uriToString(uri: Uri) = uri.toString()

	@TypeConverter
	fun stringToUri(string: String) = Uri.parse(string)
}
