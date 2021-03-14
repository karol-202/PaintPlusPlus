package pl.karol202.paintplus.util

import android.app.Activity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class ActivityArgumentDelegate<T>(private val name: String) : ReadOnlyProperty<Activity, T>
{
	class Nullable<T : Any>(name: String) : ActivityArgumentDelegate<T?>(name)
	{
		override operator fun getValue(thisRef: Activity, property: KProperty<*>): T? =
				thisRef.getArgument()
	}

	class NotNull<T : Any>(name: String,
	                       private val defaultValueProvider: () -> T) : ActivityArgumentDelegate<T>(name)
	{
		override operator fun getValue(thisRef: Activity, property: KProperty<*>): T =
				thisRef.getArgument() ?: defaultValueProvider()
	}

	@Suppress("UNCHECKED_CAST")
	protected fun Activity.getArgument() = intent.extras?.get(name) as T?
}
