package pl.karol202.paintplus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseViewModel(application: Application) : AndroidViewModel(application)
{
	protected fun launch(block: suspend CoroutineScope.() -> Unit)
	{
		viewModelScope.launch { block() }
	}
}
