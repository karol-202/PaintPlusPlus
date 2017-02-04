package pl.karol202.paintplus.image.layer.mode;

import android.graphics.*;
import pl.karol202.paintplus.image.layer.Layer;

public class LayerModeDefault implements LayerMode
{
	private int name;
	
	private Paint paint;
	
	public LayerModeDefault(int name, PorterDuff.Mode mode)
	{
		this.name = name;
		this.paint = new Paint();
		this.paint.setXfermode(new PorterDuffXfermode(mode));
	}
	
	@Override
	public void drawLayer(Canvas canvas, Layer layer, Matrix matrix)
	{
		paint.setAlpha((int) (layer.getOpacity() * 255f));
		canvas.drawBitmap(layer.getBitmap(), matrix, paint);
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