/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package pl.karol202.paintplus.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.karol202.paintplus.ErrorHandler;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSelect;
import pl.karol202.paintplus.tool.OnToolChangeListener;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolProperties;
import pl.karol202.paintplus.tool.ToolsAdapter;
import pl.karol202.paintplus.tool.ToolsAdapter.OnToolSelectListener;

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

		toolsAdapter = new ToolsAdapter(activity, activity.getTools());
		toolsAdapter.addOnToolSelectListener(this::onToolSelect);
		drawerLeft.setAdapter(toolsAdapter);

		tryToAttachPropertiesFragment();
		attachColorsFragment();
	}

	private void tryToAttachPropertiesFragment()
	{
		try
		{
			attachPropertiesFragment();
		}
		catch(Exception e)
		{
			ErrorHandler.report(new RuntimeException("Error: Could not instantiate fragment from fragment class." +
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

	private void attachColorsFragment()
	{
		FragmentTransaction colorTrans = fragments.beginTransaction();
		colorTrans.replace(R.id.colors_fragment, colorsSelect);
		colorTrans.commit();
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

	void addOnToolSelectListener(OnToolSelectListener listener)
	{
		toolsAdapter.addOnToolSelectListener(listener);
	}
}
