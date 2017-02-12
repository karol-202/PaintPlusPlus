package pl.karol202.paintplus.image.layer.mode;

import android.renderscript.Allocation;
import android.renderscript.RenderScript;

public class LayerModeMultiply extends LayerModeRenderscript<LayerModeMultiply.ScriptMultiply>
{
	protected class ScriptMultiply extends LayerScript<ScriptC_lm_multiply>
	{
		protected ScriptMultiply(RenderScript renderScript)
		{
			super(renderScript);
		}
		
		@Override
		protected ScriptC_lm_multiply getNewScript(RenderScript renderScript)
		{
			return new ScriptC_lm_multiply(renderScript);
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
			script.forEach_multiply(src, out);
		}
	}
	
	public LayerModeMultiply()
	{
		super();
	}
	
	@Override
	protected LayerScript getNewScript(RenderScript renderScript)
	{
		return new ScriptMultiply(renderScript);
	}
}