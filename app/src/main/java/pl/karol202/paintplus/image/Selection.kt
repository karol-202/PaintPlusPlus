package pl.karol202.paintplus.image

import android.graphics.Point
import android.graphics.Rect
import android.graphics.Region
import androidx.core.graphics.xor
import pl.karol202.paintplus.util.plus

data class Selection(val region: Region)
{
	companion object
	{
		val empty = Selection(Region())

		fun fromRect(rect: Rect) = Selection(Region(rect))
	}

	val path = region.boundaryPath
	val bounds = region.bounds

	operator fun contains(point: Point) = bounds.contains(point.x, point.y)

	fun translated(x: Int, y: Int) = Selection(region + Point(x, y))

	fun inverted(rect: Rect) = Selection(region xor rect)

	/*
	public void commitSelectionRectangle(Rect rect, Op op)
	{
		ActionSelectionChange action = new ActionSelectionChange(image);
		action.setOldRegion();

		region.op(rect, op);
		updatePath();

		action.applyAction();
	}

	public void commitSelectionOval(Rect rect, Op op)
	{
		ActionSelectionChange action = new ActionSelectionChange(image);
		action.setOldRegion();

		RectF rectF = new RectF(rect);
		Path ovalPath = new Path();
		ovalPath.addOval(rectF, CW);

		Region ovalRegion = new Region();
		ovalRegion.setPath(ovalPath, new Region(0, 0, imageRect.right, imageRect.bottom));

		region.op(ovalRegion, op);
		updatePath();

		action.applyAction();
	}
	 */
}
