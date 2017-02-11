package pl.karol202.paintplus.image.layer.mode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import pl.karol202.paintplus.image.layer.Layer;

public class LayerModeDefault implements LayerMode
{
	private Layer layer;
	private Paint paint;
	private Bitmap lastBitmap;
	private Canvas canvas;
	
	public LayerModeDefault()
	{
		this.paint = new Paint();
	}
	
	public LayerModeDefault(Layer layer)
	{
		this();
		this.layer = layer;
	}
	
	@Override
	public Bitmap drawLayer(Bitmap dst, Matrix matrix)
	{
		if(layer == null) throw new NullPointerException("Layer is null");
		return drawBitmap(dst, layer.getBitmap(), matrix);
	}
	
	@Override
	public Bitmap drawLayerAndTool(Bitmap dst, Matrix matrix, Bitmap toolBitmap)
	{
		drawLayer(dst, matrix);
		canvas.drawBitmap(toolBitmap, 0, 0, paint);
		return dst;
	}
	
	@Override
	public Bitmap drawTool(Bitmap dst, Bitmap toolBitmap)
	{
		return drawBitmap(dst, toolBitmap, null);
	}
	
	private Bitmap drawBitmap(Bitmap dst, Bitmap src, Matrix matrix)
	{
		paint.setFilterBitmap(LayerModeType.isAntialiasing());
		paint.setAlpha((int) (layer.getOpacity() * 255f));
		
		if(dst != lastBitmap || canvas == null) canvas = new Canvas(dst);
		if(matrix != null) canvas.drawBitmap(src, matrix, paint);
		else canvas.drawBitmap(src, 0, 0, paint);
		
		lastBitmap = dst;
		return dst;
	}
	
	@Override
	public void setLayer(Layer layer)
	{
		this.layer = layer;
	}
}