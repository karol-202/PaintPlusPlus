package pl.karol202.paintplus.color.manipulators;

import pl.karol202.paintplus.color.ChannelInOutSet;
import pl.karol202.paintplus.color.ColorCurve;

import java.util.ArrayList;

public class CurveManipulatorParams implements ColorsManipulator.ColorsManipulatorParams
{
	private ArrayList<ChannelInOutSet> channels;
	private ArrayList<ColorCurve> curves;
	
	public CurveManipulatorParams()
	{
		channels = new ArrayList<>();
		curves = new ArrayList<>();
	}
	
	public void addCurve(ChannelInOutSet channel, ColorCurve curve)
	{
		channels.add(channel);
		curves.add(curve);
	}
	
	public ChannelInOutSet getChannel(int i)
	{
		return channels.get(i);
	}
	
	public ColorCurve getCurve(int i)
	{
		return curves.get(i);
	}
	
	public int getCurvesAmount()
	{
		return curves.size();
	}
}