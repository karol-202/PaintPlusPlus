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
package pl.karol202.paintplus.activity

import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import pl.karol202.paintplus.R
import pl.karol202.paintplus.color.ColorsSelect
import pl.karol202.paintplus.databinding.ActivityPaintBinding
import pl.karol202.paintplus.tool.OnToolChangeListener
import pl.karol202.paintplus.tool.Tool
import pl.karol202.paintplus.tool.ToolsAdapter
import pl.karol202.paintplus.viewmodel.PaintViewModel

private const val LEFT_DRAWER_WIDTH = 280
private const val RIGHT_DRAWER_WIDTH = 320
private const val MIN_SPACE_TO_EDGE = 112

class ActivityPaintDrawers(private val activity: ActivityPaint,
                           private val views: ActivityPaintBinding,
                           private val paintViewModel: PaintViewModel)
{
	private inner class DrawerAdapter :
			ActionBarDrawerToggle(activity, views.layoutDrawer, views.toolbar.root, R.string.action_drawer_open, R.string.action_drawer_close)
	{
		override fun onDrawerOpened(drawerView: View)
		{
			if(drawerView == views.drawerLeft) onLeftDrawerOpened(drawerView)
			else if(drawerView == views.drawerRight) onRightDrawerOpened()
			activity.closeLayersSheet()
			activity.invalidateOptionsMenu()
		}

		override fun onDrawerClosed(drawerView: View)
		{
			if(drawerView == views.drawerLeft)
				onLeftDrawerClosed(drawerView)
			if(!views.layoutDrawer.isDrawerOpen(views.drawerLeft) && !views.layoutDrawer.isDrawerOpen(views.drawerRight))
				onAllDrawersClosed()
			activity.invalidateOptionsMenu()
		}

		override fun onDrawerSlide(drawerView: View, slideOffset: Float)
		{
			if(drawerView == views.drawerLeft) onLeftDrawerMoved(drawerView, slideOffset)
			activity.closeLayersSheet()
		}

		private fun onLeftDrawerOpened(drawerView: View)
		{
			super.onDrawerOpened(drawerView)
			paintViewModel.setTitleOverride(PaintViewModel.TitleOverride.TOOL_SELECTION)
		}

		private fun onRightDrawerOpened()
		{
			paintViewModel.setTitleOverride(PaintViewModel.TitleOverride.TOOL_PROPERTIES)
		}

		private fun onLeftDrawerClosed(drawerView: View)
		{
			super.onDrawerClosed(drawerView)
		}

		private fun onAllDrawersClosed()
		{
			paintViewModel.setTitleOverride(PaintViewModel.TitleOverride.NONE)
		}

		private fun onLeftDrawerMoved(drawerView: View, slideOffset: Float)
		{
			super.onDrawerSlide(drawerView, slideOffset)
			views.layoutDrawer.closeDrawer(views.drawerRight)
		}
	}

	val isAnyDrawerOpen: Boolean
		get() = views.layoutDrawer.isDrawerOpen(views.drawerLeft) || views.layoutDrawer.isDrawerOpen(views.drawerRight)

	private val fragments = activity.supportFragmentManager

	private val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
	private val maxDrawerWidthDp = (displayMetrics.widthPixels / displayMetrics.density).toInt() - MIN_SPACE_TO_EDGE
	private val leftDrawerWidth = (LEFT_DRAWER_WIDTH.coerceAtMost(maxDrawerWidthDp) * displayMetrics.density).toInt()
	private val rightDrawerWidth = (RIGHT_DRAWER_WIDTH.coerceAtMost(maxDrawerWidthDp) * displayMetrics.density).toInt()

	private val drawerAdapter = DrawerAdapter()
	private var toolsAdapter = ToolsAdapter(activity, paintViewModel.tools, this::onToolSelect)

	fun initDrawers()
	{
		views.layoutDrawer.addDrawerListener(drawerAdapter)

		views.drawerLeft.layoutManager = LinearLayoutManager(activity)
		views.drawerLeft.adapter = toolsAdapter
		views.drawerLeft.layoutParams.width = leftDrawerWidth
		views.drawerRight.layoutParams.width = rightDrawerWidth
	}

	fun postInitDrawers()
	{
		drawerAdapter.syncState()

		attachPropertiesFragment()
		attachColorsFragment()
	}

	private fun attachPropertiesFragment() = fragments.commit {
		replace(R.id.properties_fragment, paintViewModel.currentTool.propertiesFragmentClass.newInstance())
	}

	private fun attachColorsFragment() = fragments.commit {
		replace(R.id.colors_fragment, ColorsSelect())
	}

	fun togglePropertiesDrawer()
	{
		views.layoutDrawer.closeDrawer(views.drawerLeft)
		if(views.layoutDrawer.isDrawerOpen(views.drawerRight))
			views.layoutDrawer.closeDrawer(views.drawerRight)
		else
			views.layoutDrawer.openDrawer(views.drawerRight)
	}

	private fun onToolSelect(newTool: Tool)
	{
		val previousTool = paintViewModel.currentTool
		paintViewModel.setCurrentTool(newTool)

		attachPropertiesFragment()
		views.layoutDrawer.closeDrawer(views.drawerLeft)

		if(previousTool is OnToolChangeListener) previousTool.onOtherToolSelected()
		if(newTool is OnToolChangeListener) newTool.onToolSelected()
	}
}
