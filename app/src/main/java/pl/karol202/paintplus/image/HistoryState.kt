package pl.karol202.paintplus.image

import pl.karol202.paintplus.history.Action

data class HistoryState(val precedingActions: List<Action.ToRevert> = emptyList(),
                        val followingActions: List<Action.ToCommit> = emptyList())
{
	val canUndo = precedingActions.isNotEmpty()
	val canRedo = followingActions.isNotEmpty()

	val precedingSize = precedingActions.size
	val followingSize = followingActions.size

	fun poppingPreceding(): Pair<HistoryState, Action.ToRevert>?
	{
		val action = precedingActions.lastOrNull() ?: return null
		val newState = copy(precedingActions = precedingActions.dropLast(1))
		return newState to action
	}

	fun poppingFollowing(): Pair<HistoryState, Action.ToCommit>?
	{
		val action = followingActions.lastOrNull() ?: return null
		val newState = copy(followingActions = followingActions.dropLast(1))
		return newState to action
	}

	fun withPrecedingAction(action: Action.ToRevert) = copy(precedingActions = precedingActions + action)

	fun withFollowingAction(action: Action.ToCommit) = copy(followingActions = followingActions + action)

	fun withNoFollowingActions() = copy(followingActions = emptyList())
}
