package pl.karol202.paintplus.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSelect;
import pl.karol202.paintplus.tool.OnToolChangeListener;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.ToolsAdapter;

public class ActivityPaintDrawers implements AdapterView.OnItemClickListener
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
			else onRightDrawerMoved();
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
		
		private void onRightDrawerMoved()
		{
			//colorsSelect.updateColors();
		}
	}
	
	private final int LEFT_DRAWER_WIDTH = 280;
	private final int RIGHT_DRAWER_WIDTH = 320;
	
	private ActivityPaint activity;
	private ActionBarDrawerToggle drawerListener;
	private FragmentManager fragments;
	private DisplayMetrics displayMetrics;
	private Resources resources;
	private int screenWidthDp;
	
	private DrawerLayout layoutDrawer;
	private ListView drawerLeft;
	private View drawerRight;
	private ColorsSelect colorsSelect;
	
	ActivityPaintDrawers(ActivityPaint activity)
	{
		this.activity = activity;
		fragments = activity.getFragmentManager();
		displayMetrics = activity.getDisplayMetrics();
		resources = activity.getResources();
		screenWidthDp = (int) (displayMetrics.widthPixels / displayMetrics.density);
	}
	
	public void initDrawers()
	{
		layoutDrawer = (DrawerLayout) activity.findViewById(R.id.layout_drawer);
		drawerListener = new DrawerAdapter(activity, layoutDrawer, activity.getToolbar());
		layoutDrawer.addDrawerListener(drawerListener);
		
		drawerLeft = (ListView) activity.findViewById(R.id.drawer_left);
		drawerLeft.setOnItemClickListener(this);
		initLeftDrawer();
		
		drawerRight = activity.findViewById(R.id.drawer_right);
		initRightDrawer();
	}
	
	private void initLeftDrawer()
	{
		ViewGroup.LayoutParams params1 = drawerLeft.getLayoutParams();
		
		int maxWidth = screenWidthDp - 112;
		params1.width = (int) (Math.min(maxWidth, LEFT_DRAWER_WIDTH) * displayMetrics.density);
	}
	
	private void initRightDrawer()
	{
		ViewGroup.LayoutParams params2 = drawerRight.getLayoutParams();
		
		int maxWidth = screenWidthDp - 112;
		params2.width = (int) (Math.min(maxWidth, RIGHT_DRAWER_WIDTH) * displayMetrics.density);
	}
	
	public void postInitDrawers()
	{
		drawerListener.syncState();
		drawerLeft.setAdapter(new ToolsAdapter(activity, activity.getTools()));
		
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
			throw new RuntimeException("Error: Could not instantiate fragment from fragment class." +
					"Probably the fragment class does not contain " +
					"default constructor.", e);
		}
	}
	
	private void attachPropertiesFragment() throws InstantiationException, IllegalAccessException
	{
		Class<? extends ToolProperties> propertiesClass = activity.getTool().getPropertiesFragmentClass();
		Fragment properties = propertiesClass.newInstance();
		Bundle propArgs = new Bundle();
		propArgs.putInt("tool", activity.getTools().getToolId(activity.getTool()));
		properties.setArguments(propArgs);
		FragmentTransaction propTrans = fragments.beginTransaction();
		propTrans.replace(R.id.properties_fragment, properties);
		propTrans.commit();
	}
	
	private void tryToAttachColorsFragment()
	{
		try
		{
			attachColorsFragment();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Error: Could not instantiate fragment from fragment class." +
					"Probably the fragment class does not contain " +
					"default constructor.", e);
		}
	}
	
	private void attachColorsFragment() throws InstantiationException, IllegalAccessException
	{
		colorsSelect = new ColorsSelect();
		FragmentTransaction colorTrans = fragments.beginTransaction();
		colorTrans.replace(R.id.colors_fragment, colorsSelect);
		colorTrans.commit();
	}
	
	public void togglePropertiesDrawer()
	{
		layoutDrawer.closeDrawer(drawerLeft);
		if(layoutDrawer.isDrawerOpen(drawerRight)) layoutDrawer.closeDrawer(drawerRight);
		else layoutDrawer.openDrawer(drawerRight);
	}
	
	public boolean isAnyDrawerOpen()
	{
		return layoutDrawer.isDrawerOpen(drawerLeft) || layoutDrawer.isDrawerOpen(drawerRight);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Tool previousTool = activity.getTool();
		Tool newTool = activity.getTools().getTool(position);
		
		activity.setTool(newTool);
		tryToAttachPropertiesFragment();
		layoutDrawer.closeDrawer(drawerLeft);
		
		if(previousTool instanceof OnToolChangeListener) ((OnToolChangeListener) previousTool).onOtherToolSelected();
		if(newTool instanceof OnToolChangeListener) ((OnToolChangeListener) newTool).onToolSelected();
	}
}