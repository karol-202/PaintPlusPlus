package pl.karol202.paintplus.color;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.google.firebase.crash.FirebaseCrash;
import com.kunzisoft.androidclearchroma.IndicatorMode;
import com.kunzisoft.androidclearchroma.colormode.ColorMode;
import com.kunzisoft.androidclearchroma.fragment.ChromaColorFragment;
import com.kunzisoft.androidclearchroma.listener.OnColorChangedListener;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.settings.ActivitySettings;

public class ActivityColorSelect extends AppCompatActivity implements OnColorChangedListener
{
	public static final String ALPHA_KEY = "use_alpha";
	public static final String COLOR_KEY = "initial_color";
	private static final String CURRENT_COLOR_KEY = "current_color";
	
	private static final int VALUE_THRESHOLD = 192;
	
	private int darkTextColor;
	private int lightTextColor;
	
	private boolean useAlpha;
	private int defaultColor;
	private int currentColor;
	private boolean portrait;
	private ActionBar actionBar;
	
	private Toolbar toolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color_select);
		onArgumentsRead(getIntent());
		onLoadInstanceState(savedInstanceState);
		
		detectConfiguration();
		loadResources();
		
		initToolbar();
		if(savedInstanceState == null) createFragment();
		onColorChanged(currentColor);
	}
	
	private void onArgumentsRead(Intent intent)
	{
		if(intent == null) return;
		useAlpha = intent.getBooleanExtra(ALPHA_KEY, false);
		defaultColor = intent.getIntExtra(COLOR_KEY, Color.BLACK);
	}
	
	private void onLoadInstanceState(Bundle savedInstanceState)
	{
		if(savedInstanceState != null) currentColor = savedInstanceState.getInt(CURRENT_COLOR_KEY, defaultColor);
		else currentColor = defaultColor;
	}
	
	private void detectConfiguration()
	{
		portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}
	
	private void loadResources()
	{
		darkTextColor = ResourcesCompat.getColor(getResources(), R.color.text_color_select_title_dark, null);
		lightTextColor = ResourcesCompat.getColor(getResources(), R.color.text_color_select_title_light, null);
	}
	
	private void initToolbar()
	{
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		if(actionBar == null) throw new RuntimeException("Action bar not found.");
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void createFragment()
	{
		ChromaColorFragment fragment = ChromaColorFragment.newInstance(currentColor, getColorMode(), IndicatorMode.DECIMAL);
		getSupportFragmentManager().beginTransaction()
								   .replace(R.id.color_fragment_container, fragment)
								   .commit();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(CURRENT_COLOR_KEY, currentColor);
	}
	
	@Override
	public void onColorChanged(@ColorInt int color)
	{
		currentColor = color;
		if(portrait) changeToolbarColor(color);
		setResultColor();
	}
	
	private void changeToolbarColor(@ColorInt int color)
	{
		color |= 0xFF000000;
		toolbar.setBackgroundColor(color);
		
		int value = Math.max(Color.red(color), Math.max(Color.green(color), Color.blue(color)));
		int titleColor = value < VALUE_THRESHOLD ? lightTextColor : darkTextColor;
		toolbar.setTitleTextColor(titleColor);
		
		Drawable upArrow = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
		if(upArrow != null) upArrow.setColorFilter(titleColor, PorterDuff.Mode.SRC_ATOP);
		actionBar.setHomeAsUpIndicator(upArrow);
	}
	
	private void setResultColor()
	{
		Intent result = new Intent();
		result.putExtra(COLOR_KEY, currentColor);
		setResult(RESULT_OK, result);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == android.R.id.home) onBackPressed();
		return super.onOptionsItemSelected(item);
	}
	
	private ColorMode getColorMode()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String value = preferences.getString(ActivitySettings.KEY_COLOR_MODE, "RGB");
		switch(value)
		{
		case "RGB": return useAlpha ? ColorMode.ARGB : ColorMode.RGB;
		case "HSV": return ColorMode.HSV;
		case "HSL": return ColorMode.HSL;
		case "CMYK": return ColorMode.CMYK255;
		}
		FirebaseCrash.report(new RuntimeException("Unknown color mode: " + value));
		return ColorMode.RGB;
	}
}