package pl.karol202.paintplus.options;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.file.ActivityFileOpen;
import pl.karol202.paintplus.file.ImageLoader;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Layer;

import static android.app.Activity.RESULT_OK;

public class OptionLayerOpen extends Option implements ActivityPaint.ActivityResultListener
{
	private static final int REQUEST_OPEN_LAYER = 3;
	
	private ActivityPaint activity;
	
	public OptionLayerOpen(ActivityPaint activity, Image image)
	{
		super(activity, image);
		this.activity = activity;
		activity.registerActivityResultListener(REQUEST_OPEN_LAYER, this);
	}
	
	@Override
	public void execute()
	{
		Intent intent = new Intent(context, ActivityFileOpen.class);
		activity.startActivityForResult(intent, REQUEST_OPEN_LAYER);
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent data)
	{
		activity.unregisterActivityResultListener(REQUEST_OPEN_LAYER);
		if(resultCode != RESULT_OK) return;
		String filePath = data.getStringExtra("filePath");
		String fileName = data.getStringExtra("fileName");
		
		Bitmap bitmap = ImageLoader.openBitmap(filePath);
		if(bitmap == null) Toast.makeText(context, R.string.message_cannot_open_file, Toast.LENGTH_SHORT).show();
		else
		{
			Layer layer = new Layer(0, 0, bitmap.getWidth(), bitmap.getHeight(), fileName, Color.TRANSPARENT);
			layer.setBitmap(bitmap);
			image.addLayer(layer, 0);
		}
	}
}