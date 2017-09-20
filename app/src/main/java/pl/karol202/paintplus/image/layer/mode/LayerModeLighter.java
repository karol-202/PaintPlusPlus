package pl.karol202.paintplus.image.layer.mode;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

public class LayerModeLighter extends LayerModeRenderscript<LayerModeLighter.ScriptLighter>
{
	protected class ScriptLighter extends LayerScript<ScriptC_lm_lighter>
	{
		ScriptLighter(RenderScript renderScript)
		{
			super(renderScript);
		}
		
		@Override
		protected ScriptC_lm_lighter getNewScript(RenderScript renderScript)
		{
			return new ScriptC_lm_lighter(renderScript);
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
			script.forEach_evaluate(src, out);
		}
	}
	
	public LayerModeLighter()
	{
		super();
	}
	
	@Override
	protected ScriptLighter getNewScript(RenderScript renderScript)
	{
		return new ScriptLighter(renderScript);
	}
}