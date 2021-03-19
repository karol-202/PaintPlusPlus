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
package pl.karol202.paintplus.image.layer.mode

import android.renderscript.RenderScript
import androidx.annotation.StringRes
import pl.karol202.paintplus.R

enum class LayerModeType(@StringRes val displayName: Int,
                         val category: Int,
                         val supplier: (RenderScript) -> LayerMode)
{
	MODE_STANDARD(R.string.layer_mode_standard, 0, { DefaultLayerMode }),
	MODE_SCREEN(R.string.layer_mode_screen, 1, { ScreenLayerMode }),
	MODE_OVERLAY(R.string.layer_mode_overlay, 1, { OverlayLayerMode }),
	MODE_ADD(R.string.layer_mode_add, 2, { AddLayerMode(it) }),
	MODE_SUBTRACTION(R.string.layer_mode_subtraction, 2, { SubtractionLayerMode(it) }),
	MODE_DIFFERENCE(R.string.layer_mode_difference, 2, { DifferenceLayerMode(it) }),
	MODE_MULTIPLY(R.string.layer_mode_multiply, 2, { MultiplyLayerMode(it) }),
	MODE_LIGHTER(R.string.layer_mode_lighter, 3, { LighterLayerMode(it) }),
	MODE_DARKER(R.string.layer_mode_darker, 3, { DarkerLayerMode(it) });

	companion object
	{
		@JvmStatic
		fun getIndexOfMode(mode: LayerMode) = values().indexOfFirst { it == mode.type }

		@JvmStatic
		fun getTypeOfMode(mode: LayerMode) = values().firstOrNull { it == mode.type }
	}
}
