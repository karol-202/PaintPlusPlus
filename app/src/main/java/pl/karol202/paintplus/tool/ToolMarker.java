package pl.karol202.paintplus.tool;

import android.graphics.*;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import pl.karol202.paintplus.ColorsSet;

public class ToolMarker extends Tool
{
	private float size;

	private Paint paint;
	private Paint viewportMask;
	private Path path;
	private float lastX;
	private float lastY;

	public ToolMarker()
	{
		this.size = 25;

		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setStrokeCap(Paint.Cap.ROUND);
		this.paint.setStrokeJoin(Paint.Join.ROUND);

		this.viewportMask = new Paint();
		this.viewportMask.setColor(Color.BLACK);
		this.viewportMask.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

		this.path = new Path();
		this.path.setFillType(Path.FillType.EVEN_ODD);
	}

	@Override
	public boolean onTouch(Canvas edit, ColorsSet colors, MotionEvent event)
	{
		paint.setColor(colors.getFirstColor());
		paint.setStrokeWidth(size);

		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(edit, colors, event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(edit, colors, event.getX(), event.getY());
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchStop(edit, colors, event.getX(), event.getY());
		return true;
	}

	@Override
	public void onTouchUp(Canvas edit, ColorsSet colors)
	{
		onTouchStop(edit, colors, -1, -1);
	}

	private void onTouchStart(Canvas canvas, ColorsSet colors, float x, float y)
	{
		RectF oval = new RectF(x - size / 2,
							   y - size / 2,
							   x + size / 2,
							   y + size / 2);
		path.reset();
		path.moveTo(x, y);
		canvas.drawOval(oval, paint);
		lastX = x;
		lastY = y;
	}

	private void onTouchMove(Canvas canvas, ColorsSet colors, float x, float y)
	{
		path.quadTo(lastX, lastY, x, y);
		lastX = x;
		lastY = y;
	}

	private void onTouchStop(Canvas canvas, ColorsSet colors, float x, float y)
	{
		if(x != -1 && y != -1) path.lineTo(x, y);
		canvas.drawPath(path, paint);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		canvas.drawPath(path, paint);
		canvas.drawRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), viewportMask);
	}

	@Override
	public ToolType getToolType()
	{
		return ToolType.MARKER;
	}

	@Override
	public boolean onlyViewport()
	{
		return true;
	}

	@Override
	public void reset()
	{
		path.reset();
	}

	public float getSize()
	{
		return size;
	}

	public void setSize(float size)
	{
		this.size = size;
	}

	public static final Parcelable.Creator<ToolMarker> CREATOR = new Parcelable.Creator<ToolMarker>()
	{
		@Override
		public ToolMarker createFromParcel(Parcel source)
		{
			return new ToolMarker(source);
		}

		@Override
		public ToolMarker[] newArray(int size)
		{
			return new ToolMarker[size];
		}
	};

	private ToolMarker(Parcel source)
	{
		this.size = source.readFloat();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeFloat(size);
	}
}