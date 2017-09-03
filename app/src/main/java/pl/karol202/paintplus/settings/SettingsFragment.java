package pl.karol202.paintplus.settings;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import pl.karol202.paintplus.R;

public class SettingsFragment extends PreferenceFragmentCompat
{
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
	{
		setPreferencesFromResource(R.xml.preferences, rootKey);
	}
	
	@Override
	public void onDisplayPreferenceDialog(Preference preference)
	{
		DialogFragment fragment;
		if (preference instanceof SeekBarPreference) {
			fragment = SeekBarPreferenceDialogFragmentCompat.newInstance(preference);
			fragment.setTargetFragment(this, 0);
			fragment.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
		}
		else super.onDisplayPreferenceDialog(preference);
	}
}