package pl.karol202.paintplus.color.manipulators;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import pl.karol202.paintplus.util.GraphicsHelper;

public class ColorsInvert implements ColorsManipulator<InvertParams>
{
	private RenderScript renderScript;
	
	public ColorsInvert()
	{
		renderScript = GraphicsHelper.getRenderScript();
	}
	
	@Override
	public Bitmap run(Bitmap in, InvertParams params)
	{
		Bitmap out = Bitmap.createBitmap(in.getWidth(), in.getHeight(), Bitmap.Config.ARGB_8888);
		
		Allocation allocationIn = Allocation.createFromBitmap(renderScript, in);
		Allocation allocationOut = Allocation.createFromBitmap(renderScript, out);
		
		ScriptC_cm_invert script = new ScriptC_cm_invert(renderScript);
		script.forEach_invert(allocationIn, allocationOut);
		allocationOut.copyTo(out);
		
		return out;
	}
}