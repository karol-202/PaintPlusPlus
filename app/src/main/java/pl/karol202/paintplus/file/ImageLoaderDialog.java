package pl.karol202.paintplus.file;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import pl.karol202.paintplus.R;

public class ImageLoaderDialog implements DialogInterface.OnClickListener
{
	public interface OnImageLoadListener
	{
		void onImageLoaded(Bitmap bitmap);
	}
	
	private Context context;
	private OnImageLoadListener listener;
	
	private String path;
	private Point bitmapSize;
	
	private AlertDialog dialog;
	
	public ImageLoaderDialog(Context context, OnImageLoadListener listener)
	{
		this.context = context;
		this.listener = listener;
	}
	
	public void loadBitmapAndAskForScalingIfTooBig(String path)
	{
		this.path = path;
		this.bitmapSize = ImageLoader.getBitmapSize(path);
		
		boolean tooBig = ImageLoader.isBitmapTooBig(bitmapSize);
		if(!tooBig)
			listener.onImageLoaded(ImageLoader.openBitmapAndScaleIfNecessary(path));
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
		builder.setPositiveButton(R.string.scale_down, this);
		builder.setNegativeButton(R.string.cancel, null);
		dialog = builder.create();
		dialog.show();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		Bitmap bitmap = ImageLoader.openBitmapAndScale(path, bitmapSize);
		listener.onImageLoaded(bitmap);
	}
}