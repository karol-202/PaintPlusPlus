package pl.karol202.paintplus.tool.selection;

import android.graphics.*;
import android.view.MotionEvent;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;

import static pl.karol202.paintplus.tool.selection.ToolSelectionShape.OVAL;
import static pl.karol202.paintplus.tool.selection.ToolSelectionShape.RECTANGLE;

public class ToolSelection extends Tool
{
	private enum MoveType
	{
		NONE,
		LEFT_TOP_CORNER, RIGHT_TOP_CORNER, LEFT_BOTTOM_CORNER, RIGHT_BOTTOM_CORNER,
		LEFT_SIDE, TOP_SIDE, RIGHT_SIDE, BOTTOM_SIDE,
		MOVE
	}
	
	private final int MAX_DISTANCE = 50;
	
	private ToolSelectionShape shape;
	private ToolSelectionMode mode;
	
	private OnSelectionEditListener selectionListener;
	private HelpersManager helpersManager;
	private Selection selection;
	private Rect rect;
	private boolean editMode;
	private boolean rectCreated;
	private Paint paint;
	
	private MoveType movingType;
	private Rect rectAtBeginning;
	private Point movingStart;
	
	public ToolSelection(Image image)
	{
		super(image);
		this.shape = RECTANGLE;
		this.mode = ToolSelectionMode.NEW;
		
		this.selection = image.getSelection();
		this.rect = new Rect();
		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.paint.setColor(Color.BLACK);
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setStrokeWidth(2f);
		
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
	public boolean isUsingSnapping()
	{
		return false;
	}
	
	@Override
	public boolean isImageLimited()
	{
		return false;
	}
	
	@Override
	public boolean onTouch(MotionEvent event, HelpersManager manager)
	{
		super.onTouch(event, manager);
		helpersManager = manager;
		
		if(image.getSelectedLayer() == null)
		{
			if(editMode) cleanUp();
			return false;
		}
		if(event.getAction() == MotionEvent.ACTION_DOWN) onTouchStart(Math.round(event.getX()), Math.round(event.getY()));
		else if(event.getAction() == MotionEvent.ACTION_MOVE) onTouchMove(Math.round(event.getX()), Math.round(event.getY()));
		else if(event.getAction() == MotionEvent.ACTION_UP) onTouchStop(Math.round(event.getX()), Math.round(event.getY()));
		return true;
	}
	
	private void onTouchStart(int x, int y)
	{
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
		
		boolean left = leftDist < MAX_DISTANCE;
		boolean top = topDist < MAX_DISTANCE;
		boolean right = rightDist < MAX_DISTANCE;
		boolean bottom = bottomDist < MAX_DISTANCE;
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
	
	private void onTouchMove(int x, int y)
	{
		if(!rectCreated)
		{
			setRight(x);
			setBottom(y);
		}
		else move(x, y);
	}
	
	private void onTouchStop(int x, int y)
	{
		if(!rectCreated)
		{
			setRight(x);
			setBottom(y);
		}
		else move(x, y);
		correctBounds();
		rectCreated = true;
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
	
	@Override
	public boolean isLayerSpace()
	{
		return false;
	}
	
	@Override
	public boolean doesScreenDraw(boolean layerVisible)
	{
		return true;
	}
	
	@Override
	public boolean isDrawingOnTop()
	{
		return true;
	}
	
	@Override
	public void onScreenDraw(Canvas canvas)
	{
		if(rect.left == -1 || rect.top == -1 || rect.right == -1 || rect.bottom == -1) return;
		if(shape == RECTANGLE) canvas.drawRect(transformRect(), paint);
		else if(shape == OVAL) canvas.drawOval(new RectF(transformRect()), paint);
	}
	
	private Rect transformRect()
	{
		Rect newRect = new Rect(rect);
		newRect.offset(-image.getViewX(), -image.getViewY());
		
		newRect.left *= image.getZoom();
		newRect.top *= image.getZoom();
		newRect.right *= image.getZoom();
		newRect.bottom *= image.getZoom();
		return newRect;
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