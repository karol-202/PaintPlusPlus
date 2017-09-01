package pl.karol202.paintplus.tool.brush;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolBrush extends StandardTool
{
	private float size;
	private float shapeOffset;
	private float opacity;
	
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
		this.opacity = 1;
		
		this.colors = image.getColorsSet();
		
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
	public ToolCoordinateSpace getCoordinateSpace()
	{
		return ToolCoordinateSpace.LAYER_SPACE;
	}
	
	@Override
	public boolean isUsingSnapping()
	{
		return true;
	}
	
	@Override
	public boolean onTouchStart(float x, float y)
	{
		canvas = image.getSelectedCanvas();
		if(canvas == null) return false;
		layer = image.getSelectedLayer();
		resetClipping(canvas);
		doLayerAndSelectionClipping(canvas);
		
		paint.setAlpha((int) (opacity * 255));
		paint.setStrokeWidth(size);
		updateShader();
		
		path.reset();
		path.moveTo(x, y);
		
		lastX = -1;
		lastY = -1;
		return true;
	}
	
	private void updateShader()
	{
		int color = colors.getFirstColor();
		int center = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
		int edge = Color.argb(0, Color.red(color), Color.green(color), Color.blue(color));
		radialShader = new RadialGradient(0, 0, size / 2, center, edge, Shader.TileMode.CLAMP);
		paint.setShader(radialShader);
	}
	
	@Override
	public boolean onTouchMove(float x, float y)
	{
		if(lastX != -1 && lastY != -1)
		{
			path.quadTo(lastX, lastY, x, y);
			lastX = -1;
			lastY = -1;
			drawPointsOnPath();
		}
		else
		{
			lastX = x;
			lastY = y;
		}
		return true;
	}
	
	@Override
	public boolean onTouchStop(float x, float y)
	{
		if(lastX != -1 && lastY != -1) path.quadTo(lastX, lastY, x, y);
		else path.lineTo(x, y);
		
		drawPointsOnPath();
		
		path.reset();
		lastX = -1;
		lastY = -1;
		pathDistance = 0;
		return true;
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
	public boolean doesOnLayerDraw(boolean layerVisible)
	{
		return false;
	}
	
	@Override
	public boolean doesOnTopDraw()
	{
		return false;
	}
	
	@Override
	public ToolCoordinateSpace getOnLayerDrawingCoordinateSpace()
	{
		return null;
	}
	
	@Override
	public ToolCoordinateSpace getOnTopDrawingCoordinateSpace()
	{
		return null;
	}
	
	@Override
	public void onLayerDraw(Canvas canvas) { }
	
	@Override
	public void onTopDraw(Canvas canvas) { }
	
	float getSize()
	{
		return size;
	}

	void setSize(float size)
	{
		this.size = size;
	}

	float getShapeOffset()
	{
		return shapeOffset;
	}

	void setShapeOffset(float shapeOffset)
	{
		this.shapeOffset = shapeOffset;
	}
	
	float getOpacity()
	{
		return opacity;
	}
	
	void setOpacity(float opacity)
	{
		this.opacity = opacity;
	}
}