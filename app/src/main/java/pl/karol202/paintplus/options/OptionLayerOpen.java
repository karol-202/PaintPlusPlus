package pl.karol202.paintplus.options;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Toast;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityResultListener;
import pl.karol202.paintplus.file.ImageLoaderDialog;
import pl.karol202.paintplus.file.explorer.FileExplorer;
import pl.karol202.paintplus.file.explorer.FileExplorerFactory;
import pl.karol202.paintplus.history.action.ActionLayerAdd;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

import static android.app.Activity.RESULT_OK;

public class OptionLayerOpen extends Option implements ActivityResultListener, ImageLoaderDialog.OnImageLoadListener
{
	private static final int REQUEST_OPEN_LAYER = 3;
	
	private ActivityPaint activity;
	private AsyncManager asyncManager;
	
	private Uri uri;
	
	public OptionLayerOpen(ActivityPaint activity, Image image, AsyncManager asyncManager)
	{
		super(activity, image);
		this.activity = activity;
		this.asyncManager = asyncManager;
	}
	
	@Override
	public void execute()
	{
		activity.registerActivityResultListener(REQUEST_OPEN_LAYER, this);
		
		FileExplorer explorer = FileExplorerFactory.createFileExplorer(activity);
		explorer.openFile(REQUEST_OPEN_LAYER);
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent intent)
	{
		activity.unregisterActivityResultListener(REQUEST_OPEN_LAYER);
		if(resultCode != RESULT_OK) return;
		uri = intent.getData();
		
		new ImageLoaderDialog(context, asyncManager, this).loadBitmapAndAskForScalingIfTooBig(uri);
	}
	
	@Override
	public void onImageLoaded(Bitmap bitmap)
	{
		if(bitmap == null)
		{
			Toast.makeText(context, R.string.message_cannot_open_file, Toast.LENGTH_SHORT).show();
			return;
		}
		Layer layer = new Layer(0, 0, bitmap.getWidth(), bitmap.getHeight(), "xyz", Color.TRANSPARENT);
		layer.setBitmap(bitmap);
		if(!image.addLayer(layer, 0))
			Toast.makeText(context, R.string.too_many_layers, Toast.LENGTH_SHORT).show();
		else createLayerAddHistoryAction(layer);
	}
	
	private void createLayerAddHistoryAction(Layer layer)
	{
		ActionLayerAdd action = new ActionLayerAdd(image);
		action.setLayerAfterAdding(layer);
		action.applyAction();
	}
}