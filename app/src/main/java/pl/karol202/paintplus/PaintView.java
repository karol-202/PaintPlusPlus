package pl.karol202.paintplus;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.Tools;

public class PaintView extends SurfaceView
{
	private ColorsSet colors;
	private Image image;
	private Tool tool;

	public PaintView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		colors = ColorsSet.getDefault();
		image = new Image(colors);
		
		Tools.init(image);
		tool = Tools.getTool(0);
		image.createBitmap(600, 600);
	}

	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
		canvas.drawBitmap(image.getBitmap(), 0, 0, null);
		tool.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		boolean touchInViewport = event.getX() >= 0 && event.getY() >= 0 &&
				event.getX() < image.getWidth() && event.getY() < image.getHeight();
		boolean result = true;
		if(tool.onlyViewport() && !touchInViewport) tool.onTouchOutsideViewport(image.getEditCanvas(), colors, event);
		else result = tool.onTouch(image.getEditCanvas(), colors, event);
		invalidate();
		return result;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public ColorsSet getColors()
	{
		return colors;
	}

	/*public void setColors(ColorsSet colors)
	{
		this.colors = colors;
	}*/

	public Tool getTool()
	{
		return tool;
	}
	
	public void setTool(Tool tool)
	{
		this.tool = tool;
	}
}
