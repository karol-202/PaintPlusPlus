package pl.karol202.paintplus.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.ViewPropertyAnimator

fun ViewPropertyAnimator.setAnimationEndListener(listener: () -> Unit) = setListener(
		object : AnimatorListenerAdapter()
		{
			override fun onAnimationEnd(animation: Animator?) = listener.invoke()
		})
