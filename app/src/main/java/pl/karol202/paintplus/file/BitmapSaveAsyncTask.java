package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class BitmapSaveAsyncTask extends AsyncTask<BitmapSaveParams, Void, BitmapSaveAsyncTask.Result>
{
	public interface OnBitmapSaveListener
	{
		void onBitmapSaved(boolean saved, String filePath, Bitmap bitmap);
	}
	
	class Result
	{
		private Bitmap bitmap;
		private boolean saved;
		
		Result(Bitmap bitmap, boolean saved)
		{
			this.bitmap = bitmap;
			this.saved = saved;
		}
		
		Bitmap getBitmap()
		{
			return bitmap;
		}
		
		boolean isSaved()
		{
			return saved;
		}
	}
	
	private OnBitmapSaveListener listener;
	private String filePath;
	
	@Override
	protected Result doInBackground(BitmapSaveParams... params)
	{
		listener = params[0].getListener();
		filePath = params[0].getFilePath();
		
		Result result = new Result(params[0].getBitmap(),
								   ImageLoader.saveBitmap(params[0].getBitmap(), filePath, params[0].getQuality()));
		return result;
	}
	
	@Override
	protected void onPostExecute(Result result)
	{
		super.onPostExecute(result);
		listener.onBitmapSaved(result.isSaved(), filePath, result.getBitmap());
	}
}