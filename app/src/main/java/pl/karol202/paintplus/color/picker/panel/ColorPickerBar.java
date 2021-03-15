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
import pl.karol202.paintplus.util.MathUtils;

public class ColorPickerBar extends View
{
	interface OnColorBarUpdateListener
	{
		void onChannelValueChanged();
	}

	private static final int[] HUE_COLORS = new int[] { Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED };

	private static final float LEFT_MARGIN_DP = 12;
	private static final float TOP_MARGIN_DP = 10;
	private static final float RIGHT_MARGIN_DP = 1;
	private static final float BOTTOM_MARGIN_DP = 10;
	private static final float INDICATOR_LINE_WIDTH_DP = 2;
	private static final float BORDER_WIDTH_DP = 1;
	private static final Path TRIANGLE_OUTER_PATH = new Path();
	private static final Path TRIANGLE_INNER_PATH = new Path();

	private final float LEFT_MARGIN_PX;
	private final float TOP_MARGIN_PX;
	private final float RIGHT_MARGIN_PX;
	private final float BOTTOM_MARGIN_PX;
	private final float INDICATOR_LINE_WIDTH_PX;
	private final float BORDER_WIDTH_PX;

	private OnColorBarUpdateListener listener;
	private ColorChannel channel;

	private Paint borderPaint;

	private Paint barPaint;
	private Shader barShader;

	private Path indicatorOuterPath;
	private Path indicatorInnerPath;
	private Paint indicatorOuterPaint;
	private Paint indicatorInnerPaint;
	private Paint indicatorLinePaint;

	public ColorPickerBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		checkForEditMode();

		LEFT_MARGIN_PX = MathUtils.dpToPixels(context, LEFT_MARGIN_DP);
		TOP_MARGIN_PX = MathUtils.dpToPixels(context, TOP_MARGIN_DP);
		RIGHT_MARGIN_PX = MathUtils.dpToPixels(context, RIGHT_MARGIN_DP);
		BOTTOM_MARGIN_PX = MathUtils.dpToPixels(context, BOTTOM_MARGIN_DP);
		INDICATOR_LINE_WIDTH_PX = MathUtils.dpToPixels(context, INDICATOR_LINE_WIDTH_DP);
		BORDER_WIDTH_PX = MathUtils.dpToPixels(context, BORDER_WIDTH_DP);
		initTriangles(context);

		borderPaint = new Paint();
		borderPaint.setColor(ContextCompat.getColor(context, R.color.border));
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(BORDER_WIDTH_PX);

		barPaint = new Paint();

		indicatorOuterPaint = new Paint();
		indicatorOuterPaint.setAntiAlias(true);
		indicatorOuterPaint.setColor(Color.DKGRAY);

		indicatorInnerPaint = new Paint();
		indicatorInnerPaint.setAntiAlias(true);
		indicatorInnerPaint.setColor(Color.WHITE);

		indicatorLinePaint = new Paint();
		indicatorLinePaint.setStrokeWidth(INDICATOR_LINE_WIDTH_PX);
		indicatorLinePaint.setColor(Color.DKGRAY);
	}

	private void checkForEditMode()
	{
		setChannel(new ColorModeHSV().getChannels()[0]);
	}

	private void initTriangles(Context context)
	{
		// TODO Paths to xml
		TRIANGLE_OUTER_PATH.moveTo(0, -8);
		TRIANGLE_OUTER_PATH.lineTo(16, 0);
		TRIANGLE_OUTER_PATH.lineTo(0, 8);
		TRIANGLE_OUTER_PATH.close();

		TRIANGLE_INNER_PATH.moveTo(2, -5);
		TRIANGLE_INNER_PATH.lineTo(13, 0);
		TRIANGLE_INNER_PATH.lineTo(2, 5);
		TRIANGLE_INNER_PATH.close();

		float scale = context.getResources().getDisplayMetrics().density;
		Matrix matrix = new Matrix();
		matrix.preScale(scale, scale);
		TRIANGLE_OUTER_PATH.transform(matrix);
		TRIANGLE_INNER_PATH.transform(matrix);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(channel == null) return;
		drawBorder(canvas);
		drawBar(canvas);
		drawIndicator(canvas);
	}

	private void drawBorder(Canvas canvas)
	{
		canvas.drawRect(LEFT_MARGIN_PX - (float) Math.floor(BORDER_WIDTH_PX / 2),
						TOP_MARGIN_PX - (float) Math.floor(BORDER_WIDTH_PX / 2),
						getWidth() - RIGHT_MARGIN_PX + (float) Math.floor(BORDER_WIDTH_PX / 2),
						getHeight() - BOTTOM_MARGIN_PX + (float) Math.floor(BORDER_WIDTH_PX / 2), borderPaint);
	}

	private void drawBar(Canvas canvas)
	{
		if(barShader == null) updatePaint();
		canvas.drawRect(LEFT_MARGIN_PX, TOP_MARGIN_PX, getWidth() - RIGHT_MARGIN_PX, getHeight() - BOTTOM_MARGIN_PX, barPaint);
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
			barShader = new LinearGradient(0, TOP_MARGIN_PX, 0, getHeight() - BOTTOM_MARGIN_PX, HUE_COLORS, null, Shader.TileMode.CLAMP);
		else barShader = new LinearGradient(0, TOP_MARGIN_PX, 0, getHeight() - BOTTOM_MARGIN_PX, topColor, Color.BLACK, Shader.TileMode.CLAMP);

		barPaint.setShader(barShader);
	}

	private void drawIndicator(Canvas canvas)
	{
		float yOffset = MathUtils.map(channel.getValue(), channel.getMaxValue(), 0, TOP_MARGIN_PX, getHeight() - BOTTOM_MARGIN_PX);
		if(indicatorOuterPath == null || indicatorInnerPath == null) updateIndicator(yOffset);

		canvas.drawLine(LEFT_MARGIN_PX, yOffset, getWidth(), yOffset, indicatorLinePaint);
		canvas.drawPath(indicatorOuterPath, indicatorOuterPaint);
		canvas.drawPath(indicatorInnerPath, indicatorInnerPaint);
	}

	private void updateIndicator(float yOffset)
	{
		indicatorOuterPath = new Path(TRIANGLE_OUTER_PATH);
		indicatorOuterPath.close();
		indicatorOuterPath.offset(0, yOffset);

		indicatorInnerPath = new Path(TRIANGLE_INNER_PATH);
		indicatorInnerPath.close();
		indicatorInnerPath.offset(0, yOffset);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float value = MathUtils.map(event.getY(), TOP_MARGIN_PX, getHeight() - BOTTOM_MARGIN_PX, channel.getMaxValue(), 0);
		value = MathUtils.clamp(value, 0, channel.getMaxValue());
		channel.setValue(Math.round(value));
		indicatorOuterPath = null;
		indicatorInnerPath = null;
		if(listener != null) listener.onChannelValueChanged();

		invalidate();
		return true;
	}

	void setOnColorBarUpdateListener(OnColorBarUpdateListener listener)
	{
		this.listener = listener;
	}

	void setChannel(ColorChannel channel)
	{
		this.channel = channel;
		this.barShader = null;
		this.indicatorOuterPath = null;
		this.indicatorInnerPath = null;
		invalidate();
	}

	void update()
	{
		this.barShader = null;
		this.indicatorOuterPath = null;
		this.indicatorInnerPath = null;
		invalidate();
	}
}
