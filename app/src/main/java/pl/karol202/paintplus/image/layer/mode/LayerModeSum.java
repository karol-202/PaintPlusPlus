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
	public void drawLayer(Canvas canvas, Layer layer, Matrix matrix)
	{
		Bitmap bitmapIn = layer.getBitmap();
		Bitmap bitmapOut = Bitmap.createBitmap(layer.getWidth(), layer.getHeight(), Bitmap.Config.ARGB_8888);
		
		Allocation allocationIn = Allocation.createFromBitmap(rs, bitmapIn);
		Allocation allocationOut = Allocation.createFromBitmap(rs, bitmapOut);
		
		script.forEach_sum(allocationIn, allocationOut);
		allocationOut.copyTo(bitmapOut);
		
		paint.setAlpha((int) (layer.getOpacity() * 255f));
		canvas.drawBitmap(bitmapOut, matrix, paint);
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