package pl.karol202.paintplus.image.layer.mode;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

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