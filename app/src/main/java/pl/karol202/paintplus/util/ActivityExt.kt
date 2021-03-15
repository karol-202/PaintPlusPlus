package pl.karol202.paintplus.util

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun Activity.enterFullscreen()
{
	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) initSystemUIVisibility30()
	else initSystemUIVisibilityPre30()
}

@TargetApi(Build.VERSION_CODES.R)
private fun Activity.initSystemUIVisibility30()
{
	window.setDecorFitsSystemWindows(false)
	window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
	window.insetsController?.hide(WindowInsets.Type.systemBars())
}

// Can be replaced with usage of WindowInsetsControllerCompat as soon as the bug is resolved:
// https://issuetracker.google.com/issues/173203649
@Suppress("DEPRECATION")
private fun Activity.initSystemUIVisibilityPre30()
{
	window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
			View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
			View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
			View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
			View.SYSTEM_UI_FLAG_FULLSCREEN or
			View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
}

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
