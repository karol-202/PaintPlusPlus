package pl.karol202.paintplus.image

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SelectionService
{
	private val _selectionFlow = MutableStateFlow(Selection.empty)

	val selectionFlow: StateFlow<Selection> = _selectionFlow
}
