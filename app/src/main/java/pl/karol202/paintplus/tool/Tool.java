package pl.karol202.paintplus.tool;

import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.properties.ToolProperties;

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
	
	public abstract Class<? extends ToolProperties> getPropertiesFragmentClass();
	
	public abstract boolean onTouch(Canvas edit, ColorsSet colors, MotionEvent event);

	public abstract void onScreenDraw(Canvas canvas);
}