package pl.karol202.paintplus.image.layer.mode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import pl.karol202.paintplus.image.layer.Layer;

public class LayerModeSum implements LayerMode
{
	private Layer layer;
	private Paint paint;
	private RenderScript rs;
	private ScriptC_sum script;
	
	private Canvas canvasSrc;
	private Bitmap bitmapOut;
	private Allocation allocationOut;
	
	public LayerModeSum()
	{
		paint = new Paint();
		rs = LayerModeType.getRenderScript();
		script = new ScriptC_sum(rs);
	}
	
	@Override
	public Bitmap drawLayer(Bitmap bitmapDst, Matrix matrix)
	{
		return drawLayerAndTool(bitmapDst, matrix, null);
	}
	
	@Override
	public Bitmap drawLayerAndTool(Bitmap bitmapDst, Matrix matrix, Bitmap toolBitmap)
	{
		if(layer == null) throw new NullPointerException("Layer is null");
		if(bitmapOut == null || bitmapOut.getWidth() != bitmapDst.getWidth() || bitmapOut.getHeight() != bitmapDst.getHeight())
		{
			bitmapOut = Bitmap.createBitmap(bitmapDst.getWidth(), bitmapDst.getHeight(), Bitmap.Config.ARGB_8888);
			allocationOut = Allocation.createFromBitmap(rs, bitmapOut);
		}
		paint.setFilterBitmap(LayerModeType.isAntialiasing());
		
		Bitmap bitmapSrc = Bitmap.createBitmap(bitmapDst.getWidth(), bitmapDst.getHeight(), Bitmap.Config.ARGB_8888);
		canvasSrc = new Canvas(bitmapSrc);
		if(matrix != null) canvasSrc.drawBitmap(layer.getBitmap(), matrix, paint);
		if(toolBitmap != null) canvasSrc.drawBitmap(toolBitmap, 0, 0, paint);
		
		Allocation allocationDst = Allocation.createFromBitmap(rs, bitmapDst);
		Allocation allocationSrc = Allocation.createFromBitmap(rs, bitmapSrc);
		
		script.set_dstAlloc(allocationDst);
		script.set_opacity(layer.getOpacity());
		script.forEach_sum(allocationSrc, allocationOut);
		allocationOut.copyTo(bitmapOut);
		
		return bitmapOut;
	}
	
	@Override
	public Bitmap drawTool(Bitmap bitmapDst, Bitmap toolBitmap)
	{
		return drawLayerAndTool(bitmapDst, null, toolBitmap);
	}
	
	@Override
	public void setLayer(Layer layer)
	{
		this.layer = layer;
	}
}