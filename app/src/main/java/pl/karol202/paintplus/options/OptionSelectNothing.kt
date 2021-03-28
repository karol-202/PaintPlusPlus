package pl.karol202.paintplus.options

import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.Selection

class OptionSelectNothing(private val imageService: ImageService,
                          private val historyService: HistoryService,
                          private val optionSelect: OptionSelect) : Option
{
	private val actionPreset = Action.namePreset(R.string.history_action_selection_change).withPreview {
		optionSelect.createPreviewBitmap()
	}

	fun execute()
	{
		historyService.commitAction(this::commit)
	}

	private fun commit(): Action.ToRevert = actionPreset.commit {
		val oldSelection = imageService.selection
		imageService.setSelection(Selection.empty)
		toRevert { revert(oldSelection) }
	}

	private fun revert(selection: Selection): Action.ToCommit = actionPreset.revert {
		imageService.setSelection(selection)
		toCommit { commit() }
	}
}
