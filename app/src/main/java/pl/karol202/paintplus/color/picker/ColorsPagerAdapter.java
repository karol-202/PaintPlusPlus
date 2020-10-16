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

package pl.karol202.paintplus.color.picker;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

//TODO This pager adapter handles fragments in illegal way. Use instantiateItem() and destroyItem() instead.
public class ColorsPagerAdapter extends FragmentPagerAdapter
{
	private class TabFragment
	{
		private ColorPickerFragment fragment;
		private int icon;
		
		TabFragment(ColorPickerFragment fragment, int icon)
		{
			this.fragment = fragment;
			this.icon = icon;
		}
		
		ColorPickerFragment getFragment()
		{
			return fragment;
		}
		
		int getIcon()
		{
			return icon;
		}
	}
	
	private List<TabFragment> fragments;
	
	ColorsPagerAdapter(FragmentManager fm)
	{
		super(fm);
		this.fragments = new ArrayList<>();
	}
	
	@Override
	public ColorPickerFragment getItem(int position)
	{
		return fragments.get(position).getFragment();
	}
	
	@Override
	public int getCount()
	{
		return fragments.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position)
	{
		return null;
	}
	
	void addFragment(ColorPickerFragment fragment, int icon)
	{
		fragments.add(new TabFragment(fragment, icon));
	}
	
	void setupTabLayoutIcons(TabLayout tabLayout)
	{
		if(tabLayout.getTabCount() != fragments.size()) return;
		for(int i = 0; i < tabLayout.getTabCount(); i++)
		{
			TabLayout.Tab tab = tabLayout.getTabAt(i);
			if(tab == null) return;
			int icon = fragments.get(i).getIcon();
			tab.setIcon(icon);
		}
	}
}