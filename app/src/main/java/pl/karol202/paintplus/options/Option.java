package pl.karol202.paintplus.options;

import android.app.Activity;
import pl.karol202.paintplus.PaintView;

public abstract class Option
{
	protected Activity activity;
	protected PaintView paintView;

	public Option(Activity activity, PaintView paintView)
	{
		this.activity = activity;
		this.paintView = paintView;
	}

	public abstract void execute();
}
