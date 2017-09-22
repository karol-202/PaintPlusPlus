package pl.karol202.paintplus.color.manipulators.params;

import pl.karol202.paintplus.color.curves.ChannelInOutSet;
import pl.karol202.paintplus.color.curves.ColorChannel.ColorChannelType;
import pl.karol202.paintplus.color.curves.ColorCurve;

import java.util.ArrayList;

public class CurveManipulatorParams extends ColorsManipulatorParams
{
	private ColorChannelType channelType;
	private ArrayList<ChannelInOutSet> channels;
	private ArrayList<ColorCurve> curves;
	
	public CurveManipulatorParams(ManipulatorSelection selection, ColorChannelType channelType)
	{
		super(selection);
		this.channelType = channelType;
		this.channels = new ArrayList<>();
		this.curves = new ArrayList<>();
	}
	
	public ColorChannelType getChannelType()
	{
		return channelType;
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