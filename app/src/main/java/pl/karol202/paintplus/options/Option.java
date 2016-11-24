package pl.karol202.paintplus.options;

import android.content.Context;
import pl.karol202.paintplus.Image;

public abstract class Option
{
	protected Context context;
	protected Image image;

	public Option(Context context, Image image)
	{
		this.context = context;
		this.image = image;
	}

	public abstract void execute();
}
