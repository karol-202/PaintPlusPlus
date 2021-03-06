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

import android.renderscript.Allocation;
import android.renderscript.RenderScript;

public class LayerModeAdd extends LayerModeRenderscript<LayerModeAdd.ScriptAdd>
{
	protected static class ScriptAdd extends LayerScript<ScriptC_lm_add>
	{
		ScriptAdd(RenderScript renderScript)
		{
			super(renderScript);
		}

		@Override
		protected ScriptC_lm_add getNewScript(RenderScript renderScript)
		{
			return new ScriptC_lm_add(renderScript);
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
			script.forEach_sum(src, out);
		}
	}

	public LayerModeAdd()
	{
		super();
	}

	@Override
	protected ScriptAdd getNewScript(RenderScript renderScript)
	{
		return new ScriptAdd(renderScript);
	}
}
