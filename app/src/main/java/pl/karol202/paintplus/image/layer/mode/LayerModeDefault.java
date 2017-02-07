package pl.karol202.paintplus.image.layer.mode;

import android.graphics.*;
import pl.karol202.paintplus.image.layer.Layer;

public class LayerModeDefault implements LayerMode
{
	private int name;
	
	private Paint paint;
	
	public LayerModeDefault(int name)
	{
		this.name = name;
		this.paint = new Paint();
	}
	
	@Override
	public Bitmap drawLayer(Bitmap dst, Layer layer, Matrix matrix)
	{
		paint.setAlpha((int) (layer.getOpacity() * 255f));
		
		Canvas canvas = new Canvas(dst);
		canvas.drawBitmap(layer.getBitmap(), matrix, paint);
		return dst;
	}
	
	@Override
	public void setAntialiasing(boolean antialiasing)
	{
		paint.setFilterBitmap(antialiasing);
	}
	
	@Override
	public int getName()
	{
		return name;
	}
}