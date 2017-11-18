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

package pl.karol202.paintplus.color.picker.numerical;

import android.view.View;

class ColorPickerNumericalInterface
{
	private ColorNumericalFragment fragment;
	
	ColorPickerNumericalInterface(ColorNumericalFragment fragment)
	{
		this.fragment = fragment;
	}
	
	boolean isUsingAlpha()
	{
		return fragment.isUsingAlpha();
	}
	
	View getChannelViewA()
	{
		return fragment.getChannelViewA();
	}
	
	View getChannelViewB()
	{
		return fragment.getChannelViewB();
	}
	
	View getChannelViewC()
	{
		return fragment.getChannelViewC();
	}
	
	View getChannelViewD()
	{
		return fragment.getChannelViewD();
	}
	
	View getChannelViewE()
	{
		return fragment.getChannelViewE();
	}
	
	int getColor()
	{
		return fragment.getCurrentColor();
	}
	
	void setColor(int color)
	{
		fragment.updateColor(color, true);
	}
}