package pl.karol202.paintplus.image.layer.mode;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

public class LayerModeDarker extends LayerModeRenderscript<LayerModeDarker.ScriptDarker>
{
	protected class ScriptDarker extends LayerScript<ScriptC_lm_darker>
	{
		ScriptDarker(RenderScript renderScript)
		{
			super(renderScript);
		}
		
		@Override
		protected ScriptC_lm_darker getNewScript(RenderScript renderScript)
		{
			return new ScriptC_lm_darker(renderScript);
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
	
	public LayerModeDarker()
	{
		super();
	}
	
	@Override
	protected ScriptDarker getNewScript(RenderScript renderScript)
	{
		return new ScriptDarker(renderScript);
	}
}