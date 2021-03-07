package pl.karol202.paintplus.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.collectIn(scope: CoroutineScope, crossinline action: suspend (value: T) -> Unit)
{
	scope.launch { collect(action) }
}
