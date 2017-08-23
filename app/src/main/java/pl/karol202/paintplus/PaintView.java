package pl.karol202.paintplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Image.OnImageChangeListener;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.image.layer.mode.LayerModeType;
import pl.karol202.paintplus.settings.ActivitySettings;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.selection.Selection;

import java.util.ArrayList;
import java.util.Collections;

public class PaintView extends SurfaceView implements OnImageChangeListener, Selection.OnSelectionChangeListener
{
	private final float[] PAINT_DASH = new float[] { 5f, 5f };
	private final float CHECKERBOARD_OFFSET = 8;
	
	private ActivityPaint activity;
	private Image image;
	private ColorsSet colors;
	private HelpersManager helpersManager;
	
	private Paint selectionPaint;
	private Paint layerBoundsPaint;
	private Paint checkerboardPaint;
	private Shader checkerboardShader;
	private boolean initialized;
	
	private Matrix checkerboardMatrix;
	private ArrayList<Layer> reversedLayers;
	private Path boundsPath;
	private Path selectionPath;

	public PaintView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public void init(ActivityPaint activity)
	{
		this.activity = activity;
		
		image = activity.getImage();
		image.setOnImageChangeListener(this);
		image.addOnSelectionChangeListener(this);
		colors = image.getColorsSet();
		helpersManager = image.getHelpersManager();
		
		selectionPaint = new Paint();
		selectionPaint.setStyle(Paint.Style.STROKE);
		selectionPaint.setStrokeWidth(2f);
		selectionPaint.setPathEffect(new DashPathEffect(PAINT_DASH, 0));
		
		layerBoundsPaint = new Paint();
		layerBoundsPaint.setStyle(Paint.Style.STROKE);
		layerBoundsPaint.setColor(Color.GRAY);
		layerBoundsPaint.setStrokeWidth(2f);
		layerBoundsPaint.setPathEffect(new DashPathEffect(PAINT_DASH, 0));
		
		Bitmap checkerboard = BitmapFactory.decodeResource(activity.getResources(), R.drawable.checkerboard);
		checkerboardShader = new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		checkerboardPaint = new Paint();
		checkerboardPaint.setShader(checkerboardShader);
		checkerboardPaint.setFilterBitmap(false);
	}
	
	public void updatePreferences()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		boolean smooth = preferences.getBoolean(ActivitySettings.KEY_VIEW_SMOOTH, true);
		
		LayerModeType.setAntialiasing(smooth);
	}
	
	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
		if(isInEditMode()) return;
		if(!initialized) initImage();
		
		setClipping(canvas);
		drawCheckerboard(canvas);
		drawImage(canvas);
		removeClipping(canvas);
		drawLayerBounds(canvas);
		drawSelection(canvas);
		
		helpersManager.onScreenDraw(canvas);
	}
	
	private void initImage()
	{
		image.setViewportWidth(getWidth());
		image.setViewportHeight(getHeight());
		image.centerView();
		
		onImageChanged();
		onLayersChanged();
		initialized = true;
	}
	
	private void setClipping(Canvas canvas)
	{
		float viewX = -image.getViewX() * image.getZoom();
		float viewY = -image.getViewY() * image.getZoom();
		float width = image.getWidth() * image.getZoom();
		float height = image.getHeight() * image.getZoom();
		
		canvas.clipRect(viewX, viewY, viewX + width, viewY + height);
	}
	
	private void removeClipping(Canvas canvas)
	{
		canvas.clipRect(0, 0, canvas.getWidth(), canvas.getHeight(), Region.Op.UNION);
	}
	
	private void drawCheckerboard(Canvas canvas)
	{
		checkerboardShader.setLocalMatrix(checkerboardMatrix);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), checkerboardPaint);
	}
	
	private void drawImage(Canvas canvas)
	{
		Tool tool = getTool();
		Bitmap screenBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		
		Bitmap toolBitmap = null;
		if(tool.doesScreenDraw(image.getSelectedLayer() != null))
		{
			toolBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
			Canvas toolCanvas = new Canvas(toolBitmap);
			tool.onScreenDraw(toolCanvas);
		}
		
		if(reversedLayers == null) return;
		for(Layer layer : reversedLayers)
		{
			boolean drawTool = tool.doesScreenDraw(layer.isVisible()) && !tool.isDrawingOnTop() && image.isLayerSelected(layer);
			if(layer.isVisible())
			{
				Matrix imageMatrix = new Matrix(image.getImageMatrix());
				if(drawTool) screenBitmap = layer.drawLayerAndTool(screenBitmap, imageMatrix, toolBitmap);
				else screenBitmap = layer.drawLayer(screenBitmap, imageMatrix);
			}
			else if(drawTool) screenBitmap = layer.drawTool(screenBitmap, toolBitmap);
		}
		canvas.drawBitmap(screenBitmap, 0, 0, null);
		
		if(tool.doesScreenDraw(true))
		{
			if(tool.isDrawingOnTop())
			{
				if(!tool.isImageLimited()) removeClipping(canvas);
				canvas.drawBitmap(toolBitmap, 0, 0, null);
				if(!tool.isImageLimited()) setClipping(canvas);
			}
			else if(!tool.isImageLimited())
			{
				canvas.clipRect(0, 0, canvas.getWidth(), canvas.getHeight(), Region.Op.XOR);
				canvas.drawBitmap(toolBitmap, 0, 0, null);
			}
		}
	}
	
	private void drawLayerBounds(Canvas canvas)
	{
		if(image.getSelectedLayer() == null) return;
		canvas.drawPath(boundsPath, layerBoundsPaint);
	}
	
	private void drawSelection(Canvas canvas)
	{
		canvas.drawPath(selectionPath, selectionPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(image.getSelectedLayer() == null) return false;
		Tool tool = getTool();
		
		boolean result = tool.onTouch(event, getContext());
		if(result) invalidate();
		return result;
	}
	
	@Override
	public void onImageChanged()
	{
		if(image == null) return;
		updateCheckerboardMatrix();
		updateLayerBounds();
		updateSelectionPath();
		activity.updateLayersPreview();
		invalidate();
	}
	
	@Override
	public void onLayersChanged()
	{
		if(image == null) return;
		activity.updateLayersPreview();
		reversedLayers = new ArrayList<>(image.getLayers());
		Collections.reverse(reversedLayers);
		updateLayerBounds();
	}
	
	@Override
	public void onSelectionChanged()
	{
		updateSelectionPath();
	}
	
	private void updateCheckerboardMatrix()
	{
		checkerboardMatrix = new Matrix();
		checkerboardMatrix.preTranslate(-image.getViewX() * image.getZoom() + CHECKERBOARD_OFFSET, -image.getViewY() * image.getZoom() + CHECKERBOARD_OFFSET);
	}
	
	private void updateLayerBounds()
	{
		Layer selected = image.getSelectedLayer();
		if(selected == null) return;
		
		RectF bounds = new RectF(selected.getBounds());
		bounds.intersect(getScreenRect());
		boundsPath = new Path();
		boundsPath.addRect(bounds, Path.Direction.CW);
		boundsPath.transform(image.getImageMatrix());
	}
	
	private void updateSelectionPath()
	{
		Region region = new Region(image.getSelection().getRegion());
		Rect screen = new Rect();
		getScreenRect().round(screen);
		region.op(screen, Region.Op.INTERSECT);
		
		selectionPath = region.getBoundaryPath();
		selectionPath.transform(image.getImageMatrix());
	}
	
	private RectF getScreenRect()
	{
		return new RectF(image.getViewX() - 2,
						 image.getViewY() - 2,
						 image.getViewX() + (getWidth() / image.getZoom()) + 2,
						 image.getViewY() + (getHeight() / image.getZoom()) + 2);
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public ColorsSet getColors()
	{
		return colors;
	}
	
	private Tool getTool()
	{
		return activity.getTool();
	}
	
	public boolean isGridEnabled()
	{
		return helpersManager.getGrid().isEnabled();
	}
	
	public void setGridEnabled(boolean enabled)
	{
		helpersManager.getGrid().setEnabled(enabled);
	}
	
	public boolean isSnapToGridEnabled()
	{
		return helpersManager.getGrid().isSnapToGrid();
	}
	
	public void setSnapToGrid(boolean enabled)
	{
		helpersManager.getGrid().setSnapToGrid(enabled);
	}
}