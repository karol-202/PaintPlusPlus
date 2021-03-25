package pl.karol202.paintplus.image

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.karol202.paintplus.file.SaveFormat
import pl.karol202.paintplus.recent.RecentImage
import pl.karol202.paintplus.recent.RecentImageRepository
import pl.karol202.paintplus.util.getDisplayName

class FileService(private val context: Context,
                  private val recentImageRepository: RecentImageRepository)
{
	private data class CurrentFile(val uri: Uri,
	                               val saveFormat: SaveFormat? = null)

	private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	private val _currentFileFlow = MutableStateFlow<CurrentFile?>(null)
	private val _changedSinceSaveFlow = MutableStateFlow(false)

	val lastUri get() = _currentFileFlow.value?.uri
	val wasModifiedSinceSave get() = _changedSinceSaveFlow.value

	fun onFileReset()
	{
		_currentFileFlow.value = null
		_changedSinceSaveFlow.value = false
	}

	fun onFileChange()
	{
		_changedSinceSaveFlow.value = true
	}

	fun onFileOpen(uri: Uri) = onFileEdit(uri, null)

	fun onFileSave(uri: Uri, format: SaveFormat) = onFileEdit(uri, format)

	private fun onFileEdit(uri: Uri, format: SaveFormat?)
	{
		saveRecentImage(uri)
		_currentFileFlow.value = CurrentFile(uri, format)
		_changedSinceSaveFlow.value = false
	}

	private fun saveRecentImage(uri: Uri)
	{
		val name = uri.getDisplayName(context) ?: uri.lastPathSegment ?: return
		val image = RecentImage(uri = uri, name = name, date = System.currentTimeMillis())
		ioScope.launch {
			recentImageRepository.insertOrUpdateRecentImage(image)
		}
	}
}
