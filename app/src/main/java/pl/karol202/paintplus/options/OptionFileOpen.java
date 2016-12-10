package pl.karol202.paintplus.options;

import android.content.Intent;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.file.ActivityFileOpen;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.file.ImageLoader;

import static android.app.Activity.RESULT_OK;

public class OptionFileOpen extends Option implements ActivityPaint.ActivityResultListener
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
		
		ImageLoader.openImageFromFile(image, filePath);
	}
}