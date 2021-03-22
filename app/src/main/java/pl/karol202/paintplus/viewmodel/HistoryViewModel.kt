package pl.karol202.paintplus.viewmodel

import android.app.Application
import pl.karol202.paintplus.image.HistoryService

class HistoryViewModel(application: Application,
                       private val historyService: HistoryService) : BaseViewModel(application)
{
	val historyStateFlow = historyService.stateFlow

	val canUndo get() = historyService.state.canUndo
	val canRedo get() = historyService.state.canRedo

	fun undo() = historyService.undo()

	fun redo() = historyService.redo()
}
