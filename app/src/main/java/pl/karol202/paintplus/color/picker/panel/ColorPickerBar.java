package pl.karol202.paintplus.color.picker.panel;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class ColorPickerBar extends View
{
	private static final int[] HUE_COLORS = new int[] { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED };
	
	private ColorChannel channel;
	
	private Paint paint;
	private Shader shader;
	
	public ColorPickerBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		paint = new Paint();
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(channel == null) return;
		drawBar(canvas);
	}
	
	private void updatePaint()
	{
		int topColor = Color.WHITE;
		switch(channel.getType())
		{
		case RED: topColor = Color.RED; break;
		case GREEN: topColor = Color.GREEN; break;
		case BLUE: topColor = Color.BLUE; break;
		}
		
		if(channel.getType() == ColorChannel.ColorChannelType.HUE)
			shader = new LinearGradient(0, 0, 0, getHeight(), HUE_COLORS, null, Shader.TileMode.CLAMP);
		else shader = new LinearGradient(0, 0, 0, getHeight(), topColor, Color.BLACK, Shader.TileMode.CLAMP);
		
		paint.setShader(shader);
	}
	
	private void drawBar(Canvas canvas)
	{
		if(shader == null) updatePaint();
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
	}
	
	void setChannel(ColorChannel channel)
	{
		this.channel = channel;
		this.shader = null;
		invalidate();
	}
}