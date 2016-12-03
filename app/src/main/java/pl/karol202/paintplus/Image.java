package pl.karol202.paintplus;

import android.graphics.*;

public class Image
{
	public interface ImageChangeListener
	{
		void imageChanged();
	}
	
	public static final int FLIP_HORIZONTALLY = 0;
	public static final int FLIP_VERTICALLY = 1;
	
	private ImageChangeListener listener;
	private Bitmap bitmap;
	private Canvas editCanvas;
	private ColorsSet colorsSet;
	private int width;
	private int height;
	
	private int viewX;
	private int viewY;
	private float zoom;
	private Matrix imageMatrix;
	private int viewportWidth;
	private int viewportHeight;
	
	public Image(ImageChangeListener listener, ColorsSet colorsSet)
	{
		this.listener = listener;
		this.colorsSet = colorsSet;
		this.zoom = 1;
		this.imageMatrix = new Matrix();
		updateMatrix();
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
	
	public void rotate(float angle)
	{
		Matrix matrix = new Matrix();
		matrix.preRotate(angle);
		
		Bitmap source = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		createBitmap(source.getWidth(), source.getHeight());
		editCanvas.drawBitmap(source, 0, 0, null);
	}
	
	private void updateMatrix()
	{
		Matrix matrix = new Matrix();
		matrix.postTranslate(-viewX, -viewY);
		matrix.postScale(zoom, zoom);
		
		imageMatrix.set(matrix);
		listener.imageChanged();
	}
	
	public void centerView()
	{
		viewX = (int) (((width * zoom / 2) - (viewportWidth / 2)) / zoom);
		viewY = (int) (((height * zoom / 2) - (viewportHeight / 2)) / zoom);
		updateMatrix();
	}
	
	public Bitmap getBitmap()
	{
		return bitmap;
	}
	
	public void setBitmap(Bitmap bitmap)
	{
		if(!bitmap.isMutable()) this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		else this.bitmap = bitmap;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		editCanvas = new Canvas(this.bitmap);
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
	
	public int getViewX()
	{
		return viewX;
	}
	
	public int getViewY()
	{
		return viewY;
	}
	
	public void setViewX(int viewX)
	{
		this.viewX = viewX;
		updateMatrix();
	}
	
	public void setViewY(int viewY)
	{
		this.viewY = viewY;
		updateMatrix();
	}
	
	public float getZoom()
	{
		return zoom;
	}
	
	public void setZoom(float zoom)
	{
		this.viewX += ((viewportWidth / this.zoom) - (viewportWidth / zoom)) / 2;
		this.viewY += ((viewportHeight / this.zoom) - (viewportHeight / zoom)) / 2;
		
		this.zoom = zoom;
		updateMatrix();
	}
	
	public Matrix getImageMatrix()
	{
		return imageMatrix;
	}
	
	public int getViewportWidth()
	{
		return viewportWidth;
	}
	
	public void setViewportWidth(int viewportWidth)
	{
		this.viewportWidth = viewportWidth;
	}
	
	public int getViewportHeight()
	{
		return viewportHeight;
	}
	
	public void setViewportHeight(int viewportHeight)
	{
		this.viewportHeight = viewportHeight;
	}
}