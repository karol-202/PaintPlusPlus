package pl.karol202.paintplus.util

import kotlin.properties.ReadOnlyProperty

fun <T, I, O> ReadOnlyProperty<T, I>.map(mapper: (I) -> O) = ReadOnlyProperty<T, O> { thisRef, property ->
	mapper(getValue(thisRef, property))
}
