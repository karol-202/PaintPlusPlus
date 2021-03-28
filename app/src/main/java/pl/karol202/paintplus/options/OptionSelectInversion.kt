package pl.karol202.paintplus.options

import pl.karol202.paintplus.R
import pl.karol202.paintplus.history.Action
import pl.karol202.paintplus.image.HistoryService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.util.toRect

class OptionSelectInversion(private val imageService: ImageService,
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
		invertSelection()
		toRevert { revert() }
	}

	private fun revert(): Action.ToCommit = actionPreset.revert {
		invertSelection()
		toCommit { commit() }
	}

	private fun invertSelection() = imageService.editSelection { inverted(rect = imageService.image.size.toRect()) }
}
