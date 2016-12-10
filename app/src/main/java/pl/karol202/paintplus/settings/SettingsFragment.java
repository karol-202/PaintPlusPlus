package pl.karol202.paintplus.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import pl.karol202.paintplus.R;

public class SettingsFragment extends PreferenceFragment
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}