/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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