package pl.karol202.paintplus.history.action

interface Action
{
	interface ToCommit : Action
	{
		fun commit(): ToRevert
	}

	interface ToRevert : Action
	{
		fun revert(): ToCommit
	}

	val name: Int
}
