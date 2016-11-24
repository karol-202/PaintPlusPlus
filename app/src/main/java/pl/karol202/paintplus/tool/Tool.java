package pl.karol202.paintplus.tool;

import android.app.Fragment;
import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.ColorsSet;
import pl.karol202.paintplus.Image;

public abstract class Tool
{
	public interface OnToolUpdatedListener
	{
		void onToolUpdated(Tool tool);
	}
	
	protected Image image;
	
	protected Tool(Image image)
	{
		this.image = image;
	}
	
	public abstract int getName();
	
	public abstract int getIcon();
	
	public abstract Class<? extends Fragment> getPropertiesFragmentClass();
	
	public abstract boolean onlyViewport();
	
	public abstract boolean onTouch(Canvas edit, ColorsSet colors, MotionEvent event);

	public abstract void onTouchOutsideViewport(Canvas edit, ColorsSet colors, MotionEvent event);

	public abstract void onDraw(Canvas canvas);
}