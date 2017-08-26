package pl.karol202.paintplus.tool.gradient;

import android.graphics.Shader;
import pl.karol202.paintplus.R;

public enum GradientRepeatability
{
	NO_REPEAT(R.string.gradient_repeatability_no_repeat, R.drawable.ic_gradient_repeatability_no_repeat_24dp, Shader.TileMode.CLAMP),
	REPEAT(R.string.gradient_repeatability_repeat, R.drawable.ic_gradient_repeatability_repeat_24dp, Shader.TileMode.REPEAT),
	MIRROR(R.string.gradient_repeatability_mirror, R.drawable.ic_gradient_repeatability_mirror_24dp, Shader.TileMode.MIRROR);
	
	private int name;
	private int icon;
	private Shader.TileMode tileMode;
	
	GradientRepeatability(int name, int icon, Shader.TileMode tileMode)
	{
		this.name = name;
		this.icon = icon;
		this.tileMode = tileMode;
	}
	
	public int getName()
	{
		return name;
	}
	
	public int getIcon()
	{
		return icon;
	}
	
	public Shader.TileMode getTileMode()
	{
		return tileMode;
	}
}