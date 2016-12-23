package pl.karol202.paintplus.tool.fill;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Region;
import android.os.AsyncTask;
import android.view.MotionEvent;
import pl.karol202.paintplus.AsyncBlocker;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.Image.OnImageChangeListener;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.fill.ToolFillAsyncTask.OnFillCompleteListener;

public class ToolFill extends Tool implements OnFillCompleteListener, AsyncBlocker
{
	private float fillThreshold;
	
	private Canvas canvas;
	private OnImageChangeListener listener;
	private AsyncManager asyncManager;
	private AsyncTask asyncTask;
	
	public ToolFill(Image image, OnImageChangeListener listener, AsyncManager asyncManager)
	{
		super(image);
		this.canvas = image.getEditCanvas();
		this.listener = listener;
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
	public boolean onTouch(MotionEvent event)
	{
		if(event.getX() < 0 || event.getY() < 0 ||
		   event.getX() > image.getWidth() - 1 || event.getY() > image.getHeight() - 1)
			return false;
		
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if(!asyncManager.block(this)) return false;
			cancelClipping();
			
			FillParams params = new FillParams(this, image, fillThreshold, (int) event.getX(), (int) event.getY());
			asyncTask = new ToolFillAsyncTask().execute(params);
		}
		return false;
	}
	
	private void cancelClipping()
	{
		canvas.clipRect(0, 0, canvas.getWidth(), canvas.getHeight(), Region.Op.UNION);
	}
	
	@Override
	public void onScreenDraw(Canvas canvas) { }
	
	@Override
	public void onFillComplete(Bitmap bitmap)
	{
		image.getEditCanvas().drawBitmap(bitmap, 0, 0, null);
		listener.onImageChanged();
		if(!asyncManager.unblock(this)) throw new RuntimeException("Unable to unblock async blocker.");
	}
	
	@Override
	public void cancel()
	{
		asyncTask.cancel(true);
		if(!asyncManager.unblock(this)) throw new RuntimeException("Unable to unblock async blocker.");
	}
	
	public float getFillThreshold()
	{
		return fillThreshold;
	}
	
	public void setFillThreshold(float fillThreshold)
	{
		this.fillThreshold = fillThreshold;
	}
}