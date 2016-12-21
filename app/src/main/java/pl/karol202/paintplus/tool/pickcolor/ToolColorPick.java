package pl.karol202.paintplus.tool.pickcolor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.selection.Selection;

public class ToolColorPick extends Tool
{
	private Bitmap bitmap;
	private ColorsSet colors;
	private Selection selection;
	
	public ToolColorPick(Image image)
	{
		super(image);
		this.bitmap = image.getBitmap();
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
	public boolean onTouch(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_UP) pickColor((int) event.getX(), (int) event.getY());
		return true;
	}
	
	private void pickColor(int x, int y)
	{
		if(!selection.isEmpty() && !selection.containsPoint(x, y)) return;
		int color = bitmap.getPixel(x, y);
		colors.setFirstColor(color);
	}
	
	@Override
	public void onScreenDraw(Canvas canvas) { }
}