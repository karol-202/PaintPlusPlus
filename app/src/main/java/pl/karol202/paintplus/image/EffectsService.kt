package pl.karol202.paintplus.image

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.karol202.paintplus.viewmodel.PaintViewModel

class EffectsService
{
	private val _dialogFlow = MutableStateFlow<PaintViewModel.DialogDefinition?>(null)
	private val _messageEventFlow = MutableSharedFlow<PaintViewModel.MessageEvent>(extraBufferCapacity = 16)
	private val _actionRequestEventFlow = MutableSharedFlow<PaintViewModel.ActionRequest<*>>(extraBufferCapacity = 16)
	private val _longTaskRunFlow = MutableSharedFlow<() -> Unit>()
	private val _viewUpdateEventFlow = MutableSharedFlow<Unit>()

	val dialogFlow: Flow<PaintViewModel.DialogDefinition?> = _dialogFlow
	val messageEventFlow: Flow<PaintViewModel.MessageEvent> = _messageEventFlow
	val actionRequestEventFlow: Flow<PaintViewModel.ActionRequest<*>> = _actionRequestEventFlow
	val longTaskRunFlow: Flow<() -> Unit> = _longTaskRunFlow
	val viewUpdateEventFlow: Flow<Unit> = _viewUpdateEventFlow

	fun showDialog(dialogDefinition: PaintViewModel.DialogDefinition)
	{
		_dialogFlow.value = dialogDefinition
	}

	fun hideDialog()
	{
		_dialogFlow.value = null
	}

	fun showMessage(@StringRes text: Int)
	{
		_messageEventFlow.tryEmit(PaintViewModel.MessageEvent(text))
	}

	fun <R> makeActionRequest(request: PaintViewModel.ActionRequest<R>)
	{
		_actionRequestEventFlow.tryEmit(request)
	}

	fun postLongTask(task: () -> Unit)
	{
		_longTaskRunFlow.tryEmit(task)
	}

	fun notifyViewUpdate()
	{
		_viewUpdateEventFlow.tryEmit(Unit)
	}
}
