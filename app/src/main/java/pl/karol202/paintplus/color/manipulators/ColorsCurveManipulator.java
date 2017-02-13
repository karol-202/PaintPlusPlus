package pl.karol202.paintplus.color.manipulators;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import pl.karol202.paintplus.color.ChannelInOutSet;
import pl.karol202.paintplus.color.ColorChannel;
import pl.karol202.paintplus.color.ColorCurve;
import pl.karol202.paintplus.util.GraphicsHelper;

public class ColorsCurveManipulator implements ColorsManipulator<CurveManipulatorParams>
{
	private RenderScript renderScript;
	
	public ColorsCurveManipulator()
	{
		renderScript = GraphicsHelper.getRenderScript();
	}
	
	@Override
	public Bitmap run(Bitmap in, CurveManipulatorParams params)
	{
		Bitmap out = Bitmap.createBitmap(in.getWidth(), in.getHeight(), Bitmap.Config.ARGB_8888);
		
		Allocation allocationIn = Allocation.createFromBitmap(renderScript, in);
		Allocation allocationOut = Allocation.createFromBitmap(renderScript, out);
		
		ScriptC_cm_curves script = new ScriptC_cm_curves(renderScript);
		attachCurves(script, params);
		
		script.forEach_transform(allocationIn, allocationOut);
		allocationOut.copyTo(out);
		
		return out;
	}
	
	private void attachCurves(ScriptC_cm_curves script, CurveManipulatorParams params)
	{
		for(int i = 0; i < params.getCurvesAmount(); i++)
		{
			ChannelInOutSet channels = params.getChannel(i);
			ColorCurve curve = params.getCurve(i);
			byte[] curveMap = curve.createColorsMap();
			Allocation allocation = Allocation.createSized(renderScript, Element.U8(renderScript), curveMap.length);
			allocation.copyFrom(curveMap);
			
			if(channels.getIn() == ColorChannel.RED && channels.getOut() == ColorChannel.RED)
				script.bind_curve_rtr(allocation);
			else if(channels.getIn() == ColorChannel.RED && channels.getOut() == ColorChannel.GREEN)
				script.bind_curve_rtg(allocation);
			else if(channels.getIn() == ColorChannel.RED && channels.getOut() == ColorChannel.BLUE)
				script.bind_curve_rtb(allocation);
			else if(channels.getIn() == ColorChannel.GREEN && channels.getOut() == ColorChannel.RED)
				script.bind_curve_gtr(allocation);
			else if(channels.getIn() == ColorChannel.GREEN && channels.getOut() == ColorChannel.GREEN)
				script.bind_curve_gtg(allocation);
			else if(channels.getIn() == ColorChannel.GREEN && channels.getOut() == ColorChannel.BLUE)
				script.bind_curve_gtb(allocation);
			else if(channels.getIn() == ColorChannel.BLUE && channels.getOut() == ColorChannel.RED)
				script.bind_curve_btr(allocation);
			else if(channels.getIn() == ColorChannel.BLUE && channels.getOut() == ColorChannel.GREEN)
				script.bind_curve_btg(allocation);
			else if(channels.getIn() == ColorChannel.BLUE && channels.getOut() == ColorChannel.BLUE)
				script.bind_curve_btb(allocation);
		}
	}
}