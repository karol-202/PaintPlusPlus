package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class BitmapSaveAsyncTask extends AsyncTask<BitmapSaveParams, Void, BitmapSaveResult>
{
	public interface OnBitmapSaveListener
	{
		void onBitmapSaved(boolean saved, String filePath, Bitmap bitmap);
	}
	
	private OnBitmapSaveListener listener;
	private String filePath;
	
	@Override
	protected BitmapSaveResult doInBackground(BitmapSaveParams... params)
	{
		listener = params[0].getListener();
		filePath = params[0].getFilePath();
		
		return new BitmapSaveResult(params[0].getBitmap(),
						  ImageLoader.saveBitmap(params[0].getBitmap(), filePath, params[0].getQuality()));
	}
	
	@Override
	protected void onPostExecute(BitmapSaveResult result)
	{
		super.onPostExecute(result);
		listener.onBitmapSaved(result.isSaved(), filePath, result.getBitmap());
	}
}