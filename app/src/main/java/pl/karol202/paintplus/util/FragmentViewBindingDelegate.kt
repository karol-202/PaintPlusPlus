package pl.karol202.paintplus.util

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(val fragment: Fragment,
                                                   val bind: (View) -> T) : ReadOnlyProperty<Any, T>
{
	private var binding: T? = null

	init
	{
		fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
			override fun onCreate(owner: LifecycleOwner) {
				fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
					viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
						override fun onDestroy(owner: LifecycleOwner) {
							binding = null
						}
					})
				}
			}
		})
	}

	override fun getValue(thisRef: Any, property: KProperty<*>) = binding ?: createBinding().also { this.binding = it }

	private fun createBinding(): T
	{
		if(!fragment.viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))
			throw IllegalStateException("Should not attempt to get bindings when Fragment views are not initialized.")
		return bind(fragment.requireView())
	}
}

fun <T : ViewBinding> Fragment.viewBinding(bind: (View) -> T) = FragmentViewBindingDelegate(this, bind)


