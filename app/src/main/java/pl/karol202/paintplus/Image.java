package pl.karol202.paintplus;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Image
{
	private Bitmap bitmap;
	private Canvas editCanvas;
	private ColorsSet colorsSet;
	private int width;
	private int height;
	
	public Image(ColorsSet colorsSet)
	{
		this.colorsSet = colorsSet;
	}
	
	public void createBitmap(int width, int height)
	{
		this.width = width;
		this.height = height;
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		editCanvas = new Canvas(bitmap);
		bitmap.eraseColor(colorsSet.getSecondColor());
	}
	
	public void resize(int x, int y, int width, int height)
	{
		Bitmap source = bitmap;
		createBitmap(width, height);
		editCanvas.drawBitmap(source, -x, -y, null);
	}
	
	public void scale(int width, int height, boolean bilinear)
	{
		Bitmap source = bitmap;
		createBitmap(width, height);
		
		Rect dst = new Rect(0, 0, width, height);
		Paint paint = new Paint();
		paint.setFilterBitmap(bilinear);
		editCanvas.drawBitmap(source, null, dst, paint);
	}
	
	public Bitmap getBitmap()
	{
		return bitmap;
	}
	
	public Canvas getEditCanvas()
	{
		return editCanvas;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}