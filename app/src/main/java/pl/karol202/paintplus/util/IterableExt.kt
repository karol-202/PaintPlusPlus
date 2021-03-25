package pl.karol202.paintplus.util

fun <T> Iterable<T>.changed(index: Int, newItem: T) =
		mapIndexed { i, item -> if(i == index) newItem else item }
