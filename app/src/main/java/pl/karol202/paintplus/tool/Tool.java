package pl.karol202.paintplus.tool;

import android.graphics.Canvas;
import android.os.Parcelable;
import android.view.MotionEvent;
import pl.karol202.paintplus.ColorsSet;

public abstract class Tool implements Parcelable
{
	public interface OnToolUpdatedListener
	{
		void onToolUpdated(Tool tool);
	}

	public abstract boolean onTouch(Canvas edit, ColorsSet colors, MotionEvent event);

	public abstract void onTouchUp(Canvas edit, ColorsSet colors);

	public abstract void onDraw(Canvas canvas);

	public abstract ToolType getToolType();

	public abstract boolean onlyViewport();

	public abstract void reset();

	@Override
	public int describeContents()
	{
		return 0;
	}
}