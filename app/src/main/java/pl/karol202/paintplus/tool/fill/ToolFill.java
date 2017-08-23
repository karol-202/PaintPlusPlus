package pl.karol202.paintplus.tool.fill;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Region;
import android.os.AsyncTask;
import pl.karol202.paintplus.AsyncBlocker;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.CoordinateSpace;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.fill.ToolFillAsyncTask.OnFillCompleteListener;

public class ToolFill extends StandardTool implements OnFillCompleteListener, AsyncBlocker
{
	private float fillThreshold;
	private float opacity;
	
	private Canvas canvas;
	private AsyncManager asyncManager;
	private AsyncTask asyncTask;
	
	public ToolFill(Image image, AsyncManager asyncManager)
	{
		super(image);
		this.fillThreshold = 0;
		this.opacity = 1;
		
		this.asyncManager = asyncManager;
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_fill;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_fill_black_24dp;
	}
	
	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return FillProperties.class;
	}
	
	@Override
	public CoordinateSpace getCoordinateSpace()
	{
		return CoordinateSpace.LAYER_SPACE;
	}
	
	@Override
	public boolean isUsingSnapping()
	{
		return false;
	}
	
	@Override
	public boolean onTouchStart(float x, float y)
	{
		Layer layer = image.getSelectedLayer();
		if(x < 0 || y < 0 || x > layer.getWidth() - 1 || y > layer.getHeight() - 1) return false;
		
		canvas = image.getSelectedCanvas();
		if(canvas == null) return false;
		
		if(!asyncManager.block(this)) return false;
		cancelClipping();
		
		FillParams params = new FillParams(this, image, fillThreshold, 1 - opacity, (int) x, (int) y);
		asyncTask = new ToolFillAsyncTask().execute(params);
		return false;
	}
	
	@Override
	public boolean onTouchMove(float x, float y)
	{
		return false;
	}
	
	@Override
	public boolean onTouchStop(float x, float y)
	{
		return false;
	}
	
	private void cancelClipping()
	{
		canvas.clipRect(0, 0, canvas.getWidth(), canvas.getHeight(), Region.Op.UNION);
	}
	
	@Override
	public boolean isImageLimited()
	{
		return true;
	}
	
	@Override
	public boolean doesScreenDraw(boolean layerVisible)
	{
		return false;
	}
	
	@Override
	public boolean isDrawingOnTop()
	{
		return false;
	}
	
	@Override
	public void onScreenDraw(Canvas canvas) { }
	
	@Override
	public void onFillComplete(Bitmap bitmap)
	{
		canvas.drawBitmap(bitmap, 0, 0, null);
		image.updateImage();
		if(!asyncManager.unblock(this)) throw new RuntimeException("Unable to unblock async blocker.");
	}
	
	@Override
	public void cancel()
	{
		asyncTask.cancel(true);
		if(!asyncManager.unblock(this)) throw new RuntimeException("Unable to unblock async blocker.");
	}
	
	@Override
	public int getMessage()
	{
		return R.string.dialog_fill_message;
	}
	
	float getFillThreshold()
	{
		return fillThreshold;
	}
	
	void setFillThreshold(float fillThreshold)
	{
		this.fillThreshold = fillThreshold;
	}
	
	float getOpacity()
	{
		return opacity;
	}
	
	void setOpacity(float opacity)
	{
		this.opacity = opacity;
	}
}