package pl.karol202.paintplus.options.legacy;

import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import pl.karol202.paintplus.image.LegacyImage;

public abstract class LegacyOption
{
	public interface AppContextLegacy
	{
		Context getContext();

		Snackbar createSnackbar(int message, int duration);
	}

	private AppContextLegacy context;
	private LegacyImage image;

	public LegacyOption(AppContextLegacy context, LegacyImage image)
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

	public LegacyImage getImage()
	{
		return image;
	}
}
