package pl.karol202.paintplus.activity;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import pl.karol202.paintplus.AppDataFragment;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.PaintView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.settings.ActivitySettings;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.Tools;
import pl.karol202.paintplus.util.GraphicsHelper;

import java.util.HashMap;

public class ActivityPaint extends AppCompatActivity
{
	public interface ActivityResultListener
	{
		void onActivityResult(int resultCode, Intent data);
	}
	
	private ActivityPaintActions actions;
	private ActivityPaintDrawers drawers;
	private ActivityPaintLayers layers;
	
	private View decorView;
	private FragmentManager fragments;
	private HashMap<Integer, ActivityResultListener> resultListeners;
	private AsyncManager asyncBlocker;
	private AppDataFragment dataFragment;
	private ActionBar actionBar;

	private ViewGroup mainContainer;
	private Toolbar toolbar;
	private PaintView paintView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		GraphicsHelper.init(this);
		
		actions = new ActivityPaintActions(this);
		drawers = new ActivityPaintDrawers(this);
		layers  = new ActivityPaintLayers(this);
		
		setContentView(R.layout.activity_paint);
		decorView = getWindow().getDecorView();
		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
		{
			@Override
			public void onSystemUiVisibilityChange(int visibility)
			{
				initSystemUIVisibility();
			}
		});
		initSystemUIVisibility();
		
		fragments = getFragmentManager();
		resultListeners = new HashMap<>();
		asyncBlocker = new AsyncManager(this);
		
		mainContainer = (ViewGroup) findViewById(R.id.main_container);
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		paintView = (PaintView) findViewById(R.id.paint_view);
		
		drawers.initDrawers();
		layers.initLayers();
		
		restoreInstanceState(savedInstanceState);
	}
	
	private void initSystemUIVisibility()
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) initSystemUIVisibilityKitkat();
		else initSystemUIVisibilityPreKitkat();
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void initSystemUIVisibilityKitkat()
	{
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
									  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
									  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
									  | View.SYSTEM_UI_FLAG_FULLSCREEN
									  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
	
	private void initSystemUIVisibilityPreKitkat()
	{
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
									  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
									  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
									  | View.SYSTEM_UI_FLAG_FULLSCREEN);
	}
	
	private String makeTitle(Bundle savedInstanceState)
	{
		if(savedInstanceState != null) return savedInstanceState.getString("title");
		else return null;
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
		
		setTitle(makeTitle(savedInstanceState));
		paintView.init(this);
		layers.postInitLayers();
		drawers.postInitDrawers();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		paintView.updatePreferences();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putString("title", toolbar.getTitle().toString());
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		GraphicsHelper.destroy();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus) initSystemUIVisibility();
		layers.updateViews();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		actions.inflateMenu(menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		actions.prepareMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(actions.handleAction(item)) return true;
		else return super.onOptionsItemSelected(item);
	}
	
	public void showSettingsActivity()
	{
		Intent intent = new Intent(this, ActivitySettings.class);
		startActivity(intent);
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
	
	public boolean isAnyDrawerOpen()
	{
		return drawers.isAnyDrawerOpen();
	}
	
	public void togglePropertiesDrawer()
	{
		drawers.togglePropertiesDrawer();
	}
	
	public void toggleLayersSheet()
	{
		layers.toggleLayersSheet();
	}
	
	public void closeLayersSheet()
	{
		layers.closeLayersSheet();
	}
	
	public void setScrollingBlocked(boolean blocked)
	{
		layers.setScrollingBlocked(blocked);
	}
	
	public void setTitle(String title)
	{
		if(getTool() == null) title = getString(R.string.activity_main);
		else if(title == null) title = getString(getTool().getName());
		actionBar.setTitle(title);
	}
	
	public void updateLayersPreview()
	{
		layers.getLayersAdapter().notifyDataSetChanged();
	}
	
	public DisplayMetrics getDisplayMetrics()
	{
		return getResources().getDisplayMetrics();
	}
	
	public Image getImage()
	{
		return dataFragment.getImage();
	}
	
	public Tools getTools()
	{
		return dataFragment.getTools();
	}
	
	public Tool getTool()
	{
		if(dataFragment == null) return null;
		return dataFragment.getTool();
	}
	
	public void setTool(Tool tool)
	{
		dataFragment.setTool(tool);
		paintView.onImageChanged();
	}
	
	public ViewGroup getMainContainer()
	{
		return mainContainer;
	}
	
	public Toolbar getToolbar()
	{
		return toolbar;
	}
}