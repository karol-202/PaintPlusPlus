package pl.karol202.paintplus.color.picker;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

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