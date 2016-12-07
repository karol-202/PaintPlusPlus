package pl.karol202.paintplus.tool.fill;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.AsyncBlocker;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.Image.OnImageChangeListener;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.fill.ToolFillAsyncTask.OnFillCompleteListener;
import pl.karol202.paintplus.tool.properties.FillProperties;
import pl.karol202.paintplus.tool.properties.ToolProperties;

public class ToolFill extends Tool implements OnFillCompleteListener
{
	private float fillThreshold;
	
	private ColorsSet colors;
	private OnImageChangeListener listener;
	private AsyncBlocker asyncBlocker;
	
	public ToolFill(Image image, OnImageChangeListener listener, AsyncBlocker asyncBlocker)
	{
		super(image);
		
		this.listener = listener;
		this.asyncBlocker = asyncBlocker;
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
			colors = image.getColorsSet();
			
			if(!asyncBlocker.block(this)) return false;
			FillParams params = new FillParams(this, image, (int) event.getX(), (int) event.getY());
			new ToolFillAsyncTask().execute(params);
		}
		return false;
	}
	
	@Override
	public void onScreenDraw(Canvas canvas) { }
	
	@Override
	public void onFillComplete(Bitmap bitmap)
	{
		image.getEditCanvas().drawBitmap(bitmap, 0, 0, null);
		listener.onImageChanged();
		if(!asyncBlocker.unblock(this)) throw new RuntimeException("Unable to unblock async blocker.");
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