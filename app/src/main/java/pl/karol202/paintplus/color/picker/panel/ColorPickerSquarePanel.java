package pl.karol202.paintplus.color.picker.panel;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import static pl.karol202.paintplus.color.picker.panel.ColorChannel.ColorChannelType.*;

public class ColorPickerSquarePanel extends View
{
	private static final int[] HUE_COLORS = new int[] { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED };
	
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
		if(mode == null) return;
		drawPanel(canvas);
	}
	
	private void drawPanel(Canvas canvas)
	{
		if(shader == null) updatePaint();
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
	}
	
	private void updatePaint()
	{
		shader = createComposeShader();
		paint.setShader(shader);
	}
	
	private Shader createComposeShader()
	{
		Shader shaderX = createXShader();
		Shader shaderY = createYShader();
		
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		PorterDuff.Mode porterDuffMode = null;
		if(mode instanceof ColorModeRGB) porterDuffMode = PorterDuff.Mode.ADD;
		else if(mode instanceof ColorModeHSV) porterDuffMode = PorterDuff.Mode.DST_OVER;
		return new ComposeShader(shaderX, shaderY, porterDuffMode);
	}
	
	private Shader createXShader()
	{
		return new LinearGradient(0, 0, getWidth(), 0, getLeftColor(), getRightColor(), Shader.TileMode.CLAMP);
	}
	
	private Shader createYShader()
	{
		if(channelMain.getType() == SATURATION || channelMain.getType() == VALUE)
			return new LinearGradient(0, 0, 0, getHeight(), HUE_COLORS, null, Shader.TileMode.CLAMP);
		else
			return new LinearGradient(0, 0, 0, getHeight(), getTopColor(), getBottomColor(), Shader.TileMode.CLAMP);
	}
	
	private int getLeftColor()
	{
		if(channelMain.getType() == RED) return getARGBColor(1, getMainChannelValue(), 0, 0);
		else if(channelMain.getType() == GREEN) return getARGBColor(1, 0, getMainChannelValue(), 0);
		else if(channelMain.getType() == BLUE) return getARGBColor(1, 0, 0, getMainChannelValue());
		else if(channelMain.getType() == HUE) return getARGBColor(1, 0, 0, 0);
		else if(channelMain.getType() == SATURATION) return getARGBColor(1, 0, 0, 0);
		else if(channelMain.getType() == VALUE) return getARGBColor(1 - getMainChannelValue(), getMainChannelValue(),
																	getMainChannelValue(), getMainChannelValue());
		else return Color.BLACK;
	}
	
	private int getRightColor()
	{
		if(channelMain.getType() == RED) return getARGBColor(1, getMainChannelValue(), 0, 1);
		else if(channelMain.getType() == GREEN) return getARGBColor(1, 0, getMainChannelValue(), 1);
		else if(channelMain.getType() == BLUE) return getARGBColor(1, 0, 1, getMainChannelValue());
		else if(channelMain.getType() == HUE) return getARGBColor(0, 0, 0, 0);
		else if(channelMain.getType() == SATURATION) return getARGBColor(1 - getMainChannelValue(), 1, 1, 1);
		else if(channelMain.getType() == VALUE) return getARGBColor(1 - getMainChannelValue(), 0, 0, 0);
		else return Color.BLACK;
	}
	
	private int getBottomColor()
	{
		if(channelMain.getType() == RED) return getRGBColor(getMainChannelValue(), 0, 0);
		else if(channelMain.getType() == GREEN) return getRGBColor(0, getMainChannelValue(), 0);
		else if(channelMain.getType() == BLUE) return getRGBColor(0, 0, getMainChannelValue());
		else if(channelMain.getType() == HUE) return getRGBColor(1, 1, 1);
		else return Color.BLACK;
	}
	
	private int getTopColor()
	{
		if(channelMain.getType() == RED) return getRGBColor(getMainChannelValue(), 1, 0);
		else if(channelMain.getType() == GREEN) return getRGBColor(1, getMainChannelValue(), 0);
		else if(channelMain.getType() == BLUE) return getRGBColor(1, 0, getMainChannelValue());
		else if(channelMain.getType() == HUE) return Color.HSVToColor(new float[] { getMainChannelValue(), 1, 1 });
		else return Color.BLACK;
	}
	
	private int getRGBColor(float r, float g, float b)
	{
		return getARGBColor(1, r, g, b);
	}
	
	private int getARGBColor(float a, float r, float g, float b)
	{
		return Color.argb(Math.round(a * 255), Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
	}
	
	private float getMainChannelValue()
	{
		return channelMain.getValue() / (float) channelMain.getMaxValue();
	}
	
	void setModeAndMainChannel(ColorMode mode, ColorChannel channelMain)
	{
		if(channelMain.getMode() != mode) throw new IllegalArgumentException("Main channel must be channel of mode.");
		this.mode = mode;
		this.channelMain = channelMain;
		
		ChannelXYSet set = mode.getChannelXYSetForMainChannel(channelMain);
		channelX = set.getChannelX();
		channelY = set.getChannelY();
		
		shader = null;
		invalidate();
	}
}