package pl.karol202.paintplus.tool.shape.line;

import pl.karol202.paintplus.R;

public enum Cap
{
	ROUND(R.string.line_cap_round, R.drawable.ic_cap_round_black_24dp),
	SQUARE(R.string.line_cap_square, R.drawable.ic_cap_square_black_24dp),
	BUTT(R.string.line_cap_butt, R.drawable.ic_cap_butt_black_24dp);
	
	private int name;
	private int icon;
	
	Cap(int name, int icon)
	{
		this.name = name;
		this.icon = icon;
	}
	
	public int getName()
	{
		return name;
	}
	
	public int getIcon()
	{
		return icon;
	}
}