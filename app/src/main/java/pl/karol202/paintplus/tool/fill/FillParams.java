package pl.karol202.paintplus.tool.fill;

import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.fill.ToolFillAsyncTask.OnFillCompleteListener;

class FillParams
{
	private OnFillCompleteListener listener;
	private Image image;
	private float threshold;
	private float translucency;
	private int x;
	private int y;
	
	FillParams(OnFillCompleteListener listener, Image image, float threshold, float translucency, int x, int y)
	{
		this.listener = listener;
		this.image = image;
		this.threshold = threshold;
		this.translucency = translucency;
		this.x = x;
		this.y = y;
	}
	
	OnFillCompleteListener getListener()
	{
		return listener;
	}
	
	Image getImage()
	{
		return image;
	}
	
	float getThreshold()
	{
		return threshold;
	}
	
	float getTranslucency()
	{
		return translucency;
	}
	
	int getX()
	{
		return x;
	}
	
	int getY()
	{
		return y;
	}
}