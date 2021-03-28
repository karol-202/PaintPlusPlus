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
package pl.karol202.paintplus.color.curves

import pl.karol202.paintplus.R
import java.util.ArrayList

enum class ColorChannel(val displayName: Int,
                        val icon: Int,
                        val type: ColorChannelType,
                        val maxValue: Int)
{
	RED(R.string.channel__red, R.drawable.ic_channel_red_24dp, ColorChannelType.RGB, 255),
	GREEN(R.string.channel__green, R.drawable.ic_channel_green_24dp, ColorChannelType.RGB, 255),
	BLUE(R.string.channel__blue, R.drawable.ic_channel_blue_24dp, ColorChannelType.RGB, 255),
	HUE(R.string.channel__hue, R.drawable.ic_channel_hue_24dp, ColorChannelType.HSV, 359),
	SATURATION(R.string.channel__saturation, R.drawable.ic_channel_saturation_24dp, ColorChannelType.HSV, 100),
	VALUE(R.string.channel__value, R.drawable.ic_channel_value_24dp, ColorChannelType.HSV, 100);

	enum class ColorChannelType
	{
		RGB, HSV
	}

	companion object
	{
		@JvmStatic
		fun filterByType(type: ColorChannelType) = values().filter { it.type == type }.toTypedArray()
	}
}
