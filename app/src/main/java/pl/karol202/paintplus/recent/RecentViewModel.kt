package pl.karol202.paintplus.recent

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.karol202.paintplus.util.getDisplayName

class RecentViewModel(application: Application,
                      private val recentImageRepository: RecentImageRepository) : AndroidViewModel(application)
{
	val recentImages = recentImageRepository.allRecentImages

	fun onFileEdit(uri: Uri) = viewModelScope.launch {
		val name = uri.getDisplayName(getApplication()) ?: uri.lastPathSegment ?: return@launch
		val image = RecentImage(uri = uri, name = name, date = System.currentTimeMillis())
		recentImageRepository.insertOrUpdateRecentImage(image)
	}

	fun removeRecentImage(imageUri: Uri) = viewModelScope.launch {
		recentImageRepository.removeRecentImage(imageUri)
	}
}
