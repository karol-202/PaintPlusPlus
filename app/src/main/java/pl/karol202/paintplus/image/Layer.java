package pl.karol202.paintplus.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import pl.karol202.paintplus.image.Image.OnImageChangeListener;

public class Layer
{
	private OnImageChangeListener listener;
	private Bitmap bitmap;
	private Canvas editCanvas;
	private String name;
	private boolean visible;
	private int x;
	private int y;
	
	public Layer(int x, int y, int width, int height, String name, int color)
	{
		this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		this.bitmap.eraseColor(color);
		this.editCanvas = new Canvas(bitmap);
		this.name = name;
		this.visible = true;
		this.x = x;
		this.y = y;
	}
	
	public void setImageChnageListener(OnImageChangeListener listener)
	{
		this.listener = listener;
	}
	
	public Bitmap getBitmap()
	{
		return bitmap;
	}
	
	public Canvas getEditCanvas()
	{
		return editCanvas;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public boolean isVisible()
	{
		return visible;
	}
	
	public void setVisible(boolean visible)
	{
		this.visible = visible;
		if(listener != null) listener.onImageChanged();
	}
	
	public int getX()
	{
		return x;
	}
	
	public void setX(int x)
	{
		this.x = x;
		if(listener != null) listener.onImageChanged();
	}
	
	public int getY()
	{
		return y;
	}
	
	public void setY(int y)
	{
		this.y = y;
		if(listener != null) listener.onImageChanged();
	}
}