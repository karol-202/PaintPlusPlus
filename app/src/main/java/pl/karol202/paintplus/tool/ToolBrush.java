package pl.karol202.paintplus.tool;

import android.app.Fragment;
import android.graphics.*;
import android.view.MotionEvent;
import pl.karol202.paintplus.ColorsSet;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.properties.BrushProperties;

public class ToolBrush extends Tool
{
	private float size;
	private float shapeOffset;
	
	private Shader radialShader;
	private Paint paint;
	private RectF oval;
	private float lastX;
	private float lastY;
	private Path path;
	private PathMeasure pathMeasure;
	private float[] point;
	private float distance;

	public ToolBrush(Image image)
	{
		super(image);
		this.size = 25;
		this.shapeOffset = 10;
		
		this.paint = new Paint();
		this.oval = new RectF();
		
		this.lastX = -1;
		this.lastY = -1;
		
		this.path = new Path();
		this.pathMeasure = new PathMeasure(path, false);
		this.point = new float[2];
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_brush;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_brush_black_24dp;
	}
	
	@Override
	public Class<? extends Fragment> getPropertiesFragmentClass()
	{
		return BrushProperties.class;
	}
	
	@Override
	public boolean onlyViewport()
	{
		return false;
	}
	
	@Override
	public boolean onTouch(Canvas edit, ColorsSet colors, MotionEvent event)
	{
		paint.setStrokeWidth(size);
		
		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(edit, colors, event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(edit, colors, event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchStop(edit, colors, event.getX(), event.getY());
		return true;
	}

	@Override
	public void onTouchOutsideViewport(Canvas edit, ColorsSet colors, MotionEvent event) { }

	private void onTouchStart(Canvas canvas, ColorsSet colors, float x, float y)
	{
		path.reset();
		path.moveTo(x, y);
		
		lastX = x;
		lastY = y;
	}

	private void onTouchMove(Canvas canvas, ColorsSet colors, float x, float y)
	{
		path.quadTo(lastX, lastY, x, y);
		
		drawPointsOnPath(canvas, colors);
		
		lastX = x;
		lastY = y;
	}

	private void onTouchStop(Canvas canvas, ColorsSet colors, float x, float y)
	{
		if(lastX == -1 || lastY == -1) return;
		path.lineTo(x, y);
		
		drawPointsOnPath(canvas, colors);
		
		path.reset();
		lastX = -1;
		lastY = -1;
		distance = 0;
	}
	
	private void drawPointsOnPath(Canvas canvas, ColorsSet colors)
	{
		pathMeasure = new PathMeasure(path, false);
		while(distance <= pathMeasure.getLength())
		{
			pathMeasure.getPosTan(distance, point, null);
			drawPoint(canvas, colors, point[0], point[1]);
			distance += shapeOffset;
		}
	}
	
	private void drawPoint(Canvas canvas, ColorsSet colors, float x, float y)
	{
		int color = colors.getFirstColor();
		int center = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
		int edge = Color.argb(0, Color.red(color), Color.green(color), Color.blue(color));
		radialShader = new RadialGradient(x, y, size / 2, center, edge, Shader.TileMode.CLAMP);
		paint.setShader(radialShader);
		
		oval.left = x - size / 2;
		oval.top = y - size / 2;
		oval.right = x + size / 2;
		oval.bottom = y + size / 2;
		canvas.drawOval(oval, paint);
	}
	
	@Override
	public void onDraw(Canvas canvas) { }
	
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