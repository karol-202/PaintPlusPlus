package pl.karol202.paintplus.helpers;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.image.Image;

public class HelpersManager
{
	private Image image;
	
	private Grid grid;
	
	public HelpersManager(Image image, Resources resources)
	{
		this.image = image;
		
		this.grid = new Grid(image, resources);
	}
	
	public void onScreenDraw(Canvas canvas)
	{
		grid.onScreenDraw(canvas);
	}
	
	//Not used yet.
	public void onTouch(MotionEvent event)
	{
		
	}
	
	public void setGridEnabled(boolean enabled)
	{
		grid.setEnabled(enabled);
	}
}