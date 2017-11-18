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
import android.os.AsyncTask;

public class BitmapSaveAsyncTask extends AsyncTask<BitmapSaveParams, Void, BitmapSaveResult>
{
	public interface OnBitmapSaveListener
	{
		void onBitmapSaved(boolean saved, Bitmap bitmap);
	}
	
	private OnBitmapSaveListener listener;
	
	@Override
	protected BitmapSaveResult doInBackground(BitmapSaveParams... params)
	{
		BitmapSaveParams param = params[0];
		listener = params[0].getListener();
		
		return new BitmapSaveResult(param.getBitmap(),
						            ImageLoader.saveBitmap(param.getBitmap(), param.getFileDescriptor(), param.getName(), param.getQuality()));
	}
	
	@Override
	protected void onPostExecute(BitmapSaveResult result)
	{
		super.onPostExecute(result);
		listener.onBitmapSaved(result.isSaved(), result.getBitmap());
	}
}