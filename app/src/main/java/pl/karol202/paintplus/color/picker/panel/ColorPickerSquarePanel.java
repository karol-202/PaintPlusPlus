/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package pl.karol202.paintplus.color.picker.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.content.ContextCompat;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.util.Utils;

import static pl.karol202.paintplus.color.picker.panel.ColorChannel.ColorChannelType.*;

public class ColorPickerSquarePanel extends View
{
	interface OnColorPanelUpdateListener
	{
		void onChannelsValueChanged();
	}

	// TODO Repetition of code in ColorPickerBar. Move to another class.
	private static final int[] HUE_COLORS = new int[] { Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED };

	private static final float LEFT_MARGIN_DP = 10;
	private static final float TOP_MARGIN_DP = 10;
	private static final float RIGHT_MARGIN_DP = 10;
	private static final float BOTTOM_MARGIN_DP = 10;
	private static final float INDICATOR_RING_RADIUS_DP = 6;
	private static final float INDICATOR_RING_THICKNESS_DP = 2;
	private static final float BORDER_WIDTH_DP = 1;

	private final float LEFT_MARGIN_PX;
	private final float TOP_MARGIN_PX;
	private final float RIGHT_MARGIN_PX;
	private final float BOTTOM_MARGIN_PX;
	private final float INDICATOR_RING_RADIUS_PX;
	private final float INDICATOR_RING_THICKNESS_PX;
	private final float BORDER_WIDTH_PX;

	private OnColorPanelUpdateListener listener;
	private ColorMode mode;
	private ColorChannel channelMain;
	private ColorChannel channelX;
	private ColorChannel channelY;

	private Paint borderPaint;

	private Shader panelShader;
	private Paint panelPaint;

	private Paint indicatorPaint;

	public ColorPickerSquarePanel(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		checkForEditMode();

		LEFT_MARGIN_PX = Utils.dpToPixels(context, LEFT_MARGIN_DP);
		TOP_MARGIN_PX = Utils.dpToPixels(context, TOP_MARGIN_DP);
		RIGHT_MARGIN_PX = Utils.dpToPixels(context, RIGHT_MARGIN_DP);
		BOTTOM_MARGIN_PX = Utils.dpToPixels(context, BOTTOM_MARGIN_DP);
		INDICATOR_RING_RADIUS_PX = Utils.dpToPixels(context, INDICATOR_RING_RADIUS_DP);
		INDICATOR_RING_THICKNESS_PX = Utils.dpToPixels(context, INDICATOR_RING_THICKNESS_DP);
		BORDER_WIDTH_PX = Utils.dpToPixels(context, BORDER_WIDTH_DP);

		borderPaint = new Paint();
		borderPaint.setColor(ContextCompat.getColor(context, R.color.border));
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(BORDER_WIDTH_PX);

		panelPaint = new Paint();

		indicatorPaint = new Paint();
		indicatorPaint.setAntiAlias(true);
		indicatorPaint.setColor(Color.DKGRAY);
		indicatorPaint.setStyle(Paint.Style.STROKE);
		indicatorPaint.setStrokeWidth(INDICATOR_RING_THICKNESS_PX);
	}

	private void checkForEditMode()
	{
		if(!isInEditMode()) return;
		ColorMode mode = new ColorModeHSV();
		setModeAndMainChannel(mode, mode.getChannels()[0]);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if(mode == null) return;
		drawBorder(canvas);
		drawPanel(canvas);
		drawIndicator(canvas);
	}

	private void drawBorder(Canvas canvas)
	{
		canvas.drawRect(LEFT_MARGIN_PX - (float) Math.floor(BORDER_WIDTH_PX / 2),
						TOP_MARGIN_PX - (float) Math.floor(BORDER_WIDTH_PX / 2),
						getWidth() - RIGHT_MARGIN_PX + (float) Math.floor(BORDER_WIDTH_PX / 2),
						getHeight() - BOTTOM_MARGIN_PX + (float) Math.floor(BORDER_WIDTH_PX / 2), borderPaint);
	}

	private void drawPanel(Canvas canvas)
	{
		if(panelShader == null) updatePaint();
		canvas.drawRect(LEFT_MARGIN_PX, TOP_MARGIN_PX, getWidth() - RIGHT_MARGIN_PX, getHeight() - BOTTOM_MARGIN_PX, panelPaint);
	}

	private void updatePaint()
	{
		panelShader = createComposeShader();
		panelPaint.setShader(panelShader);
	}

	private Shader createComposeShader()
	{
		Shader shaderX = createXShader();
		Shader shaderY = createYShader();

		PorterDuff.Mode porterDuffMode = null;
		if(mode instanceof ColorModeRGB) porterDuffMode = PorterDuff.Mode.ADD;
		else if(mode instanceof ColorModeHSV) porterDuffMode = PorterDuff.Mode.DST_OVER;
		return new ComposeShader(shaderX, shaderY, porterDuffMode);
	}

	private Shader createXShader()
	{
		return new LinearGradient(LEFT_MARGIN_PX, 0, getWidth() - RIGHT_MARGIN_PX, 0, getLeftColor(), getRightColor(), Shader.TileMode.CLAMP);
	}

	private Shader createYShader()
	{
		if(channelMain.getType() == SATURATION || channelMain.getType() == VALUE)
			return new LinearGradient(0, TOP_MARGIN_PX, 0, getHeight() - BOTTOM_MARGIN_PX, HUE_COLORS, null, Shader.TileMode.CLAMP);
		else
			return new LinearGradient(0, TOP_MARGIN_PX, 0, getHeight() - BOTTOM_MARGIN_PX, getTopColor(), getBottomColor(), Shader.TileMode.CLAMP);
	}

	private int getLeftColor()
	{
		if(channelMain.getType() == RED) return getARGBColor(1, getMainChannelValue(), 0, 0);
		else if(channelMain.getType() == GREEN) return getARGBColor(1, 0, getMainChannelValue(), 0);
		else if(channelMain.getType() == BLUE) return getARGBColor(1, 0, 0, getMainChannelValue());
		else if(channelMain.getType() == HUE) return getARGBColor(1, 0, 0, 0);
		else if(channelMain.getType() == SATURATION) return getARGBColor(1, 0, 0, 0);
		else if(channelMain.getType() == VALUE) return getARGBColor(1, getMainChannelValue(),
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
		if(channelMain.getType() == RED) return getRGBColor(0, 0, 0);
		else if(channelMain.getType() == GREEN) return getRGBColor(0, 0, 0);
		else if(channelMain.getType() == BLUE) return getRGBColor(0, 0, 0);
		else if(channelMain.getType() == HUE) return getRGBColor(1, 1, 1);
		else return Color.BLACK;
	}

	private int getTopColor()
	{
		if(channelMain.getType() == RED) return getRGBColor(0, 1, 0);
		else if(channelMain.getType() == GREEN) return getRGBColor(1, 0, 0);
		else if(channelMain.getType() == BLUE) return getRGBColor(1, 0, 0);
		else if(channelMain.getType() == HUE) return Color.HSVToColor(new float[] { channelMain.getValue(), 1, 1 });
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

	private void drawIndicator(Canvas canvas)
	{
		float x = Utils.map(channelX.getValue(), 0, channelX.getMaxValue(), LEFT_MARGIN_PX, getWidth() - RIGHT_MARGIN_PX);
		float y = Utils.map(channelY.getValue(), channelY.getMaxValue(), 0, TOP_MARGIN_PX, getHeight() - BOTTOM_MARGIN_PX);

		canvas.drawCircle(x, y, INDICATOR_RING_RADIUS_PX, indicatorPaint);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN) getParent().requestDisallowInterceptTouchEvent(true);
		else if(event.getAction() == MotionEvent.ACTION_UP) getParent().requestDisallowInterceptTouchEvent(false);
		float x = Utils.map(event.getX(), LEFT_MARGIN_PX, getWidth() - RIGHT_MARGIN_PX, 0, channelX.getMaxValue());
		float y = Utils.map(event.getY(), TOP_MARGIN_PX, getHeight() - BOTTOM_MARGIN_PX, channelY.getMaxValue(), 0);
		x = Utils.clamp(x, 0, channelX.getMaxValue());
		y = Utils.clamp(y, 0, channelY.getMaxValue());
		channelX.setValue(Math.round(x));
		channelY.setValue(Math.round(y));
		if(listener != null) listener.onChannelsValueChanged();

		invalidate();
		return true;
	}

	void setModeAndMainChannel(ColorMode mode, ColorChannel channelMain)
	{
		if(channelMain.getMode() != mode) throw new IllegalArgumentException("Main channel must be channel of mode.");
		this.mode = mode;
		this.channelMain = channelMain;

		ChannelXYSet set = mode.getChannelXYSetForMainChannel(channelMain);
		channelX = set.getChannelX();
		channelY = set.getChannelY();

		panelShader = null;
		invalidate();
	}

	void setOnColorPanelUpdateListener(OnColorPanelUpdateListener listener)
	{
		this.listener = listener;
	}

	void update()
	{
		panelShader = null;
		invalidate();
	}
}
