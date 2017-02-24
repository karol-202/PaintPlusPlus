package pl.karol202.paintplus.options;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityResultListener;
import pl.karol202.paintplus.file.ActivityFileOpen;
import pl.karol202.paintplus.file.ImageLoader;
import pl.karol202.paintplus.image.Image;

import static android.app.Activity.RESULT_OK;

public class OptionFileOpen extends Option implements ActivityResultListener
{
	private static final int REQUEST_OPEN_FILE = 1;
	
	private ActivityPaint activity;
	
	public OptionFileOpen(ActivityPaint activity, Image image)
	{
		super(activity, image);
		this.activity = activity;
		activity.registerActivityResultListener(REQUEST_OPEN_FILE, this);
	}
	
	@Override
	public void execute()
	{
		Intent intent = new Intent(context, ActivityFileOpen.class);
		activity.startActivityForResult(intent, REQUEST_OPEN_FILE);
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent data)
	{
		activity.unregisterActivityResultListener(REQUEST_OPEN_FILE);
		if(resultCode != RESULT_OK) return;
		String filePath = data.getStringExtra("filePath");
		
		Bitmap bitmap = ImageLoader.openBitmap(filePath);
		if(bitmap == null) Toast.makeText(context, R.string.message_cannot_open_file, Toast.LENGTH_SHORT).show();
		else
		{
			image.openImage(bitmap);
			image.centerView();
		}
	}
}