package pl.karol202.paintplus.tool.fill;

import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.tool.fill.ToolFillAsyncTask.OnFillCompleteListener;

public class FillParams
{
	private OnFillCompleteListener listener;
	private Image image;
	private float threshold;
	private float translucency;
	private int x;
	private int y;
	
	public FillParams(OnFillCompleteListener listener, Image image, float threshold, float translucency, int x, int y)
	{
		this.listener = listener;
		this.image = image;
		this.threshold = threshold;
		this.translucency = translucency;
		this.x = x;
		this.y = y;
	}
	
	public OnFillCompleteListener getListener()
	{
		return listener;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public float getThreshold()
	{
		return threshold;
	}
	
	public float getTranslucency()
	{
		return translucency;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}