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

package pl.karol202.paintplus.options;

import android.graphics.Bitmap;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.recent.OnFileEditListener;

public class OptionFileSave extends OptionSave
{
	private static final int REQUEST_SAVE_FILE = 2;
	
	public OptionFileSave(ActivityPaint activity, Image image, AsyncManager asyncManager, OnFileEditListener listener)
	{
		super(activity, image, asyncManager, listener);
	}
	
	@Override
	int getRequestId()
	{
		return REQUEST_SAVE_FILE;
	}
	
	@Override
	Bitmap getBitmapToSave()
	{
		return image.getFullImage();
	}
	
	@Override
	void saveBitmapAsynchronously()
	{
		image.setLastUri(uri);
		image.setLastFormat(format);
		super.saveBitmapAsynchronously();
	}
}