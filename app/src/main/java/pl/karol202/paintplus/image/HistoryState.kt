package pl.karol202.paintplus.image

import pl.karol202.paintplus.history.action.HistoryAction

data class HistoryState(val precedingActions: List<HistoryAction> = emptyList(),
                        val followingActions: List<HistoryAction> = emptyList())
{
	val canUndo = precedingActions.isNotEmpty()
	val canRedo = followingActions.isNotEmpty()

	val precedingSize = precedingActions.size
	val followingSize = followingActions.size

	fun undoing(): Pair<HistoryState, HistoryAction>?
	{
		val action = precedingActions.lastOrNull() ?: return null
		val newState = copy(precedingActions = precedingActions.dropLast(1),
		                    followingActions = followingActions + action)
		return newState to action
	}

	fun redoing(): Pair<HistoryState, HistoryAction>?
	{
		val action = followingActions.lastOrNull() ?: return null
		val newState = copy(precedingActions = precedingActions + action,
		                    followingActions = followingActions.dropLast(1))
		return newState to action
	}

	fun withNewAction(action: HistoryAction) = copy(precedingActions = precedingActions + action,
	                                                followingActions = emptyList())
}
