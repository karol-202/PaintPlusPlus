package pl.karol202.paintplus.image.layer.mode;

import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptC;

public abstract class LayerScript<S extends ScriptC>
{
	S script;
	
	protected LayerScript(RenderScript renderScript)
	{
		script = getNewScript(renderScript);
	}
	
	protected abstract S getNewScript(RenderScript renderScript);
	
	protected abstract void setDstAllocation(Allocation dst);
	
	protected abstract void setOpacity(float opacity);
	
	protected abstract void run(Allocation src, Allocation out);
}