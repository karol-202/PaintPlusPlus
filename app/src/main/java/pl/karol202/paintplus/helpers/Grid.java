package pl.karol202.paintplus.helpers;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import pl.karol202.paintplus.image.Image;

public class Grid
{
	private final int LINE_WIDTH = 1;
	private final int LINE_OFFSET_CONSTANT = 50;
	
	private Image image;
	private float density;
	private boolean enabled;
	
	private Paint paint;
	private int[] verticalLines;
	private int[] horizontalLines;
	private float lastZoom;
	private int lastX;
	private int lastY;
	
	Grid(Image image, Resources resources)
	{
		this.image = image;
		this.density = resources.getDisplayMetrics().density;
		this.enabled = true;
		
		this.paint = new Paint();
		this.paint.setStrokeWidth(LINE_WIDTH);
	}
	
	private void createLines(Canvas canvas)
	{
		int offset = (int) Math.floor(density * LINE_OFFSET_CONSTANT / lastZoom);
		
		int right = lastX + (int) (canvas.getWidth() / lastZoom);
		int bottom = lastY + (int) (canvas.getHeight() / lastZoom);
		
		int firstVerticalLineIndex = (int) Math.ceil(lastX / offset);
		int lastVerticalLineIndex = (int) Math.floor(right / offset);
		int firstHorizontalLineIndex = (int) Math.ceil(lastY / offset);
		int lastHorizontalLineIndex = (int) Math.ceil(bottom / offset);
		
		verticalLines = new int[lastVerticalLineIndex - firstVerticalLineIndex + 1];
		horizontalLines = new int[lastHorizontalLineIndex - firstHorizontalLineIndex + 1];
		
		for(int vertLine = firstVerticalLineIndex; vertLine <= lastVerticalLineIndex; vertLine++)
			verticalLines[vertLine - firstVerticalLineIndex] = vertLine * offset;
		for(int horLine = firstHorizontalLineIndex; horLine <= lastHorizontalLineIndex; horLine++)
			horizontalLines[horLine - firstHorizontalLineIndex] = horLine * offset;
	}
	
	public void onScreenDraw(Canvas canvas)
	{
		if(!enabled) return;
		if(checkForChanges()) createLines(canvas);
		drawLines(canvas);
	}
	
	private boolean checkForChanges()
	{
		boolean changes = image.getZoom() != lastZoom || image.getViewX() != lastX || image.getViewY() != lastY;
		if(changes)
		{
			lastZoom = image.getZoom();
			lastX = image.getViewX();
			lastY = image.getViewY();
		}
		return changes;
	}
	
	private void drawLines(Canvas canvas)
	{
		for(int imageX : verticalLines)
		{
			int canvasX = (int) ((imageX - lastX) * lastZoom);
			canvas.drawLine(canvasX, 0, canvasX, canvas.getHeight(), paint);
		}
		for(int imageY : horizontalLines)
		{
			int canvasY = (int) ((imageY - lastY) * lastZoom);
			canvas.drawLine(0, canvasY, canvas.getWidth(), canvasY, paint);
		}
	}
	
	void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
}