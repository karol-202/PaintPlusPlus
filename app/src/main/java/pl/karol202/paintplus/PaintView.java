package pl.karol202.paintplus;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolMarker;
import pl.karol202.paintplus.tool.ToolType;

import java.util.Random;

public class PaintView extends SurfaceView
{
	private Bitmap bitmap;
	private Canvas edit;
	private ColorsSet colors;
	private Tool tool;

	public PaintView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		colors = ColorsSet.getDefault();
		tool = new ToolMarker();
		clear(600, 600);
	}

	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
		canvas.drawBitmap(bitmap, 0, 0, null);
		tool.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(tool.onlyViewport())
		{
			if(event.getX() < 0 || event.getY() < 0 ||
			   event.getX() >= bitmap.getWidth() || event.getY() >= bitmap.getHeight())
			{
				tool.onTouchUp(edit, colors);
				return true;
			}
		}
		boolean result = tool.onTouch(edit, colors, event);
		invalidate();
		return result;
	}

	public ColorsSet getColors()
	{
		return colors;
	}

	public void setColors(ColorsSet colors)
	{
		this.colors = colors;
	}

	public ToolType getToolType()
	{
		return tool.getToolType();
	}

	public Tool getTool()
	{
		return tool;
	}

	public void setTool(ToolType toolType)
	{
		try
		{
			tool = toolType.getToolClass().newInstance();
		}
		catch(Exception e)
		{
			System.err.println("Error: Could not instantiate tool from tool class." +
								"Probably the tool class does not contain default constructor(without parameters).");
			e.printStackTrace();
		}
	}

	public Point getSize()
	{
		return new Point(bitmap.getWidth(), bitmap.getHeight());
	}

	public void updateTool(Tool tool)
	{
		this.tool = tool;
	}

	public void clear(int width, int height)
	{
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(colors.getSecondColor());
		edit = new Canvas(bitmap);
		tool.reset();
	}

	public void resize(int x, int y, int width, int height)
	{
		Bitmap source = bitmap;
		clear(width, height);
		edit.drawBitmap(source, -x, -y, null);
	}
}
