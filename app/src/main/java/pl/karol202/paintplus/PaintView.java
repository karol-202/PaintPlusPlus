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
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Image.OnImageChangeListener;
import pl.karol202.paintplus.image.Layer;
import pl.karol202.paintplus.settings.ActivitySettings;
import pl.karol202.paintplus.tool.Tool;

import java.util.ArrayList;
import java.util.Collections;

public class PaintView extends SurfaceView implements OnImageChangeListener
{
	private final float[] PAINT_DASH = new float[] { 5f, 5f };
	
	private ActivityPaint activity;
	private Image image;
	private ColorsSet colors;
	private Paint bitmapPaint;
	private Paint selectionPaint;
	private Paint layerBoundsPaint;
	private Paint checkerboardPaint;
	private Shader checkerboardShader;
	private boolean initialized;

	public PaintView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public void init(ActivityPaint activity)
	{
		this.activity = activity;
		
		image = activity.getImage();
		image.setOnImageChangeListener(this);
		colors = image.getColorsSet();
		
		bitmapPaint = new Paint();
		
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
		
		bitmapPaint.setFilterBitmap(smooth);
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
	
	private void initImage()
	{
		image.setViewportWidth(getWidth());
		image.setViewportHeight(getHeight());
		image.centerView();
		initialized = true;
	}
	
	private void drawCheckerboard(Canvas canvas)
	{
		float viewX = -image.getViewX() * image.getZoom();
		float viewY = -image.getViewY() * image.getZoom();
		
		Matrix matrix = new Matrix();
		matrix.preTranslate(viewX, viewY);
		checkerboardShader.setLocalMatrix(matrix);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), checkerboardPaint);
	}
	
	private void drawImage(Canvas canvas)
	{
		Tool tool = getTool();
		ArrayList<Layer> layers = new ArrayList<>(image.getLayers());
		Collections.reverse(layers);
		for(Layer layer : layers)
		{
			if(layer.isVisible())
			{
				Matrix matrix = new Matrix(image.getImageMatrix());
				matrix.preTranslate(layer.getX(), layer.getY());
				canvas.drawBitmap(layer.getBitmap(), matrix, bitmapPaint);
			}
			if(image.isLayerSelected(layer) && tool.doesScreenDraw(layer))
			{
				if(!tool.isImageLimited()) removeClipping(canvas);
				drawToolBitmap(canvas);
				if(!tool.isImageLimited()) setClipping(canvas);
			}
		}
	}
	
	private void drawToolBitmap(Canvas canvas)
	{
		Bitmap toolBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas toolCanvas = new Canvas(toolBitmap);
		getTool().onScreenDraw(toolCanvas);
		canvas.drawBitmap(toolBitmap, 0, 0, bitmapPaint);
	}
	
	private void drawLayerBounds(Canvas canvas)
	{
		Layer selected = image.getSelectedLayer();
		if(selected == null) return;
		
		RectF bounds = selected.getBounds();
		RectF screen = new RectF(image.getViewX() - 2,
								 image.getViewY() - 2,
						        image.getViewX() + (getWidth() / image.getZoom()) + 2,
							  image.getViewY() + (getHeight() / image.getZoom()) + 2);
		bounds.intersect(screen);
		Path boundsPath = new Path();
		boundsPath.addRect(bounds, Path.Direction.CW);
		boundsPath.transform(image.getImageMatrix());
		canvas.drawPath(boundsPath, layerBoundsPaint);
	}
	
	private void drawSelection(Canvas canvas)
	{
		Path selectionPath = new Path(image.getSelection().getPath());
		selectionPath.transform(image.getImageMatrix());
		canvas.drawPath(selectionPath, selectionPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(image.getSelectedLayer() == null) return false;
		float x = (event.getX() / image.getZoom()) + image.getViewX();
		float y = (event.getY() / image.getZoom()) + image.getViewY();
		if(getTool().isLayerSpace())
		{
			x -= image.getSelectedLayerX();
			y -= image.getSelectedLayerY();
		}
		event.setLocation(x, y);
		
		boolean result = getTool().onTouch(event);
		invalidate();
		return result;
	}
	
	@Override
	public void onImageChanged()
	{
		invalidate();
	}
	
	@Override
	public void onLayersChanged()
	{
		activity.updateLayersPreview();
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
}