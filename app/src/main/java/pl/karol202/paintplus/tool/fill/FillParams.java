package pl.karol202.paintplus.tool.fill;

import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.tool.fill.ToolFillAsyncTask.OnFillCompleteListener;

public class FillParams
{
	private OnFillCompleteListener listener;
	private Image image;
	private int x;
	private int y;
	
	public FillParams(OnFillCompleteListener listener, Image image, int x, int y)
	{
		this.listener = listener;
		this.image = image;
		this.x = x;
		this.y = y;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public OnFillCompleteListener getListener()
	{
		return listener;
	}
}