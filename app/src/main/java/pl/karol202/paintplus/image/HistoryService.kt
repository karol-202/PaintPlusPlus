package pl.karol202.paintplus.image

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.karol202.paintplus.history.Action

class HistoryService(private val fileService: FileService)
{
	private val _stateFlow = MutableStateFlow(HistoryState())

	val stateFlow: StateFlow<HistoryState> = _stateFlow

	val state get() = _stateFlow.value

	fun undo()
	{
		val (newState, action) = state.poppingPreceding() ?: return
		val toCommit = action.revert()
		_stateFlow.value = newState.withFollowingAction(toCommit)
	}

	fun redo()
	{
		val (newState, action) = state.poppingFollowing() ?: return
		val toRevert = action.commit()
		_stateFlow.value = newState.withPrecedingAction(toRevert)
	}

	fun commitAction(commit: () -> Action.ToRevert): Action.ToRevert
	{
		val toRevert = commit()
		_stateFlow.value = state.withPrecedingAction(toRevert).withNoFollowingActions()

		fileService.onFileChange()
		return toRevert
	}

	fun revertAction(toRevert: Action.ToRevert)
	{
		if(!state.precedingActions.contains(toRevert)) error("No such action")
		do { undo() }
		while(state.precedingActions.contains(toRevert))
	}

	fun clearHistory()
	{
		_stateFlow.value = HistoryState()
	}
}
