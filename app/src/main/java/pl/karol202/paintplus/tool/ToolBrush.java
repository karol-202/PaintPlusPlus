package pl.karol202.paintplus.tool;

import android.graphics.*;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import pl.karol202.paintplus.ColorsSet;
import pl.karol202.paintplus.math.Line;
import pl.karol202.paintplus.math.Utils;
import pl.karol202.paintplus.math.Vector2f;

public class ToolBrush extends Tool
{
	private float size;
	private float shapeOffset;

	private Paint paint;
	private float lastX;
	private float lastY;

	public ToolBrush()
	{
		this.size = 25;
		this.shapeOffset = 0;

		this.paint = new Paint();
		this.lastX = -1;
		this.lastY = -1;
	}

	@Override
	public boolean onTouch(Canvas edit, ColorsSet colors, MotionEvent event)
	{


		return true;
	}

	@Override
	public void onTouchUp(Canvas edit, ColorsSet colors)
	{

	}

	private void onTouchStart(Canvas canvas, ColorsSet colors, float x, float y)
	{

	}

	private void onTouchMove(Canvas canvas, ColorsSet colors, float x, float y)
	{

	}

	private void onTouchStop(Canvas canvas, ColorsSet colors, float x, float y)
	{

	}

	@Override
	public void onDraw(Canvas canvas)
	{

	}

	@Override
	public ToolType getToolType()
	{
		return ToolType.BRUSH;
	}

	@Override
	public boolean onlyViewport()
	{
		return true;
	}

	@Override
	public void reset()
	{

	}

	public float getSize()
	{
		return size;
	}

	public void setSize(float size)
	{
		this.size = size;
	}

	public float getShapeOffset()
	{
		return shapeOffset;
	}

	public void setShapeOffset(float shapeOffset)
	{
		this.shapeOffset = shapeOffset;
	}

	public static final Parcelable.Creator<ToolBrush> CREATOR = new Parcelable.Creator<ToolBrush>()
	{
		@Override
		public ToolBrush createFromParcel(Parcel source)
		{
			return new ToolBrush(source);
		}

		@Override
		public ToolBrush[] newArray(int size)
		{
			return new ToolBrush[size];
		}
	};

	private ToolBrush(Parcel source)
	{
		this.size = source.readFloat();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeFloat(size);
	}
}