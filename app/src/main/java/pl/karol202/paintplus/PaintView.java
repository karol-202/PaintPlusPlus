package pl.karol202.paintplus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.Tools;

public class PaintView extends SurfaceView implements Image.OnImageChangeListener
{
	private Image image;
	private Tools tools;
	private ColorsSet colors;
	private Tool tool;
	private Bitmap toolBitmap;
	private Paint bitmapPaint;
	private boolean initialized;

	public PaintView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public void init(ActivityPaint activity)
	{
		image = activity.getImage();
		image.setOnImageChangeListener(this);
		
		tools = activity.getTools();
		
		colors = image.getColorsSet();
		
		tool = tools.getTool(1);
		
		bitmapPaint = new Paint();
		bitmapPaint.setFilterBitmap(false);
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
		
		canvas.drawBitmap(image.getBitmap(), image.getImageMatrix(), bitmapPaint);
		
		toolBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas toolCanvas = new Canvas(toolBitmap);
		tool.onScreenDraw(toolCanvas);
		canvas.drawBitmap(toolBitmap, 0, 0, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float x = (event.getX() / image.getZoom()) + image.getViewX();
		float y = (event.getY() / image.getZoom()) + image.getViewY();
		event.setLocation(x, y);
		
		boolean result = tool.onTouch(event);
		invalidate();
		return result;
	}
	
	@Override
	public void onImageChanged()
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

	public Tool getTool()
	{
		return tool;
	}
	
	public void setTool(Tool tool)
	{
		this.tool = tool;
	}
}