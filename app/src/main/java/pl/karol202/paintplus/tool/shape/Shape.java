package pl.karol202.paintplus.tool.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.Image;

public abstract class Shape
{
	private static final int MAX_TOUCH_DISTANCE_DP = 25;
	
	private static final float TRANSLUCENT_SHAPE_OPACITY = 0.5f;
	
	private boolean smooth;
	private float opacity;
	
	private Image image;
	private OnShapeEditListener shapeEditListener;
	private boolean editMode;
	private Paint paint;
	private ColorsSet colors;
	private HelpersManager helpersManager;
	
	public Shape(Image image, OnShapeEditListener shapeEditListener)
	{
		this.smooth = true;
		this.opacity = 1;
		
		this.image = image;
		this.shapeEditListener = shapeEditListener;
		this.paint = new Paint();
		this.colors = image.getColorsSet();
		this.helpersManager = image.getHelpersManager();
	}
	
	public abstract int getName();
	
	public abstract int getIcon();
	
	public abstract Class<? extends ShapeProperties> getPropertiesClass();
	
	public abstract void onTouchStart(int x, int y);
	
	public abstract void onTouchMove(int x, int y);
	
	public abstract void onTouchStop(int x, int y);
	
	public abstract void expandDirtyRect(Rect dirtyRect);
	
	public abstract void onScreenDraw(Canvas canvas, boolean translucent);
	
	public abstract void apply(Canvas imageCanvas);
	
	public abstract void cancel();
	
	protected float calcDistance(Point point, int x, int y)
	{
		return (float) Math.hypot(point.x - x, point.y - y);
	}
	
	protected void update()
	{
		updateColor(false);
		paint.setAntiAlias(smooth);
		image.updateImage();
	}
	
	protected void updateColor(boolean translucent)
	{
		paint.setColor(colors.getFirstColor());
		if(translucent) paint.setAlpha((int) (opacity * 255 * TRANSLUCENT_SHAPE_OPACITY));
		else paint.setAlpha((int) (opacity * 255));
	}
	
	protected void cleanUp()
	{
		editMode = false;
		image.updateImage();
	}
	
	protected float getMaxTouchDistance()
	{
		return MAX_TOUCH_DISTANCE_DP * image.SCREEN_DENSITY / image.getZoom();
	}
	
	protected Image getImage()
	{
		return image;
	}
	
	protected boolean isInEditMode()
	{
		return editMode;
	}
	
	protected void enableEditMode()
	{
		editMode = true;
		shapeEditListener.onStartShapeEditing();
	}
	
	protected Paint getPaint()
	{
		return paint;
	}
	
	protected HelpersManager getHelpersManager()
	{
		return helpersManager;
	}
	
	boolean isSmooth()
	{
		return smooth;
	}
	
	void setSmooth(boolean smooth)
	{
		this.smooth = smooth;
		update();
	}
	
	protected float getOpacity()
	{
		return opacity;
	}
	
	protected void setOpacity(float opacity)
	{
		this.opacity = opacity;
		update();
	}
}