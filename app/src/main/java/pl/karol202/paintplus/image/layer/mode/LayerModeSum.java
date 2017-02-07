package pl.karol202.paintplus.image.layer.mode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.layer.Layer;

public class LayerModeSum implements LayerMode
{
	private Paint paint;
	
	private RenderScript rs;
	private ScriptC_sum script;
	
	public LayerModeSum()
	{
		paint = new Paint();
		
		rs = LayerModes.getRenderScript();
		script = new ScriptC_sum(rs);
	}
	
	@Override
	public Bitmap drawLayer(Bitmap bitmapDst, Layer layer, Matrix matrix)
	{
		Bitmap bitmapSrc = Bitmap.createBitmap(bitmapDst.getWidth(), bitmapDst.getHeight(), Bitmap.Config.ARGB_8888);
		Bitmap bitmapOut = Bitmap.createBitmap(bitmapDst.getWidth(), bitmapDst.getHeight(), Bitmap.Config.ARGB_8888);
		
		Canvas canvasSrc = new Canvas(bitmapSrc);
		canvasSrc.drawBitmap(layer.getBitmap(), matrix, paint);
		
		Allocation allocationDst = Allocation.createFromBitmap(rs, bitmapDst);
		Allocation allocationSrc = Allocation.createFromBitmap(rs, bitmapSrc);
		Allocation allocationOut = Allocation.createFromBitmap(rs, bitmapOut);
		
		script.set_dstAlloc(allocationDst);
		script.set_opacity(layer.getOpacity());
		script.forEach_sum(allocationSrc, allocationOut);
		allocationOut.copyTo(bitmapOut);
		
		return bitmapOut;
	}
	
	@Override
	public void setAntialiasing(boolean antialiasing)
	{
		paint.setFilterBitmap(antialiasing);
	}
	
	@Override
	public int getName()
	{
		return R.string.layer_mode_add;
	}
}