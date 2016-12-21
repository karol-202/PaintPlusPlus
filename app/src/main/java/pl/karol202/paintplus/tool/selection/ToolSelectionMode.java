package pl.karol202.paintplus.tool.selection;

import android.graphics.Region.Op;
import pl.karol202.paintplus.R;

public enum ToolSelectionMode
{
	NEW(R.string.selection_mode_new, R.drawable.ic_selection_mode_new_black_24dp, Op.REPLACE),
	ADD(R.string.selection_mode_add, R.drawable.ic_selection_mode_add_black_24dp, Op.UNION),
	SUBTRACT(R.string.selection_mode_subtract, R.drawable.ic_selection_mode_subtract_black_24dp, Op.DIFFERENCE),
	MULTIPLY(R.string.selection_mode_multiply, R.drawable.ic_selection_mode_multiply_black_24dp, Op.INTERSECT);
	
	private int name;
	private int icon;
	private Op op;
	
	ToolSelectionMode(int name, int icon, Op op)
	{
		this.name = name;
		this.icon = icon;
		this.op = op;
	}
	
	public int getName()
	{
		return name;
	}
	
	public int getIcon()
	{
		return icon;
	}
	
	public Op getOp()
	{
		return op;
	}
}