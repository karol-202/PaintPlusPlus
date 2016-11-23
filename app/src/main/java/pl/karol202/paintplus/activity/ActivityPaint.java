package pl.karol202.paintplus.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import pl.karol202.paintplus.*;
import pl.karol202.paintplus.ColorsSelect.OnColorSelectedListener;
import pl.karol202.paintplus.options.ImageNew;
import pl.karol202.paintplus.options.ImageResize;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.Tool.OnToolUpdatedListener;
import pl.karol202.paintplus.tool.ToolType;

public class ActivityPaint extends AppCompatActivity implements ListView.OnItemClickListener, OnToolUpdatedListener, OnColorSelectedListener
{
	private class DrawerAdapter extends ActionBarDrawerToggle
	{
		public DrawerAdapter(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar)
		{
			super(activity, drawerLayout, toolbar, R.string.action_drawer_open, R.string.action_drawer_close);
		}
		
		@Override
		public void onDrawerOpened(View drawerView)
		{
			if(drawerView == drawerLeft)
			{
				super.onDrawerOpened(drawerView);
				onLeftDrawerOpened();
			}
			else if(drawerView == drawerRight) onRightDrawerOpened();
			invalidateOptionsMenu();
		}
		
		@Override
		public void onDrawerClosed(View drawerView)
		{
			if(drawerView == drawerLeft) super.onDrawerClosed(drawerView);
			else if(drawerView == drawerRight) removePropertiesFragment();
			if(!(layoutDrawer.isDrawerOpen(drawerLeft) || layoutDrawer.isDrawerOpen(drawerRight)))
			{
				actionBar.setTitle(title);
			}
			invalidateOptionsMenu();
		}
		
		@Override
		public void onDrawerSlide(View drawerView, float slideOffset)
		{
			if(drawerView == drawerLeft)
			{
				super.onDrawerSlide(drawerView, slideOffset);
				layoutDrawer.closeDrawer(drawerRight);
			}
			else if(drawerView == drawerRight && !propertiesAttached) createPropertiesFragment();
		}
	}
	
	private View decorView;
	private FragmentManager fragments;
	private ActionBarDrawerToggle drawerListener;
	private boolean propertiesAttached;
	private ActionBar actionBar;

	private Toolbar toolbar;
	private PaintView paintView;
	private DrawerLayout layoutDrawer;
	private ListView drawerLeft;
	private View drawerRight;
	

	private ImageNew imageNew;
	private ImageResize imageResize;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paint);
		decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
									  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
									  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
									  | View.SYSTEM_UI_FLAG_FULLSCREEN
									  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
		{
			@Override
			public void onSystemUiVisibilityChange(int visibility)
			{
				decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
											  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
											  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
											  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
											  | View.SYSTEM_UI_FLAG_FULLSCREEN
											  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			}
		});
		fragments = getFragmentManager();
		
		/*DisplayMetrics metrics = getResources().getDisplayMetrics();
		float widthDp = metrics.widthPixels / metrics.density;
		int maxDrawerLeftWidth = (int) (280 * metrics.density);
		int maxDrawerRightWidth = (int) (320 * metrics.density);*/

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		paintView = (PaintView) findViewById(R.id.paint_view);

		layoutDrawer = (DrawerLayout) findViewById(R.id.layout_drawer);
		drawerListener = new ActionBarDrawerToggle(this, layoutDrawer, toolbar,
												   R.string.action_drawer_open, R.string.action_drawer_close)
		{
			
		};
		layoutDrawer.addDrawerListener(drawerListener);

		drawerLeft = (ListView) findViewById(R.id.drawer_left);
		LayoutParams params1 = drawerLeft.getLayoutParams();
		params1.width = (int) ((widthDp - 112) * metrics.density);
		if(params1.width > maxDrawerLeftWidth) params1.width = maxDrawerLeftWidth;
		drawerLeft.setAdapter(new AdapterListTools(this, ToolType.values()));
		drawerLeft.setOnItemClickListener(this);

		drawerRight = findViewById(R.id.drawer_right);
		LayoutParams params2 = drawerRight.getLayoutParams();
		params2.width = (int) ((widthDp - 56) * metrics.density);
		if(params2.width > maxDrawerRightWidth) params2.width = maxDrawerRightWidth;

		ColorsSelect colors = (ColorsSelect) fragments.findFragmentById(R.id.colorsFragment);
		colors.setColors(paintView.getColors());

		imageNew = new ImageNew(this, paintView);
		imageResize = new ImageResize(this, paintView);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		drawerListener.syncState();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus)
		{
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_paint, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		switch(id)
		{
		case android.R.id.home:
			drawerListener.onOptionsItemSelected(item);
			return true;
		case R.id.action_tool:
			layoutDrawer.closeDrawer(drawerLeft);
			if(layoutDrawer.isDrawerOpen(drawerRight)) layoutDrawer.closeDrawer(drawerRight);
			else layoutDrawer.openDrawer(drawerRight);
			return true;
		case R.id.action_new_image:
			imageNew.execute();
			return true;
		case R.id.action_resize_image:
			imageResize.execute();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		boolean drawerOpen = layoutDrawer.isDrawerOpen(drawerLeft) ||
							 layoutDrawer.isDrawerOpen(drawerRight);
		menu.setGroupVisible(R.id.group_paint, !drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_main);
		drawerListener.onConfigurationChanged(newConfig);
	}

	private void onLeftDrawerOpened()
	{
		String toolChoice = getResources().getString(R.string.choice_of_tool);
		actionBar.setTitle(toolChoice);
	}
	
	private void onRightDrawerOpened()
	{
		String properties = getResources().getString(R.string.properties);
		String tool = getResources().getString(paintView.getToolType().getName());
		actionBar.setTitle(properties + ": " + tool);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		paintView.setTool(ToolType.values()[position]);
		layoutDrawer.closeDrawer(drawerLeft);
	}

	public void onToolUpdated(Tool tool)
	{
		paintView.updateTool(tool);
	}

	@Override
	public void onColorSelected(ColorsSet colors)
	{
		paintView.setColors(colors);
	}

	private void createPropertiesFragment()
	{
		try
		{
			attachPropertiesFragment();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Error: Could not instantiate fragment from fragment class." +
									   "Probably the fragment class does not contain " +
									   "default constructor.", e);
		}
	}

	private void attachPropertiesFragment() throws InstantiationException, IllegalAccessException
	{
		Fragment properties = paintView.getToolType().getFragmentClass().newInstance();
		Bundle propArgs = new Bundle();
		propArgs.putParcelable("tool", paintView.getTool());
		properties.setArguments(propArgs);
		FragmentTransaction propTrans = fragments.beginTransaction();
		propTrans.add(R.id.propertiesFragment, properties);
		propTrans.commit();
		propertiesAttached = true;
	}
	
	private void removePropertiesFragment()
	{
		Fragment fragment = fragments.findFragmentById(R.id.propertiesFragment);
		FragmentTransaction propTrans = fragments.beginTransaction();
		propTrans.remove(fragment);
		propTrans.commit();
		propertiesAttached = false;
	}
}