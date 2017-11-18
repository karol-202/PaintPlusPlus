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

class ColorChannel
{
	private int name;
	private int maxValue;
	private int seekBarColorId;
	
	private boolean active;
	private int value;
	
	ColorChannel(int name, int maxValue, int seekBarColorId)
	{
		this(name, maxValue, seekBarColorId, true);
	}
	
	ColorChannel(int name, int maxValue, int seekBarColorId, boolean active)
	{
		this.name = name;
		this.maxValue = maxValue;
		this.seekBarColorId = seekBarColorId;
		
		this.active = active;
	}
	
	int getName()
	{
		return name;
	}
	
	int getMaxValue()
	{
		return maxValue;
	}
	
	int getSeekBarColorId()
	{
		return seekBarColorId;
	}
	
	boolean isActive()
	{
		return active;
	}
	
	int getValue()
	{
		return value;
	}
	
	void setValue(int value)
	{
		this.value = value;
	}
}