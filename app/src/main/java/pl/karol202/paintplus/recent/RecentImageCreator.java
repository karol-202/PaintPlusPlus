package pl.karol202.paintplus.recent;

import android.content.Context;
import android.graphics.Bitmap;

public class RecentImageCreator implements OnFileEditListener
{
	private static final int MAX_BOUND = 256;
	
	private Context context;
	
	public RecentImageCreator(Context context)
	{
		this.context = context;
	}
	
	@Override
	public void onFileEdited(String path, Bitmap bitmap)
	{
		RecentLoader loader = new RecentLoader(context);
		loader.load();
		loader.addOrUpdateRecentImage(createRecentImage(path, bitmap));
		loader.save();
	}
	
	private RecentImage createRecentImage(String path, Bitmap originalBitmap)
	{
		String[] parts = path.split("/");
		String name = parts[parts.length - 1];
		Bitmap thumbnail = createThumbnail(originalBitmap);
		
		return new RecentImage(path, null, thumbnail, name, System.currentTimeMillis());
	}
	
	private Bitmap createThumbnail(Bitmap originalBitmap)
	{
		int width = Math.min(originalBitmap.getWidth(), MAX_BOUND);
		int height = Math.min(originalBitmap.getHeight(), MAX_BOUND);
		float ratio = (float) originalBitmap.getWidth() / (float) originalBitmap.getHeight();
		if(ratio < 1) width *= ratio;
		else if(ratio > 1) height /= ratio;
		return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
	}
}