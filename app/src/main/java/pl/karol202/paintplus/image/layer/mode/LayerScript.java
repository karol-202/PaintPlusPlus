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