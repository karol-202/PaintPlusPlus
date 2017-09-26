package pl.karol202.paintplus.color.picker.panel;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import static pl.karol202.paintplus.color.picker.panel.ColorChannel.ColorChannelType.*;

public class ColorPickerSquarePanel extends View
{
	private ColorMode mode;
	private ColorChannel channelMain;
	private ColorChannel channelX;
	private ColorChannel channelY;
	
	private Shader shader;
	private Paint paint;
	
	public ColorPickerSquarePanel(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		paint = new Paint();
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		drawPanel(canvas);
	}
	
	private void drawPanel(Canvas canvas)
	{
		if(shader == null) updatePaint();
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
	}
	
	private void updatePaint()
	{
		paint.setShader(createComposeShader());
	}
	
	private Shader createComposeShader()
	{
		Shader shaderX = createXShader();
		Shader shaderY = createYShader();
		
		PorterDuff.Mode porterDuffMode = null;
		if(mode instanceof ColorModeRGB) porterDuffMode = PorterDuff.Mode.ADD;
		else if(mode instanceof ColorModeHSV) porterDuffMode = PorterDuff.Mode.SRC_ATOP;
		return new ComposeShader(shaderX, shaderY, porterDuffMode);
	}
	
	private Shader createXShader()
	{
		if(channelMain.getType() != SATURATION && channelMain.getType() != VALUE)
			return new LinearGradient(0, 0, 1, 0, getLeftColor(), getRightColor(), Shader.TileMode.CLAMP);
		else
		{
			int[] colors = new int[] { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED };
			return new LinearGradient(0, 0, 1, 0, colors, null, Shader.TileMode.CLAMP);
		}
	}
	
	private Shader createYShader()
	{
		return new LinearGradient(0, 0, 0, 1, getTopColor(), getBottomColor(), Shader.TileMode.CLAMP);
	}
	
	private int getLeftColor()
	{
		if(channelMain.getType() == RED) return Color.argb(255, channelMain.getValue(), 0, 0);
		else if(channelMain.getType() == GREEN) return Color.argb(255, 0, channelMain.getValue(), 0);
		else if(channelMain.getType() == BLUE) return Color.argb(255, 0, 0, channelMain.getValue());
		else if(channelMain.getType() == SATURATION) return Color.argb(255, 0, 0, 0);
		else if(channelMain.getType() == VALUE) return Color.argb(channelMain.getValue(), channelMain.getValue(), channelMain.getValue(), channelMain.getValue());
		else return Color.BLACK;
	}
	
	private int getRightColor()
	{
		if(channelMain.getType() == RED) return Color.argb(255, channelMain.getValue(), 0, 255);
		else if(channelMain.getType() == GREEN) return Color.argb(255, 0, channelMain.getValue(), 255);
		else if(channelMain.getType() == BLUE) return Color.argb(255, 0, 255, channelMain.getValue());
		else if(channelMain.getType() == SATURATION) return Color.argb(255 - channelMain.getValue(), 255, 255, 255);
		else if(channelMain.getType() == VALUE) return Color.argb(255 - channelMain.getValue(), 0, 0, 0);
		else return Color.BLACK;
	}
	
	private int getBottomColor()
	{
		if(channelMain.getType() == RED) return Color.rgb(channelMain.getValue(), 0, 0);
		else if(channelMain.getType() == GREEN) return Color.rgb(0, channelMain.getValue(), 0);
		else if(channelMain.getType() == BLUE) return Color.rgb(0, 0, channelMain.getValue());
		else if(channelMain.getType() == HUE) return Color.rgb(255, 255, 255);
		else return Color.BLACK;
	}
	
	private int getTopColor()
	{
		if(channelMain.getType() == RED) return Color.rgb(channelMain.getValue(), 255, 0);
		else if(channelMain.getType() == GREEN) return Color.rgb(255, channelMain.getValue(), 0);
		else if(channelMain.getType() == BLUE) return Color.rgb(255, 0, channelMain.getValue());
		else if(channelMain.getType() == HUE) return Color.HSVToColor(new float[] { channelMain.getValue(), 100, 100 });
		else return Color.BLACK;
	}
	
	public void setModeAndMainChannel(ColorMode mode, ColorChannel channelMain)
	{
		if(channelMain.getMode() != mode) throw new IllegalArgumentException("Main channel must be channel of mode.");
		this.mode = mode;
		this.channelMain = channelMain;
		
		ChannelXYSet set = mode.getChannelXYSetForMainChannel(channelMain);
		channelX = set.getChannelX();
		channelY = set.getChannelY();
	}
}