package pl.karol202.paintplus.util

import android.view.View
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T, I, O> ReadOnlyProperty<T, I>.map(mapper: (I) -> O) = ReadOnlyProperty<T, O> { thisRef, property ->
	mapper(getValue(thisRef, property))
}

fun <T, V> ReadOnlyProperty<T, V?>.require() = ReadOnlyProperty<T, V> { thisRef, property ->
	getValue(thisRef, property) ?: throw IllegalStateException("${property.name} is null")
}

fun <T, V> ReadWriteProperty<T, V?>.require() = object : ReadWriteProperty<T, V> {
	override fun getValue(thisRef: T, property: KProperty<*>) =
			this@require.getValue(thisRef, property) ?: throw IllegalStateException("${property.name} is null")

	override fun setValue(thisRef: T, property: KProperty<*>, value: V) =
			this@require.setValue(thisRef, property, value)
}

fun <T : View, V> T.invalidating(default: V) = object : ReadWriteProperty<T, V> {
	private var value = default

	override fun getValue(thisRef: T, property: KProperty<*>) = value

	override fun setValue(thisRef: T, property: KProperty<*>, value: V)
	{
		this.value = value
		thisRef.invalidate()
	}
}

fun <V> notifying(initial: V, notifyTarget: MutableSharedFlow<Unit>) =
		Delegates.observable(initial) { _, _, _ -> notifyTarget.tryEmit(Unit) }

