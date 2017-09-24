package pl.karol202.paintplus.image.layer.mode;

import android.graphics.*;
import pl.karol202.paintplus.image.layer.Layer;

public abstract class LayerModeSimple implements LayerMode
{
	private Layer layer;
	private Paint paint;
	
	private Bitmap bitmapDst;
	private Canvas canvasDst;
	
	public LayerModeSimple()
	{
		this.paint = new Paint();
	}
	
	public LayerModeSimple(Layer layer)
	{
		this();
		this.layer = layer;
	}
	
	protected abstract PorterDuff.Mode getMode();
	
	@Override
	public void startDrawing(Bitmap bitmapDst, Canvas canvasDst)
	{
		if(layer == null) throw new NullPointerException("Layer is null");
		this.bitmapDst = bitmapDst;
		this.canvasDst = canvasDst;
		
		paint.setFilterBitmap(LayerModeType.isAntialiasing());
		paint.setXfermode(new PorterDuffXfermode(getMode()));
		paint.setAlpha((int) (layer.getOpacity() * 255f));
	}
	
	@Override
	public void addLayer(Matrix matrixLayer)
	{
		canvasDst.drawBitmap(layer.getBitmap(), matrixLayer, paint);
	}
	
	@Override
	public void addTool(Bitmap bitmapTool)
	{
		canvasDst.drawBitmap(bitmapTool, 0, 0, paint);
	}
	
	@Override
	public void setRectClipping(RectF clipRect)
	{
		if(canvasDst.getSaveCount() > 0) canvasDst.restoreToCount(1);
		canvasDst.save();
		canvasDst.clipRect(clipRect);
	}
	
	@Override
	public void resetClipping()
	{
		canvasDst.restore();
	}
	
	@Override
	public Bitmap apply()
	{
		return bitmapDst;
	}
	
	@Override
	public void setLayer(Layer layer)
	{
		this.layer = layer;
	}
	
	@Override
	public boolean replacesBitmap()
	{
		return false;
	}
}