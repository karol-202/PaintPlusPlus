package pl.karol202.paintplus.tool.brush;

import android.graphics.*;
import android.graphics.Region.Op;
import android.view.MotionEvent;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.selection.Selection;

public class ToolBrush extends Tool
{
	private float size;
	private float shapeOffset;
	
	private Canvas canvas;
	private ColorsSet colors;
	private Shader radialShader;
	private Matrix shaderMatrix;
	private Paint paint;
	private RectF oval;
	private float lastX;
	private float lastY;
	private Path path;
	private float pathDistance;

	public ToolBrush(Image image)
	{
		super(image);
		this.size = 25;
		this.shapeOffset = 10;
		
		this.shaderMatrix = new Matrix();
		this.paint = new Paint();
		this.oval = new RectF();
		
		this.lastX = -1;
		this.lastY = -1;
		
		this.path = new Path();
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
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return BrushProperties.class;
	}
	
	@Override
	public boolean onTouch(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchStop(event.getX(), event.getY());
		return true;
	}

	private void onTouchStart(float x, float y)
	{
		canvas = image.getEditCanvas();
		colors = image.getColorsSet();
		paint.setStrokeWidth(size);
		updateShader();
		updateClipping();
		
		path.reset();
		path.moveTo(x, y);
		
		lastX = x;
		lastY = y;
	}
	
	private void updateShader()
	{
		int color = colors.getFirstColor();
		int center = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
		int edge = Color.argb(0, Color.red(color), Color.green(color), Color.blue(color));
		radialShader = new RadialGradient(0, 0, size / 2, center, edge, Shader.TileMode.CLAMP);
		paint.setShader(radialShader);
	}
	
	private void updateClipping()
	{
		Selection selection = image.getSelection();
		if(selection.isEmpty()) canvas.clipRect(0, 0, image.getWidth(), image.getHeight(), Op.REPLACE);
		else canvas.clipPath(selection.getPath(), Op.REPLACE);
	}
	
	private void onTouchMove(float x, float y)
	{
		path.quadTo(lastX, lastY, x, y);
		
		drawPointsOnPath();
		
		lastX = x;
		lastY = y;
	}

	private void onTouchStop(float x, float y)
	{
		if(lastX == -1 || lastY == -1) return;
		path.lineTo(x, y);
		
		drawPointsOnPath();
		
		path.reset();
		lastX = -1;
		lastY = -1;
		pathDistance = 0;
	}
	
	private void drawPointsOnPath()
	{
		PathMeasure pathMeasure = new PathMeasure(path, false);
		float[] point = new float[2];
		while(pathDistance <= pathMeasure.getLength())
		{
			pathMeasure.getPosTan(pathDistance, point, null);
			drawPoint(point[0], point[1]);
			pathDistance += shapeOffset;
		}
	}
	
	private void drawPoint(float x, float y)
	{
		shaderMatrix.reset();
		shaderMatrix.preTranslate(x, y);
		radialShader.setLocalMatrix(shaderMatrix);
		
		oval.left = x - size / 2;
		oval.top = y - size / 2;
		oval.right = x + size / 2;
		oval.bottom = y + size / 2;
		canvas.drawOval(oval, paint);
	}
	
	@Override
	public void onScreenDraw(Canvas canvas) { }
	
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