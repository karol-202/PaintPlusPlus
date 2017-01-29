package pl.karol202.paintplus.tool.pickcolor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Layer;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.selection.Selection;

public class ToolColorPick extends Tool
{
	private int size;
	
	private Bitmap bitmap;
	private ColorsSet colors;
	private Selection selection;
	
	public ToolColorPick(Image image)
	{
		super(image);
		this.size = 1;

		this.colors = image.getColorsSet();
		this.selection = image.getSelection();
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_color_pick;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_color_pick_black_24dp;
	}
	
	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return ColorPickProperties.class;
	}
	
	@Override
	public boolean isLayerSpace()
	{
		return true;
	}
	
	@Override
	public boolean isImageLimited()
	{
		return true;
	}
	
	@Override
	public boolean onTouch(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_UP) pickColor((int) event.getX(), (int) event.getY());
		return true;
	}
	
	private void pickColor(int x, int y)
	{
		bitmap = image.getSelectedBitmap();
		if(x < 0 || y < 0 || x >= bitmap.getWidth() || y >= bitmap.getHeight()) return;
		
		if(bitmap == null) colors.setFirstColor(Color.BLACK);
		else if(size == 1) pickPixelColor(x, y);
		else if(size > 1) pickAverageColor(x, y);
	}
	
	private void pickPixelColor(int x, int y)
	{
		if(!checkSelection(x, y)) return;
		int color = bitmap.getPixel(x, y);
		colors.setFirstColor(color);
	}
	
	private void pickAverageColor(int centerX, int centerY)
	{
		int pixels = 0;
		long redSum = 0;
		long greenSum = 0;
		long blueSum = 0;
		
		int regionStartX = centerX - (int) Math.floor((size - 1) / 2);
		int regionStartY = centerY - (int) Math.floor((size - 1) / 2);
		int regionEndX = centerX + (int) Math.floor(size / 2);
		int regionEndY = centerY + (int) Math.floor(size / 2);
		for(int x = regionStartX; x <= regionEndX; x++)
		{
			for(int y = regionStartY; y <= regionEndY; y++)
			{
				if(!checkSelection(x, y)) continue;
				int color = bitmap.getPixel(x, y);
				pixels++;
				redSum += Math.pow(Color.red(color), 2);
				greenSum += Math.pow(Color.green(color), 2);
				blueSum += Math.pow(Color.blue(color), 2);
			}
		}
		
		int red = (int) Math.round(Math.sqrt(redSum / (double) pixels));
		int green = (int) Math.round(Math.sqrt(greenSum / (double) pixels));
		int blue = (int) Math.round(Math.sqrt(blueSum / (double) pixels));
		colors.setFirstColor(Color.rgb(red, green, blue));
	}
	
	private boolean checkSelection(int x, int y)
	{
		return selection.isEmpty() || selection.containsPoint(x + image.getSelectedLayerX(), y + image.getSelectedLayerY());
	}
	
	@Override
	public boolean doesScreenDraw(Layer layer)
	{
		return false;
	}
	
	@Override
	public void onScreenDraw(Canvas canvas) { }
	
	public int getSize()
	{
		return size;
	}
	
	public void setSize(int size)
	{
		this.size = size;
	}
}