package pl.karol202.paintplus.file;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import pl.karol202.paintplus.AsyncBlocker;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;

public class ImageLoaderDialog
{
	public interface OnImageLoadListener
	{
		void onImageLoaded(Bitmap bitmap);
	}
	
	private class ImageLoadAsyncBlocker implements AsyncBlocker
	{
		@Override
		public void cancel()
		{
			onLoadingCancel();
		}
		
		@Override
		public int getMessage()
		{
			return R.string.dialog_load_wait_message;
		}
	}
	
	private Context context;
	private OnImageLoadListener listener;
	
	private String path;
	private Point bitmapSize;
	
	private AlertDialog dialog;
	
	private AsyncManager asyncManager;
	private AsyncBlocker asyncBlocker;
	private AsyncTask<BitmapLoadParams, Void, BitmapLoadResult> asyncTask;
	
	public ImageLoaderDialog(Context context, AsyncManager asyncManager, OnImageLoadListener listener)
	{
		this.context = context;
		this.asyncManager = asyncManager;
		this.listener = listener;
	}
	
	public void loadBitmapAndAskForScalingIfTooBig(String path)
	{
		this.path = path;
		this.bitmapSize = ImageLoader.getBitmapSize(path);
		
		boolean tooBig = ImageLoader.isBitmapTooBig(bitmapSize);
		if(!tooBig) listener.onImageLoaded(ImageLoader.openBitmapAndScaleIfNecessary(path));
		else
		{
			bitmapSize = ImageLoader.scaleBitmapSizeIfNecessary(bitmapSize);
			showDialog();
		}
	}
	
	private void showDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_image_too_big);
		builder.setMessage(context.getString(R.string.dialog_image_too_big_question, bitmapSize.x, bitmapSize.y));
		builder.setPositiveButton(R.string.scale_down, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i)
			{
				onAccept();
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		dialog = builder.create();
		dialog.show();
	}
	
	private void onAccept()
	{
		asyncBlocker = new ImageLoadAsyncBlocker();
		asyncManager.block(asyncBlocker);
		BitmapLoadParams params = new BitmapLoadParams(new BitmapLoadAsyncTask.OnBitmapLoadListener() {
			@Override
			public void onBitmapLoad(Bitmap bitmap)
			{
				onBitmapLoaded(bitmap);
			}
		}, path, bitmapSize);
		asyncTask = new BitmapLoadAsyncTask();
		asyncTask.execute(params);
	}
	
	private void onBitmapLoaded(Bitmap bitmap)
	{
		asyncManager.unblock(asyncBlocker);
		listener.onImageLoaded(bitmap);
	}
	
	private void onLoadingCancel()
	{
		asyncManager.unblock(asyncBlocker);
		asyncTask.cancel(true);
	}
}