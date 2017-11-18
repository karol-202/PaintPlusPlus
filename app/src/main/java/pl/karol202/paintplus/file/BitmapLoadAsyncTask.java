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

package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;

import java.io.FileDescriptor;

class BitmapLoadAsyncTask extends AsyncTask<BitmapLoadParams, Void, BitmapLoadResult>
{
	interface OnBitmapLoadListener
	{
		void onBitmapLoad(Bitmap bitmap);
	}
	
	private OnBitmapLoadListener listener;
	
	@Override
	protected BitmapLoadResult doInBackground(BitmapLoadParams... params)
	{
		listener = params[0].getListener();
		FileDescriptor fileDescriptor = params[0].getFileDescriptor();
		Point bitmapSize = params[0].getBitmapSize();
		
		Bitmap bitmap = ImageLoader.openBitmapAndScale(fileDescriptor, bitmapSize);
		return new BitmapLoadResult(bitmap);
	}
	
	@Override
	protected void onPostExecute(BitmapLoadResult result)
	{
		super.onPostExecute(result);
		listener.onBitmapLoad(result.getBitmap());
	}
}