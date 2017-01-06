package pl.karol202.paintplus.tool;

import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.image.Image;

public abstract class Tool
{
	protected Image image;
	
	protected Tool(Image image)
	{
		this.image = image;
	}
	
	public abstract int getName();
	
	public abstract int getIcon();
	
	public abstract Class<? extends ToolProperties> getPropertiesFragmentClass();
	
	public abstract boolean onTouch(MotionEvent event);

	public abstract void onScreenDraw(Canvas canvas);
}