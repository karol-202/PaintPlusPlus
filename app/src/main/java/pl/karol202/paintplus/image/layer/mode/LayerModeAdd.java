package pl.karol202.paintplus.image.layer.mode;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

public class LayerModeAdd extends LayerModeRenderscript<LayerModeAdd.ScriptAdd>
{
	protected class ScriptAdd extends LayerScript<ScriptC_lm_add>
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