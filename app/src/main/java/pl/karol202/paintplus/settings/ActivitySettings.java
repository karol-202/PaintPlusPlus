package pl.karol202.paintplus.settings;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import pl.karol202.paintplus.R;

public class ActivitySettings extends AppCompatActivity
{
	public static final String KEY_JPG_QUALITY = "preference_jpg_quality";
	public static final String KEY_VIEW_SMOOTH = "preference_view_smooth";
	public static final String KEY_COLOR_MODE = "preference_color_mode";
	
	private FragmentManager fragments;
	private ActionBar actionBar;
	
	private Toolbar toolbar;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		fragments = getFragmentManager();
		fragments.beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		if(actionBar == null) throw new RuntimeException("Cannot set action bar of activity.");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == android.R.id.home) finish();
		return super.onOptionsItemSelected(item);
	}
}