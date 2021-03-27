package pl.karol202.paintplus.image

import kotlinx.coroutines.flow.MutableStateFlow

class ClipboardService
{
	private val _contentFlow = MutableStateFlow<ClipboardContent?>(null)

	val content get() = _contentFlow.value

	fun offsetClipboard(x: Int, y: Int) = setContent(content?.translated(x, y))

	fun setContent(content: ClipboardContent?)
	{
		_contentFlow.value = content
	}
}
