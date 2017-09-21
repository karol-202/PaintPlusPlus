package pl.karol202.paintplus.image.layer.mode;

import android.graphics.*;
import pl.karol202.paintplus.image.layer.Layer;

public abstract class LayerModeSimple implements LayerMode
{
	private Layer layer;
	private Paint paint;
	
	private Bitmap bitmapOut;
	private Canvas canvasOut;
	
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
		updateOutIfOutdated(bitmapDst);
		canvasOut.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		canvasOut.drawBitmap(bitmapDst, 0, 0, null);
		
		paint.setFilterBitmap(LayerModeType.isAntialiasing());
		paint.setXfermode(new PorterDuffXfermode(getMode()));
		paint.setAlpha((int) (layer.getOpacity() * 255f));
	}
	
	private void updateOutIfOutdated(Bitmap bitmapDst)
	{
		if(bitmapOut != null && bitmapOut.getWidth() == bitmapDst.getWidth() && bitmapOut.getHeight() == bitmapDst.getHeight())
			return;
		bitmapOut = Bitmap.createBitmap(bitmapDst.getWidth(), bitmapDst.getHeight(), Bitmap.Config.ARGB_8888);
		canvasOut = new Canvas(bitmapOut);
	}
	
	@Override
	public void addLayer(Matrix matrixLayer)
	{
		canvasOut.drawBitmap(layer.getBitmap(), matrixLayer, paint);
	}
	
	@Override
	public void addTool(Bitmap bitmapTool)
	{
		canvasOut.drawBitmap(bitmapTool, 0, 0, paint);
	}
	
	@Override
	public void setRectClipping(RectF clipRect)
	{
		if(canvasOut.getSaveCount() > 0) canvasOut.restoreToCount(1);
		canvasOut.save();
		canvasOut.clipRect(clipRect);
	}
	
	@Override
	public void resetClipping()
	{
		canvasOut.restore();
	}
	
	@Override
	public Bitmap apply()
	{
		return bitmapOut;
	}
	
	@Override
	public void setLayer(Layer layer)
	{
		this.layer = layer;
	}
}