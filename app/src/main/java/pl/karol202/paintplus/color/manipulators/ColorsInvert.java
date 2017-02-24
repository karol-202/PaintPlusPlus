package pl.karol202.paintplus.color.manipulators;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import pl.karol202.paintplus.color.manipulators.params.InvertParams;
import pl.karol202.paintplus.color.manipulators.params.ManipulatorSelection;
import pl.karol202.paintplus.util.GraphicsHelper;

public class ColorsInvert implements ColorsManipulator<InvertParams>
{
	private RenderScript renderScript;
	private InvertParams params;
	
	public ColorsInvert()
	{
		renderScript = GraphicsHelper.getRenderScript();
	}
	
	@Override
	public Bitmap run(Bitmap in, InvertParams params)
	{
		this.params = params;
		
		Bitmap out = Bitmap.createBitmap(in.getWidth(), in.getHeight(), Bitmap.Config.ARGB_8888);
		
		Allocation allocationIn = Allocation.createFromBitmap(renderScript, in);
		Allocation allocationOut = Allocation.createFromBitmap(renderScript, out);
		
		ScriptC_cm_invert script = new ScriptC_cm_invert(renderScript);
		attachSelection(script);
		script.forEach_invert(allocationIn, allocationOut);
		allocationOut.copyTo(out);
		
		return out;
	}
	
	private void attachSelection(ScriptC_cm_invert script)
	{
		ManipulatorSelection selection = params.getSelection();
		if(selection == null) return;
		byte[] selectionData = selection.getData();
		Rect selectionBounds = selection.getBounds();
		
		Allocation allocationSelection = Allocation.createSized(renderScript, Element.U8(renderScript), selectionData.length);
		allocationSelection.copyFrom(selectionData);
		
		script.bind_selectionData(allocationSelection);
		script.set_selectionWidth(selectionBounds.width());
		script.set_selectionLeft(selectionBounds.left);
		script.set_selectionTop(selectionBounds.top);
		script.set_selectionRight(selectionBounds.right);
		script.set_selectionBottom(selectionBounds.bottom);
	}
}