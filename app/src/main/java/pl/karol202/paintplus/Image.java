package pl.karol202.paintplus;

import android.graphics.*;

public class Image
{
	public static final int FLIP_HORIZONTALLY = 0;
	public static final int FLIP_VERTICALLY = 1;
	
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
	
	public void flip(int direction)
	{
		Matrix matrix = new Matrix();
		matrix.preScale(direction == FLIP_HORIZONTALLY ? -1 : 1, direction == FLIP_VERTICALLY ? -1 : 1);
		
		Bitmap source = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
		editCanvas.drawBitmap(source, 0, 0, null);
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