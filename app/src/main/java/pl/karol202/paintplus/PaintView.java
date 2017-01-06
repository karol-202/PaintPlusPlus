package pl.karol202.paintplus;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Image.OnImageChangeListener;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.image.Layer;
import pl.karol202.paintplus.tool.Tools;

import java.util.ArrayList;
import java.util.Collections;

public class PaintView extends SurfaceView implements OnImageChangeListener
{
	private final float[] SELECTION_PAINT_DASH = new float[] { 5f, 5f };
	
	private ActivityPaint activity;
	private Image image;
	private Tools tools;
	private ColorsSet colors;
	private Paint bitmapPaint;
	private Paint selectionPaint;
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
		
		tools = activity.getTools();
		
		colors = image.getColorsSet();
		
		bitmapPaint = new Paint();
		bitmapPaint.setFilterBitmap(false);
		
		selectionPaint = new Paint();
		selectionPaint.setStyle(Paint.Style.STROKE);
		selectionPaint.setStrokeWidth(2f);
		selectionPaint.setPathEffect(new DashPathEffect(SELECTION_PAINT_DASH, 0));
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
		
		drawImage(canvas);
		drawSelection(canvas);
	}
	
	private void drawImage(Canvas canvas)
	{
		ArrayList<Layer> layers = new ArrayList<>(image.getLayers());
		Collections.reverse(layers);
		for(Layer layer : layers)
		{
			if(!layer.isVisible()) continue;
			Matrix matrix = new Matrix(image.getImageMatrix());
			matrix.preTranslate(layer.getX(), layer.getY());
			canvas.drawBitmap(layer.getBitmap(), matrix, bitmapPaint);
			
			if(image.isLayerSelected(layer)) drawToolBitmap(canvas);
		}
	}
	
	private void drawSelection(Canvas canvas)
	{
		Path selectionPath = new Path(image.getSelection().getPath());
		selectionPath.transform(image.getImageMatrix());
		canvas.drawPath(selectionPath, selectionPaint);
	}
	
	private void drawToolBitmap(Canvas canvas)
	{
		Bitmap toolBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas toolCanvas = new Canvas(toolBitmap);
		activity.getTool().onScreenDraw(toolCanvas);
		canvas.drawBitmap(toolBitmap, 0, 0, null);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float x = (event.getX() / image.getZoom()) + image.getViewX();
		float y = (event.getY() / image.getZoom()) + image.getViewY();
		event.setLocation(x, y);
		
		boolean result = activity.getTool().onTouch(event);
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
}