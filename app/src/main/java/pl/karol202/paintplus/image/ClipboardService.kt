package pl.karol202.paintplus.image

import kotlinx.coroutines.flow.MutableStateFlow

class ClipboardService
{
	private val _contentFlow = MutableStateFlow<ClipboardContent?>(null)

	private val content get() = _contentFlow.value

	fun setContent(content: ClipboardContent?)
	{
		_contentFlow.value = content
	}

	fun offsetClipboard(x: Int, y: Int) = setContent(content?.translated(x, y))
}
