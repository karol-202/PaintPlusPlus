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
import pl.karol202.paintplus.image.layer.mode.LayerMode;
import pl.karol202.paintplus.image.layer.mode.LayerModeType;
import pl.karol202.paintplus.settings.ActivitySettings;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
import pl.karol202.paintplus.tool.selection.Selection;

import java.util.ArrayList;
import java.util.Collections;

public class PaintView extends SurfaceView implements OnImageChangeListener, Selection.OnSelectionChangeListener
{
	private final float[] PAINT_DASH = new float[] { 5f, 5f };
	private final float CHECKERBOARD_OFFSET = 8;
	
	private ActivityPaint activity;
	private Image image;
	private Selection selection;
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
	private RectF imageRect;
	
	private Path rawLimitedSelectionPath;
	private Path limitedSelectionPath;
	private Path rawSelectionPath;
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
		selection = image.getSelection();
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
		
		imageRect = new RectF();
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
		removeClipping(canvas);
		drawImage(canvas);
		drawLayerBounds(canvas);
		drawSelection(canvas);
		
		helpersManager.onScreenDraw(canvas);
	}
	
	private void initImage()
	{
		image.setViewportWidth(getWidth());
		image.setViewportHeight(getHeight());
		image.centerView();
		
		onSelectionChanged();
		onImageChanged();
		onLayersChanged();
		initialized = true;
	}
	
	private void setClipping(Canvas canvas)
	{
		canvas.save();
		canvas.clipRect(imageRect);
	}
	
	private void removeClipping(Canvas canvas)
	{
		canvas.restore();
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
		Canvas screenCanvas = new Canvas(screenBitmap);
		
		if(reversedLayers == null) return;
		for(Layer layer : reversedLayers)
		{
			boolean drawLayer = layer.isVisible() && !layer.isTemporaryHidden();
			boolean drawTool = image.isLayerSelected(layer) && tool.doesOnLayerDraw(layer.isVisible());
			
			LayerMode layerMode = layer.getMode();
			layerMode.startDrawing(screenBitmap, screenCanvas);
			
			if(drawLayer)
			{
				Matrix layerMatrix = new Matrix(image.getImageMatrix());
				layerMatrix.preTranslate(layer.getX(), layer.getY());
				
				layerMode.setRectClipping(imageRect);
				layerMode.addLayer(layerMatrix);
				layerMode.resetClipping();
			}
			if(drawTool) layerMode.addTool(createOnLayerToolBitmap(tool, layer));
			
			screenBitmap = layerMode.apply();
		}
		
		if(tool.doesOnTopDraw())
		{
			Bitmap toolBitmap = createOnTopToolBitmap(tool);
			screenCanvas.drawBitmap(toolBitmap, 0, 0, null);
		}
		
		canvas.drawBitmap(screenBitmap, 0, 0, null);
	}
	
	private Bitmap createOnLayerToolBitmap(Tool tool, Layer layer)
	{
		if(!tool.doesOnLayerDraw(layer.isVisible())) return null;
		
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		transformToolCanvas(canvas, tool.getOnLayerDrawingCoordinateSpace());
		tool.onLayerDraw(canvas);
		return bitmap;
	}
	
	private Bitmap createOnTopToolBitmap(Tool tool)
	{
		if(!tool.doesOnTopDraw()) return null;
		
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		transformToolCanvas(canvas, tool.getOnTopDrawingCoordinateSpace());
		tool.onTopDraw(canvas);
		return bitmap;
	}
	
	private void transformToolCanvas(Canvas canvas, ToolCoordinateSpace space)
	{
		switch(space)
		{
		case SCREEN_SPACE: break;
		case IMAGE_SPACE:
			canvas.scale(image.getZoom(), image.getZoom());
			canvas.translate(-image.getViewX(), -image.getViewY());
			break;
		case LAYER_SPACE:
			canvas.scale(image.getZoom(), image.getZoom());
			canvas.translate(-image.getViewX() + image.getSelectedLayerX(),
							 -image.getViewY() + image.getSelectedLayerY());
			break;
		}
	}
	
	private void drawLayerBounds(Canvas canvas)
	{
		if(image.getSelectedLayer() == null) return;
		canvas.drawPath(boundsPath, layerBoundsPaint);
	}
	
	private void drawSelection(Canvas canvas)
	{
		canvas.drawPath(limitedSelectionPath, selectionPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		Tool tool = getTool();
		if(image.getSelectedLayer() == null)
		{
			if(event.getAction() != MotionEvent.ACTION_DOWN) tool.onTouch(event, getContext());
			return false;
		}
		
		boolean result = tool.onTouch(event, getContext());
		if(result) invalidate();
		return result;
	}
	
	@Override
	public void onImageChanged()
	{
		if(image == null) return;
		updateLayerBounds();
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
	public void onImageMatrixChanged()
	{
		if(image == null) return;
		updateCheckerboardMatrix();
		updateLayerBounds();
		updateSelectionPath();
		updateImageRect();
		invalidate();
	}
	
	@Override
	public void onSelectionChanged()
	{
		createSelectionPath();
	}
	
	private void createSelectionPath()
	{
		Rect screen = new Rect();
		getScreenRect().round(screen);
		
		Region region = new Region(selection.getRegion());
		region.op(screen, Region.Op.INTERSECT);
		
		rawLimitedSelectionPath = region.getBoundaryPath();
		rawSelectionPath = selection.getPath();
		
		limitedSelectionPath = new Path();
		selectionPath = new Path();
		updateSelectionPath();
	}
	
	private void updateCheckerboardMatrix()
	{
		checkerboardMatrix = new Matrix();
		checkerboardMatrix.preTranslate(-image.getViewX() * image.getZoom() + CHECKERBOARD_OFFSET,
										-image.getViewY() * image.getZoom() + CHECKERBOARD_OFFSET);
	}
	
	private void updateLayerBounds()
	{
		Layer selected = image.getSelectedLayer();
		if(selected == null) return;
		
		RectF bounds = new RectF(selected.getBounds());
		bounds.intersect(getScreenRect());
		boundsPath = new Path();
		boundsPath.addRect(bounds, Path.Direction.CW);
		boundsPath.close();
		boundsPath.transform(image.getImageMatrix());
	}
	
	private void updateSelectionPath()
	{
		if(rawLimitedSelectionPath == null || rawSelectionPath == null) return;
		rawLimitedSelectionPath.transform(image.getImageMatrix(), limitedSelectionPath);
		rawSelectionPath.transform(image.getImageMatrix(), selectionPath);
	}
	
	private void updateImageRect()
	{
		image.setImageRect(imageRect);
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