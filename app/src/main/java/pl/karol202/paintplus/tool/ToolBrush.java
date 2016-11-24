package pl.karol202.paintplus.tool;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import pl.karol202.paintplus.ColorsSet;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.properties.BrushProperties;

public class ToolBrush extends Tool
{
	private float size;
	private float shapeOffset;

	private Paint paint;
	private float lastX;
	private float lastY;

	public ToolBrush(Image image)
	{
		super(image);
		this.size = 25;
		this.shapeOffset = 0;
		
		this.paint = new Paint();
		this.lastX = -1;
		this.lastY = -1;
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_brush;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_brush_black_48dp;
	}
	
	@Override
	public Class<? extends Fragment> getPropertiesFragmentClass()
	{
		return BrushProperties.class;
	}
	
	@Override
	public boolean onlyViewport()
	{
		return true;
	}
	
	@Override
	public boolean onTouch(Canvas edit, ColorsSet colors, MotionEvent event)
	{
		return true;
	}

	@Override
	public void onTouchOutsideViewport(Canvas edit, ColorsSet colors, MotionEvent event)
	{

	}

	private void onTouchStart(Canvas canvas, ColorsSet colors, float x, float y)
	{

	}

	private void onTouchMove(Canvas canvas, ColorsSet colors, float x, float y)
	{

	}

	private void onTouchStop(Canvas canvas, ColorsSet colors, float x, float y)
	{

	}

	@Override
	public void onDraw(Canvas canvas)
	{

	}
	
	public float getSize()
	{
		return size;
	}

	public void setSize(float size)
	{
		this.size = size;
	}

	public float getShapeOffset()
	{
		return shapeOffset;
	}

	public void setShapeOffset(float shapeOffset)
	{
		this.shapeOffset = shapeOffset;
	}
}