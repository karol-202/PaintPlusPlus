package pl.karol202.paintplus.helpers;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;
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
	
	public float snapX(float x)
	{
		if(grid.isSnapToGrid()) return grid.snapXToGrid(x);
		else return x;
	}
	
	public float snapY(float y)
	{
		if(grid.isSnapToGrid()) return grid.snapYToGrid(y);
		else return y;
	}
	
	public void snapPoint(PointF point)
	{
		if(grid.isSnapToGrid()) grid.snapPointToGrid(point);
	}
	
	public Grid getGrid()
	{
		return grid;
	}
}