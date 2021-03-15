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
package pl.karol202.paintplus.tool.gradient

import android.graphics.Shader.TileMode
import pl.karol202.paintplus.R
import android.graphics.Shader
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

enum class GradientRepeatability(@StringRes val displayName: Int,
                                 @DrawableRes val icon: Int,
                                 val tileMode: TileMode)
{
	NO_REPEAT(R.string.gradient_repeatability_no_repeat, R.drawable.ic_gradient_repeatability_no_repeat_24dp, TileMode.CLAMP),
	REPEAT(R.string.gradient_repeatability_repeat, R.drawable.ic_gradient_repeatability_repeat_24dp, TileMode.REPEAT),
	MIRROR(R.string.gradient_repeatability_mirror, R.drawable.ic_gradient_repeatability_mirror_24dp, TileMode.MIRROR);
}
