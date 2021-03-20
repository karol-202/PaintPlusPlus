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

package pl.karol202.paintplus.tool.fill;

import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.tool.fill.ToolFillAsyncTask.OnFillCompleteListener;

class FillParams
{
	private OnFillCompleteListener listener;
	private LegacyImage image;
	private float threshold;
	private float translucency;
	private int x;
	private int y;

	FillParams(OnFillCompleteListener listener, LegacyImage image, float threshold, float translucency, int x, int y)
	{
		this.listener = listener;
		this.image = image;
		this.threshold = threshold;
		this.translucency = translucency;
		this.x = x;
		this.y = y;
	}

	OnFillCompleteListener getListener()
	{
		return listener;
	}

	LegacyImage getImage()
	{
		return image;
	}

	float getThreshold()
	{
		return threshold;
	}

	float getTranslucency()
	{
		return translucency;
	}

	int getX()
	{
		return x;
	}

	int getY()
	{
		return y;
	}
}
