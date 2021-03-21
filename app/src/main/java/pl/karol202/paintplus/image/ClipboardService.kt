package pl.karol202.paintplus.image

import kotlinx.coroutines.flow.MutableStateFlow

class ClipboardService
{
	private val _contentFlow = MutableStateFlow<ClipboardContent?>(null)
}
