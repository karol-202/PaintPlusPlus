package pl.karol202.paintplus.options;

import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import pl.karol202.paintplus.image.Image;

public abstract class LegacyOption
{
	public interface AppContextLegacy
	{
		Context getContext();

		Snackbar createSnackbar(int message, int duration);
	}

	private AppContextLegacy context;
	private Image image;

	public LegacyOption(AppContextLegacy context, Image image)
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
