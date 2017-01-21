package pl.karol202.paintplus;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Image.OnImageChangeListener;
import pl.karol202.paintplus.image.Layer;
import pl.karol202.paintplus.tool.Tool;

import java.util.ArrayList;
import java.util.Collections;

public class PaintView extends SurfaceView implements OnImageChangeListener
{
	private final float[] SELECTION_PAINT_DASH = new float[] { 5f, 5f };
	
	private ActivityPaint activity;
	private Image image;
	private ColorsSet colors;
	private Paint bitmapPaint;
	private Paint selectionPaint;
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
		bitmapPaint.setFilterBitmap(false);
		
		selectionPaint = new Paint();
		selectionPaint.setStyle(Paint.Style.STROKE);
		selectionPaint.setStrokeWidth(2f);
		selectionPaint.setPathEffect(new DashPathEffect(SELECTION_PAINT_DASH, 0));
		
		Bitmap checkerboard = BitmapFactory.decodeResource(activity.getResources(), R.drawable.checkerboard);
		checkerboardShader = new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		checkerboardPaint = new Paint();
		checkerboardPaint.setShader(checkerboardShader);
		checkerboardPaint.setFilterBitmap(false);
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
		
		drawCheckerboard(canvas);
		drawImage(canvas);
		drawSelection(canvas);
	}
	
	private void drawCheckerboard(Canvas canvas)
	{
		float viewX = -image.getViewX() * image.getZoom();
		float viewY = -image.getViewY() * image.getZoom();
		float width = image.getWidth() * image.getZoom();
		float height = image.getHeight() * image.getZoom();
		
		Matrix matrix = new Matrix();
		matrix.preTranslate(viewX, viewY);
		checkerboardShader.setLocalMatrix(matrix);
		canvas.drawRect(viewX, viewY, viewX + width, viewY + height, checkerboardPaint);
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
		getTool().onScreenDraw(toolCanvas);
		canvas.drawBitmap(toolBitmap, 0, 0, null);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
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