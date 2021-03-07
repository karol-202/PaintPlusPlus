package pl.karol202.paintplus.settings

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

fun SharedPreferences.observeString(key: String, default: String?) = observe(key, default, this::getString)
fun SharedPreferences.observeStringSet(key: String, default: Set<String>?) = observe(key, default, this::getStringSet)
fun SharedPreferences.observeInt(key: String, default: Int) = observe(key, default, this::getInt)
fun SharedPreferences.observeLong(key: String, default: Long) = observe(key, default, this::getLong)
fun SharedPreferences.observeFloat(key: String, default: Float) = observe(key, default, this::getFloat)
fun SharedPreferences.observeBoolean(key: String, default: Boolean) = observe(key, default, this::getBoolean)

private fun <T> SharedPreferences.observe(key: String, default: T, getter: (String, T) -> T) = callbackFlow<T> {
	channel.offer(getter(key, default))
	val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, updatedKey ->
		if(updatedKey == key) channel.offer(getter(key, default))
	}
	registerOnSharedPreferenceChangeListener(listener)
	awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}.conflate()
