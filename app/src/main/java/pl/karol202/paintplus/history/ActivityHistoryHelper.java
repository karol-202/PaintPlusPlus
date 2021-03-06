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

package pl.karol202.paintplus.history;

import android.content.Context;
import android.content.Intent;
import pl.karol202.paintplus.image.Image;

public class ActivityHistoryHelper
{
	private static Image image; // Is there any way to remove this ugly static variable?

	private Context context;

	public ActivityHistoryHelper(Image image, Context context)
	{
		ActivityHistoryHelper.image = image;
		this.context = context;
	}

	public void startActivity()
	{
		Intent intent = new Intent(context, ActivityHistory.class);
		context.startActivity(intent);
	}

	static Image getImage()
	{
		if(ActivityHistoryHelper.image == null) throw new NullPointerException("Image object has been already used.");
		Image image = ActivityHistoryHelper.image;
		ActivityHistoryHelper.image = null;
		return image;
	}
}
