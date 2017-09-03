package pl.karol202.paintplus.settings;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;

public class SeekBarPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements SeekBar.OnSeekBarChangeListener
{
	private SeekBar seekBar;
	private TextView textView;
	
	private int value;
	
	static SeekBarPreferenceDialogFragmentCompat newInstance(Preference preference)
	{
		SeekBarPreferenceDialogFragmentCompat fragment = new SeekBarPreferenceDialogFragmentCompat();
		Bundle bundle = new Bundle(1);
		bundle.putString("key", preference.getKey());
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	protected void onBindDialogView(View view)
	{
		super.onBindDialogView(view);
		if(!(getPreference() instanceof SeekBarPreference)) throw new IllegalStateException("Unsupported preference.");
		value = ((SeekBarPreference) getPreference()).getValue();
		
		seekBar = view.findViewById(R.id.seekBar_preference);
		seekBar.setOnSeekBarChangeListener(this);
		seekBar.setProgress(value);
		
		textView = view.findViewById(R.id.text_seekBar_preference);
		update();
	}
	
	@Override
	public void onDialogClosed(boolean positiveResult)
	{
		if(positiveResult) update();
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if(textView != null) textView.setText(getTextFromValue(progress));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
	
	static String getTextFromValue(int value)
	{
		return value + "%";
	}
	
	private void update()
	{
		value = seekBar.getProgress();
		((SeekBarPreference) getPreference()).setValue(value);
		getPreference().setSummary(getTextFromValue(value));
		
		if(textView != null) textView.setText(getTextFromValue(value));
	}
}