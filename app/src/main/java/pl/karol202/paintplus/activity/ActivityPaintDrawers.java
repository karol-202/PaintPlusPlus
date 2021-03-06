package pl.karol202.paintplus.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.crash.FirebaseCrash;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSelect;
import pl.karol202.paintplus.tool.OnToolChangeListener;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.ToolsAdapter;

class ActivityPaintDrawers
{
	private class DrawerAdapter extends ActionBarDrawerToggle
	{
		DrawerAdapter(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar)
		{
			super(activity, drawerLayout, toolbar, R.string.action_drawer_open, R.string.action_drawer_close);
		}
		
		@Override
		public void onDrawerOpened(View drawerView)
		{
			if(drawerView == drawerLeft) onLeftDrawerOpened(drawerView);
			else if(drawerView == drawerRight) onRightDrawerOpened();
			activity.closeLayersSheet();
			activity.invalidateOptionsMenu();
		}
		
		@Override
		public void onDrawerClosed(View drawerView)
		{
			if(drawerView == drawerLeft) onLeftDrawerClosed(drawerView);
			if(!(layoutDrawer.isDrawerOpen(drawerLeft) || layoutDrawer.isDrawerOpen(drawerRight)))
				onAllDrawersClosed();
			activity.invalidateOptionsMenu();
		}
		
		@Override
		public void onDrawerSlide(View drawerView, float slideOffset)
		{
			if(drawerView == drawerLeft) onLeftDrawerMoved(drawerView, slideOffset);
			activity.closeLayersSheet();
		}
		
		private void onLeftDrawerOpened(View drawerView)
		{
			super.onDrawerOpened(drawerView);
			String toolChoice = resources.getString(R.string.choice_of_tool);
			activity.setTitle(toolChoice);
		}
		
		private void onRightDrawerOpened()
		{
			String properties = resources.getString(R.string.properties);
			String tool = resources.getString(activity.getTool().getName());
			activity.setTitle(properties + ": " + tool);
		}
		
		private void onLeftDrawerClosed(View drawerView)
		{
			super.onDrawerClosed(drawerView);
		}
		
		private void onAllDrawersClosed()
		{
			activity.setTitle(null);
		}
		
		private void onLeftDrawerMoved(View drawerView, float slideOffset)
		{
			super.onDrawerSlide(drawerView, slideOffset);
			layoutDrawer.closeDrawer(drawerRight);
		}
	}
	
	private final int LEFT_DRAWER_WIDTH = 280;
	private final int RIGHT_DRAWER_WIDTH = 320;
	private final int MIN_SPACE_TO_EDGE = 112;
	
	private ActivityPaint activity;
	private DrawerAdapter drawerAdapter;
	private FragmentManager fragments;
	private DisplayMetrics displayMetrics;
	private Resources resources;
	private int screenWidthDp;
	private ToolsAdapter toolsAdapter;
	
	private DrawerLayout layoutDrawer;
	private RecyclerView drawerLeft;
	private View drawerRight;
	private ColorsSelect colorsSelect;
	
	ActivityPaintDrawers(ActivityPaint activity)
	{
		this.activity = activity;
		fragments = activity.getSupportFragmentManager();
		displayMetrics = activity.getDisplayMetrics();
		resources = activity.getResources();
		screenWidthDp = (int) (displayMetrics.widthPixels / displayMetrics.density);
	}
	
	void initDrawers()
	{
		layoutDrawer = activity.findViewById(R.id.layout_drawer);
		drawerAdapter = new DrawerAdapter(activity, layoutDrawer, activity.getToolbar());
		layoutDrawer.addDrawerListener(drawerAdapter);
		
		drawerLeft = activity.findViewById(R.id.drawer_left);
		drawerLeft.setLayoutManager(new LinearLayoutManager(activity));
		setLeftDrawerWidth();
		
		drawerRight = activity.findViewById(R.id.drawer_right);
		setRightDrawerWidth();
		
		colorsSelect = new ColorsSelect();
	}
	
	private void setLeftDrawerWidth()
	{
		ViewGroup.LayoutParams params1 = drawerLeft.getLayoutParams();
		
		int maxWidth = screenWidthDp - MIN_SPACE_TO_EDGE;
		params1.width = (int) (Math.min(maxWidth, LEFT_DRAWER_WIDTH) * displayMetrics.density);
	}
	
	private void setRightDrawerWidth()
	{
		ViewGroup.LayoutParams params2 = drawerRight.getLayoutParams();
		
		int maxWidth = screenWidthDp - MIN_SPACE_TO_EDGE;
		params2.width = (int) (Math.min(maxWidth, RIGHT_DRAWER_WIDTH) * displayMetrics.density);
	}
	
	void postInitDrawers()
	{
		drawerAdapter.syncState();
		
		toolsAdapter = new ToolsAdapter(activity, activity.getTools(), new ToolsAdapter.OnToolSelectListener()
		{
			@Override
			public void onToolSelect(Tool tool)
			{
				ActivityPaintDrawers.this.onToolSelect(tool);
			}
		});
		drawerLeft.setAdapter(toolsAdapter);
		
		tryToAttachPropertiesFragment();
		tryToAttachColorsFragment();
	}
	
	private void tryToAttachPropertiesFragment()
	{
		try
		{
			attachPropertiesFragment();
		}
		catch(Exception e)
		{
			FirebaseCrash.report(new RuntimeException("Error: Could not instantiate fragment from fragment class." +
													  "Probably the fragment class does not contain " +
													  "default constructor.", e));
		}
	}
	
	private void attachPropertiesFragment() throws InstantiationException, IllegalAccessException
	{
		FragmentTransaction propTrans = fragments.beginTransaction();
		propTrans.replace(R.id.properties_fragment, createPropertiesFragment());
		propTrans.commit();
	}
	
	private Fragment createPropertiesFragment() throws InstantiationException, IllegalAccessException
	{
		Bundle propArgs = new Bundle();
		propArgs.putInt("tool", activity.getTools().getToolId(activity.getTool()));
		
		Class<? extends ToolProperties> propertiesClass = activity.getTool().getPropertiesFragmentClass();
		Fragment properties = propertiesClass.newInstance();
		properties.setArguments(propArgs);
		return properties;
	}
	
	private void tryToAttachColorsFragment()
	{
		try
		{
			attachColorsFragment();
		}
		catch(Exception e)
		{
			FirebaseCrash.report(new RuntimeException("Error: Could not instantiate fragment from fragment class." +
													  "Probably the fragment class does not contain " +
													  "default constructor.", e));
		}
	}
	
	private void attachColorsFragment() throws InstantiationException, IllegalAccessException
	{
		FragmentTransaction colorTrans = fragments.beginTransaction();
		colorTrans.replace(R.id.colors_fragment, colorsSelect);
		colorTrans.commit();
	}
	
	void togglePropertiesDrawer()
	{
		layoutDrawer.closeDrawer(drawerLeft);
		if(layoutDrawer.isDrawerOpen(drawerRight)) layoutDrawer.closeDrawer(drawerRight);
		else layoutDrawer.openDrawer(drawerRight);
	}
	
	boolean isAnyDrawerOpen()
	{
		return layoutDrawer.isDrawerOpen(drawerLeft) || layoutDrawer.isDrawerOpen(drawerRight);
	}
	
	private void onToolSelect(Tool newTool)
	{
		Tool previousTool = activity.getTool();
		
		activity.setTool(newTool);
		tryToAttachPropertiesFragment();
		layoutDrawer.closeDrawer(drawerLeft);
		
		if(previousTool instanceof OnToolChangeListener) ((OnToolChangeListener) previousTool).onOtherToolSelected();
		if(newTool instanceof OnToolChangeListener) ((OnToolChangeListener) newTool).onToolSelected();
	}
}