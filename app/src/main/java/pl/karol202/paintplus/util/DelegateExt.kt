package pl.karol202.paintplus.util

import android.view.View
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.karol202.paintplus.tool.StandardTool
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

fun <T, V> ReadWriteProperty<T, V>.onChange(onChange: (old: V, new: V) -> Unit) = object : ReadWriteProperty<T, V> {
	override fun getValue(thisRef: T, property: KProperty<*>) =
			this@onChange.getValue(thisRef, property)

	override fun setValue(thisRef: T, property: KProperty<*>, value: V)
	{
		onChange(this@onChange.getValue(thisRef, property), value)
		this@onChange.setValue(thisRef, property, value)
	}
}

fun <T, V> ReadWriteProperty<T, V>.assert(assertion: (new: V) -> Boolean) = onChange { _, new -> require(assertion(new)) }

fun <T : View, V> T.invalidating(default: V) = object : ReadWriteProperty<T, V> {
	private var value = default

	override fun getValue(thisRef: T, property: KProperty<*>) = value

	override fun setValue(thisRef: T, property: KProperty<*>, value: V)
	{
		this.value = value
		thisRef.invalidate()
	}
}

