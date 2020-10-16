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

package pl.karol202.paintplus.image.layer.mode;

import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

public class LayerModeDifference extends LayerModeRenderscript<LayerModeDifference.ScriptDifference>
{
	protected class ScriptDifference extends LayerScript<ScriptC_lm_difference>
	{
		ScriptDifference(RenderScript renderScript)
		{
			super(renderScript);
		}

		@Override
		protected ScriptC_lm_difference getNewScript(RenderScript renderScript)
		{
			return new ScriptC_lm_difference(renderScript);
		}

		@Override
		protected void setDstAllocation(Allocation dst)
		{
			script.set_dstAlloc(dst);
		}

		@Override
		protected void setOpacity(float opacity)
		{
			script.set_opacity(opacity);
		}

		@Override
		protected void run(Allocation src, Allocation out)
		{
			script.forEach_subtract(src, out);
		}
	}

	public LayerModeDifference()
	{
		super();
	}

	@Override
	protected ScriptDifference getNewScript(RenderScript renderScript)
	{
		return new ScriptDifference(renderScript);
	}
}
