package pl.karol202.paintplus.image

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.karol202.paintplus.history.action.HistoryAction

class HistoryService
{
	private val _stateFlow = MutableStateFlow(HistoryState())

	val stateFlow: StateFlow<HistoryState> = _stateFlow

	val state get() = _stateFlow.value

	fun undo()
	{
		val (newState, action) = state.undoing() ?: return
		// TODO Execute action
		_stateFlow.value = newState
	}

	fun redo()
	{
		val (newState, action) = state.redoing() ?: return
		// TODO Execute action
		_stateFlow.value = newState
	}

	fun recordAction(action: HistoryAction)
	{
		_stateFlow.value = state.withNewAction(action)
	}
}
