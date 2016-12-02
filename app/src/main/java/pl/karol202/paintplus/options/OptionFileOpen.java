package pl.karol202.paintplus.options;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.activity.ActivityFileOpen;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.util.GLHelper;

import static android.app.Activity.RESULT_OK;

public class OptionFileOpen extends Option implements ActivityPaint.ActivityResultListener
{
	private static final int REQUEST_OPEN_FILE = 1;
	
	private ActivityPaint activity;
	private GLHelper glHelper;
	
	public OptionFileOpen(ActivityPaint activity, Image image)
	{
		super(activity, image);
		this.activity = activity;
		this.glHelper = activity.getGLHelper();
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
		
		openImageFromFile(image, glHelper, filePath);
	}
	
	public static void openImageFromFile(Image image, GLHelper glHelper, String photoFilePath)
	{
		Bitmap photo = BitmapFactory.decodeFile(photoFilePath);
		if(photo == null) return;
		
		float maxSize = glHelper.getMaxTextureSize();
		if(photo.getWidth() < maxSize || photo.getHeight() < maxSize)
		{
			float widthRatio = photo.getWidth() / maxSize;
			float heightRatio = photo.getHeight() / maxSize;
			float higher = Math.max(widthRatio, heightRatio);
			int newWidth = (int) Math.floor(photo.getWidth() / higher);
			int newHeight = (int) Math.floor(photo.getHeight() / higher);
			Bitmap scaled = Bitmap.createScaledBitmap(photo, newWidth, newHeight, true);
			image.setBitmap(scaled);
		}
		else image.setBitmap(photo);
		image.centerView();
	}
}