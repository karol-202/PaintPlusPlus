package pl.karol202.paintplus.util

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : ViewBinding> Activity.viewBinding(bindingInflater: (LayoutInflater) -> T) =
		lazy { bindingInflater(layoutInflater) }

fun <T : Any> Activity.argument(name: String) =
		ActivityArgumentDelegate.Nullable<T>(name)

fun <T : Any> Activity.argumentOr(name: String, defaultValue: T) =
		ActivityArgumentDelegate.NotNull(name) { defaultValue }

fun <T : Any> Activity.argumentOr(name: String, defaultValueProvider: () -> T) =
		ActivityArgumentDelegate.NotNull(name, defaultValueProvider)

fun <T : Any> Activity.argumentOrThrow(name: String) =
		ActivityArgumentDelegate.NotNull<T>(name) { throw IllegalStateException("No argument passed") }
