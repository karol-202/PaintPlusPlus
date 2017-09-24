package pl.karol202.paintplus.color.picker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.picker.numerical.ColorNumericalFragment;

public class ActivityColorSelect extends AppCompatActivity
{
	public static final String ALPHA_KEY = "use_alpha";
	public static final String COLOR_KEY = "initial_color";
	private static final String CURRENT_COLOR_KEY = "current_color";
	
	private boolean useAlpha;
	private int initialColor;
	private int currentColor;
	
	private ActionBar actionBar;
	private ColorsPagerAdapter adapter;
	
	private Toolbar toolbar;
	private ViewPager viewPager;
	private TabLayout tabLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color_select);
		onArgumentsRead(getIntent());
		onLoadInstanceState(savedInstanceState);
		
		initPagerAdapter();
		initToolbar();
		initViewPager();
		initTabLayout();
		
		setResultColor();
	}
	
	private void onArgumentsRead(Intent intent)
	{
		if(intent == null) return;
		useAlpha = intent.getBooleanExtra(ALPHA_KEY, false);
		initialColor = intent.getIntExtra(COLOR_KEY, Color.BLACK);
	}
	
	private void onLoadInstanceState(Bundle savedInstanceState)
	{
		if(savedInstanceState != null) currentColor = savedInstanceState.getInt(CURRENT_COLOR_KEY, initialColor);
		else currentColor = initialColor;
	}
	
	private void initPagerAdapter()
	{
		Fragment numericalFragment = new ColorNumericalFragment();
		Fragment panelFragment = new ColorPanelFragment();
		
		Bundle args = new Bundle();
		args.putBoolean("useAlpha", useAlpha);
		numericalFragment.setArguments(args);
		panelFragment.setArguments(args);
		
		adapter = new ColorsPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(numericalFragment, R.drawable.ic_color_picker_numerical_white_24dp);
		adapter.addFragment(panelFragment, R.drawable.ic_color_picker_panel_white_24dp);
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
	
	private void initViewPager()
	{
		viewPager = findViewById(R.id.colors_view_pager);
		viewPager.setAdapter(adapter);
	}
	
	private void initTabLayout()
	{
		tabLayout = findViewById(R.id.colors_tab_layout);
		tabLayout.setupWithViewPager(viewPager);
		adapter.setupTabLayoutIcons(tabLayout);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(CURRENT_COLOR_KEY, currentColor);
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
	
	int getCurrentColor()
	{
		return currentColor;
	}
	
	void setCurrentColor(int currentColor)
	{
		this.currentColor = currentColor;
		setResultColor();
	}
}