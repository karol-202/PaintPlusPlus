package pl.karol202.paintplus.history;

import android.content.Context;
import android.content.Intent;
import pl.karol202.paintplus.image.Image;

public class ActivityHistoryHelper
{
	private static Image image;//Is there any way to remove this ugly static veriable?
	
	private Context context;
	
	public ActivityHistoryHelper(Image image, Context context)
	{
		ActivityHistoryHelper.image = image;
		this.context = context;
	}
	
	public void startActivity()
	{
		Intent intent = new Intent(context, ActivityHistory.class);
		context.startActivity(intent);
	}
	
	static Image getImage()
	{
		if(image == null) throw new NullPointerException("Image object has been already used.");
		Image image = ActivityHistoryHelper.image;
		ActivityHistoryHelper.image = null;
		return image;
	}
}