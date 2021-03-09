package pl.karol202.paintplus.legacy;

import android.content.Context;
import com.google.android.material.snackbar.Snackbar;

public interface AppContextLegacy
{
	Context getContext();

	Snackbar createSnackbar(int message, int duration);
}
