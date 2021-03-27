package pl.karol202.paintplus.util

fun <T> Iterable<T>.inserted(index: Int, newItem: T) =
		take(index) + newItem + drop(index)

fun <T> Iterable<T>.changed(index: Int, newItem: T) =
		mapIndexed { i, item -> if(i == index) newItem else item }

fun <T> Iterable<T>.removed(index: Int) =
		filterIndexed { i, _ -> i != index }
