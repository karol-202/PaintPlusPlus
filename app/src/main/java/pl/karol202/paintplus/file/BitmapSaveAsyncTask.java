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