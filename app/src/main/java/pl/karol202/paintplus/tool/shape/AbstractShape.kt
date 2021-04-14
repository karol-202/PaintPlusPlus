/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package pl.karol202.paintplus.tool.shape

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.karol202.paintplus.image.ColorsService
import pl.karol202.paintplus.image.EffectsService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.util.MathUtils.dpToPixels
import pl.karol202.paintplus.util.cache
import kotlin.properties.Delegates

private const val MAX_TOUCH_DISTANCE_DP = 25f
private const val TRANSLUCENT_SHAPE_OPACITY = 0.5f

abstract class AbstractShape(private val context: Context,
                             private val viewService: ViewService,
                             private val colorsService: ColorsService,
                             private val effectsService: EffectsService) : Shape
{
	protected val maxTouchDistancePx get() = dpToPixels(context, MAX_TOUCH_DISTANCE_DP) / viewService.zoom

	override var opacity by notifying(1f)
	override var smooth by notifying(true)

	protected open val standardPaint by cache({opacity}, {smooth}, {colorsService.currentColor}) { opacity, smooth, currentColor ->
		Paint().apply {
			alpha = (opacity * 255).toInt()
			isAntiAlias = smooth
			color = currentColor
		}
	}
	protected open val translucentPaint by cache({standardPaint}, {opacity}) { standardPaint, opacity ->
		Paint(standardPaint).apply {
			alpha = (opacity * 255 * TRANSLUCENT_SHAPE_OPACITY).toInt()
		}
	}

	protected fun <V> notifying(initial: V) = Delegates.observable(initial) { _, _, _ -> effectsService.notifyViewUpdate() }
}
