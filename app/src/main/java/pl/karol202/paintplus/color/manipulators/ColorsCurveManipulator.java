package pl.karol202.paintplus.color.manipulators;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import pl.karol202.paintplus.color.ChannelInOutSet;
import pl.karol202.paintplus.color.ColorChannel;
import pl.karol202.paintplus.color.ColorChannel.ColorChannelType;
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
		
		if(params.getChannelType() == ColorChannelType.RGB) runRGB(params, allocationIn, allocationOut);
		else if(params.getChannelType() == ColorChannelType.HSV) runHSV(params, allocationIn, allocationOut);
		allocationOut.copyTo(out);
		
		return out;
	}
	
	private void runRGB(CurveManipulatorParams params, Allocation in, Allocation out)
	{
		ScriptC_cm_curves_rgb script = new ScriptC_cm_curves_rgb(renderScript);
		attachCurvesRGB(script, params);
		script.forEach_transform(in, out);
	}
	
	private void attachCurvesRGB(ScriptC_cm_curves_rgb script, CurveManipulatorParams params)
	{
		for(int i = 0; i < params.getCurvesAmount(); i++)
		{
			ChannelInOutSet channels = params.getChannel(i);
			ColorCurve curve = params.getCurve(i);
			byte[] curveMap = curve.createByteColorsMap();
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
	
	private void runHSV(CurveManipulatorParams params, Allocation in, Allocation out)
	{
		ScriptC_cm_curves_hsv script = new ScriptC_cm_curves_hsv(renderScript);
		attachCurvesHSV(script, params);
		script.forEach_transform(in, out);
	}
	
	private void attachCurvesHSV(ScriptC_cm_curves_hsv script, CurveManipulatorParams params)
	{
		for(int i = 0; i < params.getCurvesAmount(); i++)
		{
			ChannelInOutSet channels = params.getChannel(i);
			ColorCurve curve = params.getCurve(i);
			short[] curveMap = curve.createShortColorsMap();
			Allocation allocation = Allocation.createSized(renderScript, Element.U16(renderScript), curveMap.length);
			allocation.copyFrom(curveMap);
			
			if(channels.getIn() == ColorChannel.HUE && channels.getOut() == ColorChannel.HUE)
				script.bind_curve_hth(allocation);
			else if(channels.getIn() == ColorChannel.HUE && channels.getOut() == ColorChannel.SATURATION)
				script.bind_curve_hts(allocation);
			else if(channels.getIn() == ColorChannel.HUE && channels.getOut() == ColorChannel.VALUE)
				script.bind_curve_htv(allocation);
			else if(channels.getIn() == ColorChannel.SATURATION && channels.getOut() == ColorChannel.HUE)
				script.bind_curve_sth(allocation);
			else if(channels.getIn() == ColorChannel.SATURATION && channels.getOut() == ColorChannel.SATURATION)
				script.bind_curve_sts(allocation);
			else if(channels.getIn() == ColorChannel.SATURATION && channels.getOut() == ColorChannel.VALUE)
				script.bind_curve_stv(allocation);
			else if(channels.getIn() == ColorChannel.VALUE && channels.getOut() == ColorChannel.HUE)
				script.bind_curve_vth(allocation);
			else if(channels.getIn() == ColorChannel.VALUE && channels.getOut() == ColorChannel.SATURATION)
				script.bind_curve_vts(allocation);
			else if(channels.getIn() == ColorChannel.VALUE && channels.getOut() == ColorChannel.VALUE)
				script.bind_curve_vtv(allocation);
		}
	}
}