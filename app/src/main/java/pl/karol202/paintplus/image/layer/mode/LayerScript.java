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

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptC;

public abstract class LayerScript<S extends ScriptC>
{
	S script;
	
	LayerScript(RenderScript renderScript)
	{
		script = getNewScript(renderScript);
	}
	
	protected abstract S getNewScript(RenderScript renderScript);
	
	protected abstract void setDstAllocation(Allocation dst);
	
	protected abstract void setOpacity(float opacity);
	
	protected abstract void run(Allocation src, Allocation out);
}