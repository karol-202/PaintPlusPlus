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
import android.renderscript.Allocation
import pl.karol202.paintplus.R

class DarkerLayerMode(renderScript: RenderScript) : RenderscriptLayerMode(name = R.string.layer_mode_darker,
                                                                          category = 3,
                                                                          renderScript = renderScript,
                                                                          layerScript = ScriptDarker(renderScript))
{
	private class ScriptDarker(renderScript: RenderScript) : LayerScript
	{
		private val script = ScriptC_lm_darker(renderScript)

		override fun setDstAllocation(dst: Allocation) = script.set_dstAlloc(dst)

		override fun setOpacity(opacity: Float) = script.set_opacity(opacity)

		override fun run(src: Allocation, out: Allocation) = script.forEach_evaluate(src, out)
	}
}