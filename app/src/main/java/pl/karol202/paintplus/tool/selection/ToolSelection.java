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

package pl.karol202.paintplus.tool.selection;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
import pl.karol202.paintplus.tool.ToolProperties;

import static pl.karol202.paintplus.tool.selection.ToolSelectionShape.OVAL;
import static pl.karol202.paintplus.tool.selection.ToolSelectionShape.RECTANGLE;

public class ToolSelection extends StandardTool
{
	private enum MoveType
	{
		NONE,
		LEFT_TOP_CORNER, RIGHT_TOP_CORNER, LEFT_BOTTOM_CORNER, RIGHT_BOTTOM_CORNER,
		LEFT_SIDE, TOP_SIDE, RIGHT_SIDE, BOTTOM_SIDE,
		MOVE
	}

	private static final int MAX_DISTANCE_DP = 50;
	private static final float SELECTION_LINE_WIDTH_DP = 1f;

	private final int MAX_DISTANCE_PX;
	private final float SELECTION_LINE_WIDTH_PX;

	private ToolSelectionShape shape;
	private ToolSelectionMode mode;

	private OnSelectionEditListener selectionListener;
	private Rect rect;
	private boolean editMode;
	private boolean rectCreated;
	private Paint paint;
	private Rect dirtyRect;

	private MoveType movingType;
	private Rect rectAtBeginning;
	private Point movingStart;

	public ToolSelection(LegacyImage image)
	{
		super(image);
		MAX_DISTANCE_PX = (int) (MAX_DISTANCE_DP * image.SCREEN_DENSITY);
		SELECTION_LINE_WIDTH_PX = (int) (SELECTION_LINE_WIDTH_DP * image.SCREEN_DENSITY);

		this.shape = RECTANGLE;
		this.mode = ToolSelectionMode.NEW;

		this.rect = new Rect();
		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.paint.setColor(Color.BLACK);
		this.paint.setStyle(Paint.Style.STROKE);

		cleanUp();
	}

	@Override
	public int getName()
	{
		return R.string.tool_selection;
	}

	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_selection_black_24dp;
	}

	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return SelectionProperties.class;
	}

	@Override
	public ToolCoordinateSpace getInputCoordinateSpace()
	{
		return ToolCoordinateSpace.IMAGE_SPACE;
	}

	@Override
	public boolean isUsingSnapping()
	{
		return false;
	}

	@Override
	public boolean onTouchStart(float xFloat, float yFloat)
	{
		if(image.getSelectedLayer() == null)
		{
			if(editMode) cleanUp();
			return false;
		}

		int x = Math.round(xFloat);
		int y = Math.round(yFloat);

		if(!editMode) enableEditMode();
		if(!rectCreated)
		{
			setLeft(x);
			setTop(y);
		}
		else
		{
			setMovingType(x, y);
			rectAtBeginning = new Rect(rect);
			movingStart = new Point(x, y);
		}
		expandDirtyRect();

		return true;
	}

	private void enableEditMode()
	{
		editMode = true;
		if(selectionListener != null) selectionListener.onStartSelectionEditing();
	}

	private void setMovingType(int x, int y)
	{
		int leftDist = Math.abs(rect.left - x);
		int topDist = Math.abs(rect.top - y);
		int rightDist = Math.abs(rect.right - x);
		int bottomDist = Math.abs(rect.bottom - y);

		boolean left = leftDist < (MAX_DISTANCE_PX / image.getZoom());
		boolean top = topDist < (MAX_DISTANCE_PX / image.getZoom());
		boolean right = rightDist < (MAX_DISTANCE_PX / image.getZoom());
		boolean bottom = bottomDist < (MAX_DISTANCE_PX / image.getZoom());
		boolean xInside = x > rect.left && x < rect.right;
		boolean yInside = y > rect.top && y < rect.bottom;

		movingType = MoveType.NONE;

		if(xInside && yInside) movingType = MoveType.MOVE;

		if(left && !right && yInside) movingType = MoveType.LEFT_SIDE;
		if(top && !bottom && xInside) movingType = MoveType.TOP_SIDE;
		if(right && !left && yInside) movingType = MoveType.RIGHT_SIDE;
		if(bottom && !top && xInside) movingType = MoveType.BOTTOM_SIDE;

		if(left && top) movingType = MoveType.LEFT_TOP_CORNER;
		if(right && top) movingType = MoveType.RIGHT_TOP_CORNER;
		if(right && bottom) movingType = MoveType.RIGHT_BOTTOM_CORNER;
		if(left && bottom) movingType = MoveType.LEFT_BOTTOM_CORNER;
	}

	@Override
	public boolean onTouchMove(float xFloat, float yFloat)
	{
		int x = Math.round(xFloat);
		int y = Math.round(yFloat);

		if(!rectCreated)
		{
			setRight(x);
			setBottom(y);
		}
		else move(x, y);
		expandDirtyRect();

		return true;
	}

	@Override
	public boolean onTouchStop(float xFloat, float yFloat)
	{
		int x = Math.round(xFloat);
		int y = Math.round(yFloat);

		if(!rectCreated)
		{
			setRight(x);
			setBottom(y);
		}
		else move(x, y);
		correctBounds();
		rectCreated = true;
		dirtyRect = null;

		return true;
	}

	private void move(int x, int y)
	{
		int deltaX = x - movingStart.x;
		int deltaY = y - movingStart.y;
		rect = new Rect(rectAtBeginning);

		switch(movingType)
		{
		case NONE:
			break;
		case LEFT_TOP_CORNER:
			setLeft(rect.left + deltaX);
			setTop(rect.top + deltaY);
			break;
		case RIGHT_TOP_CORNER:
			setRight(rect.right + deltaX);
			setTop(rect.top + deltaY);
			break;
		case LEFT_BOTTOM_CORNER:
			setLeft(rect.left + deltaX);
			setBottom(rect.bottom + deltaY);
			break;
		case RIGHT_BOTTOM_CORNER:
			setRight(rect.right + deltaX);
			setBottom(rect.bottom + deltaY);
			break;
		case LEFT_SIDE:
			setLeft(rect.left + deltaX);
			break;
		case TOP_SIDE:
			setTop(rect.top + deltaY);
			break;
		case RIGHT_SIDE:
			setRight(rect.right + deltaX);
			break;
		case BOTTOM_SIDE:
			setBottom(rect.bottom + deltaY);
			break;
		case MOVE:
			PointF oldCenter = new PointF(rect.centerX(), rect.centerY());
			PointF newCenter = new PointF(oldCenter.x + deltaX, oldCenter.y + deltaY);
			helpersManager.snapPoint(newCenter);
			rect.offset((int) (newCenter.x - oldCenter.x), (int) (newCenter.y - oldCenter.y));
			break;
		}
	}

	private void setLeft(int left)
	{
		rect.left = (int) helpersManager.snapX(left);
	}

	private void setTop(int top)
	{
		rect.top = (int) helpersManager.snapY(top);
	}

	private void setRight(int right)
	{
		rect.right = (int) helpersManager.snapX(right);
	}

	private void setBottom(int bottom)
	{
		rect.bottom = (int) helpersManager.snapY(bottom);
	}

	private void correctBounds()
	{
		if(rect.left > rect.right)
		{
			int temp = rect.left;
			rect.left = rect.right;
			rect.right = temp;
		}
		if(rect.top > rect.bottom)
		{
			int temp = rect.top;
			rect.top = rect.bottom;
			rect.bottom = temp;
		}
	}

	private void expandDirtyRect()
	{
		if(dirtyRect == null) dirtyRect = new Rect();
		dirtyRect.left = Math.min(dirtyRect.left, rect.left);
		dirtyRect.top = Math.min(dirtyRect.top, rect.top);
		dirtyRect.right = Math.max(dirtyRect.right, rect.right);
		dirtyRect.bottom = Math.max(dirtyRect.bottom, rect.bottom);
	}

	void applySelection()
	{
		if(shape == RECTANGLE) selection.commitSelectionRectangle(rect, mode.getOp());
		else if(shape == OVAL) selection.commitSelectionOval(rect, mode.getOp());
		cancelSelection();
	}

	void cancelSelection()
	{
		cleanUp();
		image.updateImage();
	}

	private void cleanUp()
	{
		editMode = false;
		rectCreated = false;
		rect.left = -1;
		rect.top = -1;
		rect.right = -1;
		rect.bottom = -1;
	}

	void selectAll()
	{
		selection.selectAll();
	}

	void selectNothing()
	{
		selection.selectNothing();
	}

	void invertSelection()
	{
		selection.revert();
	}

	@Override
	public boolean providesDirtyRegion()
	{
		return true;
	}

	@Override
	public Rect getDirtyRegion()
	{
		return dirtyRect;
	}

	@Override
	public void resetDirtyRegion()
	{
		if(dirtyRect != null) dirtyRect.setEmpty();
	}

	@Override
	public boolean doesOnLayerDraw(boolean layerVisible)
	{
		return false;
	}

	@Override
	public boolean doesOnTopDraw()
	{
		return true;
	}

	@Override
	public ToolCoordinateSpace getOnLayerDrawingCoordinateSpace()
	{
		return null;
	}

	@Override
	public ToolCoordinateSpace getOnTopDrawingCoordinateSpace()
	{
		return ToolCoordinateSpace.IMAGE_SPACE;
	}

	@Override
	public void drawOnLayer(Canvas canvas) { }

	@Override
	public void drawOnTop(Canvas canvas)
	{
		if(rect.left == -1 || rect.top == -1 || rect.right == -1 || rect.bottom == -1) return;
		paint.setStrokeWidth(SELECTION_LINE_WIDTH_PX / image.getZoom());

		if(shape == RECTANGLE) canvas.drawRect(rect, paint);
		else if(shape == OVAL) canvas.drawOval(new RectF(rect), paint);
	}

	void setSelectionListener(OnSelectionEditListener selectionListener)
	{
		this.selectionListener = selectionListener;
	}

	boolean isInEditMode()
	{
		return editMode;
	}

	ToolSelectionShape getShape()
	{
		return shape;
	}

	void setShape(ToolSelectionShape shape)
	{
		this.shape = shape;
	}

	ToolSelectionMode getMode()
	{
		return mode;
	}

	void setMode(ToolSelectionMode mode)
	{
		this.mode = mode;
	}
}
