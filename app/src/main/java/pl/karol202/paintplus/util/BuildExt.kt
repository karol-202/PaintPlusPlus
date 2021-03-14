package pl.karol202.paintplus.util

import android.os.Build

inline fun <R> doOnApi(api: Int, block: () -> R, fallback: () -> R) =
		if(Build.VERSION.SDK_INT >= api) block()
		else fallback()
