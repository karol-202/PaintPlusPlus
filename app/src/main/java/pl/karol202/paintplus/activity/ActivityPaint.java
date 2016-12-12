package pl.karol202.paintplus.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import pl.karol202.paintplus.*;
import pl.karol202.paintplus.color.ColorsSelect;
import pl.karol202.paintplus.options.*;
import pl.karol202.paintplus.settings.ActivitySettings;
import pl.karol202.paintplus.tool.ToolsAdapter;
import pl.karol202.paintplus.tool.Tools;
import pl.karol202.paintplus.tool.properties.ToolProperties;
import pl.karol202.paintplus.util.GLHelper;

import java.util.HashMap;

public class ActivityPaint extends AppCompatActivity implements ListView.OnItemClickListener
{
	public interface ActivityResultListener
	{
		void onActivityResult(int resultCode, Intent data);
	}
	
	private class DrawerAdapter extends ActionBarDrawerToggle
	{
		public DrawerAdapter(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar)
		{
			super(activity, drawerLayout, toolbar, R.string.action_drawer_open, R.string.action_drawer_close);
		}
		
		@Override
		public void onDrawerOpened(View drawerView)
		{
			if(drawerView == drawerLeft) onLeftDrawerOpened(drawerView);
			else if(drawerView == drawerRight) onRightDrawerOpened();
			invalidateOptionsMenu();
		}
		
		@Override
		public void onDrawerClosed(View drawerView)
		{
			if(drawerView == drawerLeft) onLeftDrawerClosed(drawerView);
			else if(drawerView == drawerRight) onRightDrawerClosed();
			if(!(layoutDrawer.isDrawerOpen(drawerLeft) || layoutDrawer.isDrawerOpen(drawerRight)))
				onAllDrawersClosed();
			invalidateOptionsMenu();
		}
		
		@Override
		public void onDrawerSlide(View drawerView, float slideOffset)
		{
			if(drawerView == drawerLeft) onLeftDrawerMoved(drawerView, slideOffset);
			else if(drawerView == drawerRight) onRightDrawerMoved(drawerView, slideOffset);
		}
		
		private void onLeftDrawerOpened(View drawerView)
		{
			super.onDrawerOpened(drawerView);
			String toolChoice = getResources().getString(R.string.choice_of_tool);
			actionBar.setTitle(toolChoice);
		}
		
		private void onRightDrawerOpened()
		{
			String properties = getResources().getString(R.string.properties);
			String tool = getResources().getString(paintView.getTool().getName());
			actionBar.setTitle(properties + ": " + tool);
		}
		
		private void onLeftDrawerClosed(View drawerView)
		{
			super.onDrawerClosed(drawerView);
		}
		
		private void onRightDrawerClosed()
		{
			removePropertiesFragment();
		}
		
		private void onAllDrawersClosed()
		{
			actionBar.setTitle(R.string.activity_paint);
		}
		
		private void onLeftDrawerMoved(View drawerView, float slideOffset)
		{
			super.onDrawerSlide(drawerView, slideOffset);
			layoutDrawer.closeDrawer(drawerRight);
		}
		
		private void onRightDrawerMoved(View drawerView, float slideOffset)
		{
			super.onDrawerSlide(drawerView, slideOffset);
			if(!propertiesAttached) createPropertiesFragment();
		}
	}
	
	private View decorView;
	private FragmentManager fragments;
	private ActionBarDrawerToggle drawerListener;
	private ActionBar actionBar;
	private DisplayMetrics displayMetrics;
	private boolean propertiesAttached;
	private float screenWidthDp;
	private HashMap<Integer, ActivityResultListener> resultListeners;
	private AppDataFragment dataFragment;
	private AsyncManager asyncBlocker;

	private Toolbar toolbar;
	private PaintView paintView;
	private DrawerLayout layoutDrawer;
	private ListView drawerLeft;
	private View drawerRight;
	private ColorsSelect colorsSelect;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paint);
		decorView = getWindow().getDecorView();
		initSystemUIVisibility();
		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
		{
			@Override
			public void onSystemUiVisibilityChange(int visibility)
			{
				initSystemUIVisibility();
			}
		});
		fragments = getFragmentManager();
		displayMetrics = getResources().getDisplayMetrics();
		screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
		resultListeners = new HashMap<>();
		asyncBlocker = new AsyncManager(this);
		new GLHelper();
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.activity_paint);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		paintView = (PaintView) findViewById(R.id.paint_view);

		layoutDrawer = (DrawerLayout) findViewById(R.id.layout_drawer);
		drawerListener = new DrawerAdapter(this, layoutDrawer, toolbar);
		layoutDrawer.addDrawerListener(drawerListener);

		drawerLeft = (ListView) findViewById(R.id.drawer_left);
		drawerLeft.setOnItemClickListener(this);
		initLeftDrawer();

		drawerRight = findViewById(R.id.drawer_right);
		initRightDrawer();

		colorsSelect = (ColorsSelect) fragments.findFragmentById(R.id.colorsFragment);
		
		restoreInstanceState(savedInstanceState);
	}
	
	private void initSystemUIVisibility()
	{
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
									  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
									  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
									  | View.SYSTEM_UI_FLAG_FULLSCREEN
									  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
	
	private void initLeftDrawer()
	{
		LayoutParams params1 = drawerLeft.getLayoutParams();
		
		int maxWidth = (int) (280 * displayMetrics.density);
		int preferredWidth = (int) ((screenWidthDp - 112) * displayMetrics.density);
		params1.width = Math.min(maxWidth, preferredWidth);
	}
	
	private void initRightDrawer()
	{
		LayoutParams params2 = drawerRight.getLayoutParams();
		
		int maxWidth = (int) (320 * displayMetrics.density);
		int preferredWidth = (int) ((screenWidthDp - 112) * displayMetrics.density);
		params2.width = Math.min(maxWidth, preferredWidth);
	}
	
	private void restoreInstanceState(Bundle state)
	{
		if(state != null) super.onRestoreInstanceState(state);
		dataFragment = (AppDataFragment) fragments.findFragmentByTag(AppDataFragment.TAG);
		if(dataFragment == null)
		{
			dataFragment = new AppDataFragment();
			dataFragment.setOnImageChangeListener(paintView);
			dataFragment.setAsyncManager(asyncBlocker);
			FragmentTransaction transaction = fragments.beginTransaction();
			transaction.add(dataFragment, AppDataFragment.TAG);
			transaction.commit();
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		drawerListener.syncState();
		
		paintView.init(this);
		drawerLeft.setAdapter(new ToolsAdapter(this, getTools()));
		colorsSelect.setColors(paintView.getColors());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus) initSystemUIVisibility();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_main);
		drawerListener.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_paint, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		boolean anyDrawerOpen = layoutDrawer.isDrawerOpen(drawerLeft) || layoutDrawer.isDrawerOpen(drawerRight);
		menu.setGroupVisible(R.id.group_paint, !anyDrawerOpen);
		
		preparePhotoCaptureOption(menu);
		prepareFileOpenOption(menu);
		prepareFileSaveOption(menu);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	private void preparePhotoCaptureOption(Menu menu)
	{
		boolean hasCamera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
		menu.findItem(R.id.action_capture_photo).setEnabled(hasCamera);
	}
	
	private void prepareFileOpenOption(Menu menu)
	{
		String state = Environment.getExternalStorageState();
		boolean enable = state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
		menu.findItem(R.id.action_open_image).setEnabled(enable);
	}
	
	private void prepareFileSaveOption(Menu menu)
	{
		String state = Environment.getExternalStorageState();
		boolean enable = state.equals(Environment.MEDIA_MOUNTED);
		menu.findItem(R.id.action_save_image).setEnabled(enable);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		switch(id)
		{
		case R.id.action_tool:
			layoutDrawer.closeDrawer(drawerLeft);
			if(layoutDrawer.isDrawerOpen(drawerRight)) layoutDrawer.closeDrawer(drawerRight);
			else layoutDrawer.openDrawer(drawerRight);
			return true;
			
		case R.id.action_new_image:
			new OptionFileNew(this, paintView.getImage()).execute();
			return true;
		case R.id.action_capture_photo:
			new OptionFileCapturePhoto(this, paintView.getImage()).execute();
			return true;
		case R.id.action_open_image:
			new OptionFileOpen(this, paintView.getImage()).execute();
			return true;
		case R.id.action_save_image:
			new OptionFileSave(this, paintView.getImage()).execute();
			return true;
			
		case R.id.action_resize_image:
			new OptionImageResize(this, paintView.getImage()).execute();
			return true;
		case R.id.action_scale_image:
			new OptionImageScale(this, paintView.getImage()).execute();
			return true;
		case R.id.action_flip_image:
			new OptionImageFlip(this, paintView.getImage()).execute();
			return true;
		case R.id.action_rotate_image:
			new OptionImageRotate(this, paintView.getImage()).execute();
			
		case R.id.action_settings:
			showSettingsActivity();
		}
		return super.onOptionsItemSelected(item);
	}

	private void showSettingsActivity()
	{
		Intent intent = new Intent(this, ActivitySettings.class);
		startActivity(intent);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		paintView.setTool(getTools().getTool(position));
		layoutDrawer.closeDrawer(drawerLeft);
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
		Class<? extends ToolProperties> propertiesClass = paintView.getTool().getPropertiesFragmentClass();
		Fragment properties = propertiesClass.newInstance();
		Bundle propArgs = new Bundle();
		propArgs.putInt("tool", getTools().getToolId(paintView.getTool()));
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
	
	public void registerActivityResultListener(int requestCode, ActivityResultListener listener)
	{
		if(resultListeners.containsKey(requestCode)) throw new RuntimeException("requestCode is already used.");
		resultListeners.put(requestCode, listener);
	}
	
	public void unregisterActivityResultListener(int requestCode)
	{
		if(!resultListeners.containsKey(requestCode)) throw new RuntimeException("requestCode isn't registered yet.");
		resultListeners.remove(requestCode);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(!resultListeners.containsKey(requestCode)) return;
		resultListeners.get(requestCode).onActivityResult(resultCode, data);
	}
	
	public Image getImage()
	{
		return dataFragment.getImage();
	}
	
	public Tools getTools()
	{
		return dataFragment.getTools();
	}
}