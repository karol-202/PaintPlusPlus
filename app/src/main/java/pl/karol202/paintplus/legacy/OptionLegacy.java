package pl.karol202.paintplus.legacy;

import android.content.Context;
import pl.karol202.paintplus.image.Image;

public abstract class OptionLegacy
{
	private AppContextLegacy context;
	private Image image;

	public OptionLegacy(AppContextLegacy context, Image image)
	{
		this.context = context;
		this.image = image;
	}

	public abstract void execute();

	public AppContextLegacy getAppContext()
	{
		return context;
	}

	public Context getContext()
	{
		return context.getContext();
	}

	public Image getImage()
	{
		return image;
	}
}
