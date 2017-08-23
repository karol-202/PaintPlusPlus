package pl.karol202.paintplus.tool.marker;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region.Op;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.CoordinateSpace;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.selection.Selection;

public class ToolMarker extends StandardTool
{
	private float size;
	private float opacity;
	private boolean smoothEdge;
	
	private Canvas canvas;
	private ColorsSet colors;
	private Path selectionPath;
	private Layer layer;
	
	private MarkerAdapterQuadraticPath adapterQuadraticPath;

	public ToolMarker(Image image)
	{
		super(image);
		this.size = 25;
		this.opacity = 1;
		this.smoothEdge = true;
		
		this.colors = image.getColorsSet();
		this.layer = image.getSelectedLayer();
		updateSelectionPath();
		
		this.adapterQuadraticPath = new MarkerAdapterQuadraticPath(this);
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_marker;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_marker_black_24dp;
	}
	
	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return MarkerProperties.class;
	}
	
	@Override
	public CoordinateSpace getCoordinateSpace()
	{
		return CoordinateSpace.LAYER_SPACE;
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
		
		updateSelectionPath();
		updateClipping(canvas);
		
		getCurrentAdapter().onBeginDraw(x, y);
		return true;
	}
	
	@Override
	public boolean onTouchMove(float x, float y)
	{
		getCurrentAdapter().onDraw(x, y);
		return true;
	}
	
	@Override
	public boolean onTouchStop(float x, float y)
	{
		getCurrentAdapter().onEndDraw(x, y);
		return true;
	}
	
	@Override
	public boolean isImageLimited()
	{
		return true;
	}
	
	@Override
	public boolean doesScreenDraw(boolean layerVisible)
	{
		return layerVisible;
	}
	
	@Override
	public boolean isDrawingOnTop()
	{
		return false;
	}
	
	@Override
	public void onScreenDraw(Canvas canvas)
	{
		layer = image.getSelectedLayer();
		
		canvas.scale(image.getZoom(), image.getZoom());
		canvas.translate(-image.getViewX() + layer.getX(),
						 -image.getViewY() + layer.getY());
		
		updateClipping(canvas);
		getCurrentAdapter().onScreenDraw(canvas);
	}
	
	private void updateClipping(Canvas canvas)
	{
		Selection selection = image.getSelection();
		
		canvas.clipRect(0, 0, layer.getWidth(), layer.getHeight(), Op.REPLACE);
		if(!selection.isEmpty()) canvas.clipPath(selectionPath, Op.INTERSECT);
	}
	
	private void updateSelectionPath()
	{
		selectionPath = new Path(image.getSelection().getPath());
		selectionPath.offset(-layer.getX(), -layer.getY());
	}
	
	private MarkerAdapter getCurrentAdapter()
	{
		return adapterQuadraticPath;
	}
	
	float getSize()
	{
		return size;
	}

	void setSize(float size)
	{
		this.size = size;
	}
	
	float getOpacity()
	{
		return opacity;
	}
	
	void setOpacity(float opacity)
	{
		this.opacity = opacity;
	}
	
	boolean isSmoothEdge()
	{
		return smoothEdge;
	}
	
	void setSmoothEdge(boolean smoothEdge)
	{
		this.smoothEdge = smoothEdge;
	}
	
	Canvas getCanvas()
	{
		return canvas;
	}
	
	ColorsSet getColors()
	{
		return colors;
	}
}