package pl.karol202.paintplus.image.layer.mode;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

public class LayerModeSubtraction extends LayerModeRenderscript<LayerModeSubtraction.ScriptSubtraction>
{
	protected class ScriptSubtraction extends LayerScript<ScriptC_lm_subtraction>
	{
		ScriptSubtraction(RenderScript renderScript)
		{
			super(renderScript);
		}
		
		@Override
		protected ScriptC_lm_subtraction getNewScript(RenderScript renderScript)
		{
			return new ScriptC_lm_subtraction(renderScript);
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
	
	public LayerModeSubtraction()
	{
		super();
	}
	
	@Override
	protected ScriptSubtraction getNewScript(RenderScript renderScript)
	{
		return new ScriptSubtraction(renderScript);
	}
}