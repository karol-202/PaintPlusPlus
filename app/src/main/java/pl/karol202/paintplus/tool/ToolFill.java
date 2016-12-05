package pl.karol202.paintplus.tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.properties.FillProperties;
import pl.karol202.paintplus.tool.properties.ToolProperties;

import java.util.Stack;

public class ToolFill extends Tool
{
	private float fillThreshold;
	
	protected ToolFill(Image image)
	{
		super(image);
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
	public boolean onTouch(Canvas edit, ColorsSet colors, MotionEvent event)
	{
		if(event.getX() < 0 || event.getY() < 0 ||
		   event.getX() > image.getWidth() - 1 || event.getY() > image.getHeight() - 1)
			return false;
		
		if(event.getAction() == MotionEvent.ACTION_DOWN) fill((int) event.getX(), (int) event.getY(), colors.getFirstColor());
		return false;
	}
	
	private void fill(int x, int y, int destColor)
	{
		Bitmap bitmap = image.getBitmap();
		int touchedColor = bitmap.getPixel(x, y);
		if(touchedColor == destColor) return;
		
		Stack<Point> pointsToCheck = new Stack<>();
		pointsToCheck.push(new Point(x, y));
		while(!pointsToCheck.isEmpty())
		{
			Point point = pointsToCheck.pop();
			int color = bitmap.getPixel(point.x, point.y);
			if(color != touchedColor) continue;
			bitmap.setPixel(point.x, point.y, destColor);
			if(point.x > 0)
				pointsToCheck.add(new Point(point.x - 1, point.y));
			if(point.y > 0)
				pointsToCheck.add(new Point(point.x, point.y - 1));
			if(point.x < image.getWidth() - 1)
				pointsToCheck.add(new Point(point.x + 1, point.y));
			if(point.y < image.getHeight() - 1)
				pointsToCheck.add(new Point(point.x, point.y + 1));
		}
	}
	
	@Override
	public void onScreenDraw(Canvas canvas) { }
	
	public float getFillThreshold()
	{
		return fillThreshold;
	}
	
	public void setFillThreshold(float fillThreshold)
	{
		this.fillThreshold = fillThreshold;
	}
}