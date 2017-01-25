package pl.karol202.paintplus.image;

import android.graphics.*;
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
	
	public void offset(int x, int y)
	{
		this.x += x;
		this.y += y;
	}
	
	public void scale(double scaleX, double scaleY, boolean bilinear)
	{
		Bitmap source = bitmap;
		int width = (int) (bitmap.getWidth() * scaleX);
		int height = (int) (bitmap.getHeight() * scaleY);
		
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		editCanvas = new Canvas(bitmap);
		
		Paint paint = new Paint();
		paint.setFilterBitmap(bilinear);
		Rect rect = new Rect(0, 0, width, height);
		editCanvas.drawBitmap(source, null, rect, paint);
	}
	
	public void flip(int direction)
	{
		Matrix matrix = new Matrix();
		matrix.preScale(direction == Image.FLIP_HORIZONTALLY ? -1 : 1, direction == Image.FLIP_VERTICALLY ? -1 : 1);
		
		Bitmap source = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		bitmap.eraseColor(Color.TRANSPARENT);
		editCanvas.drawBitmap(source, 0, 0, null);
	}
	
	public void rotate()
	{
		//Will be added soon
	}
	
	public void setImageChangeListener(OnImageChangeListener listener)
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
	
	public RectF getBounds()
	{
		return new RectF(x, y, x + getWidth(), y + getHeight());
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
	
	public int getWidth()
	{
		return bitmap.getWidth();
	}
	
	public int getHeight()
	{
		return bitmap.getHeight();
	}
}