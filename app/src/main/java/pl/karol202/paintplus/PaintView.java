package pl.karol202.paintplus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.Tools;

public class PaintView extends SurfaceView implements Image.ImageChangeListener
{
	private ColorsSet colors;
	private Image image;
	private Tool tool;
	private Bitmap toolBitmap;
	private boolean initialized;

	public PaintView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		if(isInEditMode()) return;
		colors = ColorsSet.getDefault();
		image = new Image(this, colors);
		Tools.init(image);
		tool = Tools.getTool(1);
		
		image.createBitmap(600, 600);
	}
	
	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
		if(isInEditMode()) return;
		if(!initialized)
		{
			image.setViewportWidth(getWidth());
			image.setViewportHeight(getHeight());
			image.centerView();
			initialized = true;
		}
		
		canvas.drawBitmap(image.getBitmap(), image.getImageMatrix(), null);
		
		toolBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas toolCanvas = new Canvas(toolBitmap);
		tool.onDraw(toolCanvas);
		canvas.drawBitmap(toolBitmap, image.getImageMatrix(), null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float x = (event.getX() / image.getZoom()) + image.getViewX();
		float y = (event.getY() / image.getZoom()) + image.getViewY();
		event.setLocation(x, y);
		
		boolean result = tool.onTouch(image.getEditCanvas(), colors, event);
		invalidate();
		return result;
	}
	
	@Override
	public void imageChanged()
	{
		invalidate();
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