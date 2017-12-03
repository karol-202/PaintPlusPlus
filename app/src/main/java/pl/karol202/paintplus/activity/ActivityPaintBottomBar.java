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

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import pl.karol202.paintplus.ErrorHandler;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.color.picker.ActivityColorSelect;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.ToolBottomBar;

class ActivityPaintBottomBar
{
	private static final int REQUEST_PICK_COLOR = 7;

	private long animationDuration;
	private ActivityPaint activity;
	private Image image;
	private ColorsSet colors;
	private FragmentManager fragments;
	private ToolBottomBar bottomBarFragment;

	private View bottomBarReplacement;
	private View bottomBar;
	private View viewColor;
	private ImageButton buttonHide;

	ActivityPaintBottomBar(ActivityPaint activity)
	{
		this.animationDuration = activity.getResources().getInteger(android.R.integer.config_shortAnimTime);
		this.activity = activity;
		this.fragments = activity.getSupportFragmentManager();
	}

	void initBottomBar()
	{
		bottomBarReplacement = activity.findViewById(R.id.bottom_bar_replacement);

		bottomBar = activity.findViewById(R.id.bottom_bar);

		viewColor = activity.findViewById(R.id.bb_view_color);
		viewColor.setOnClickListener(v -> pickColor());

		buttonHide = activity.findViewById(R.id.bb_button_hide);
		buttonHide.setOnClickListener(v -> {
			hideBottomBar();
			activity.invalidateOptionsMenu();
		});
	}

	void postInitBottomBar()
	{
		image = activity.getImage();
		colors = image.getColorsSet();

		activity.addOnToolSelectListener(tool -> tryToAttachFragment());

		updateColor();
		tryToAttachFragment();
	}

	void showBottomBar()
	{
		bottomBar.setVisibility(View.VISIBLE);
		bottomBar.animate().translationY(0).setDuration(animationDuration).setInterpolator(new DecelerateInterpolator())
						   .setListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) { }

			@Override
			public void onAnimationEnd(Animator animation)
			{
				onShowingAnimationEnded();
			}

			@Override
			public void onAnimationCancel(Animator animation) { }

			@Override
			public void onAnimationRepeat(Animator animation) { }
		}).start();
	}

	private void onShowingAnimationEnded()
	{
		bottomBarReplacement.setVisibility(View.INVISIBLE);
	}

	void hideBottomBar()
	{
		bottomBarReplacement.setVisibility(View.GONE);
		bottomBar.animate().translationY(bottomBar.getHeight()).setDuration(animationDuration)
						   .setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) { }

			@Override
			public void onAnimationEnd(Animator animation)
			{
				onHidingAnimationEnd();
			}

			@Override
			public void onAnimationCancel(Animator animation) { }

			@Override
			public void onAnimationRepeat(Animator animation) { }
		}).start();
	}

	private void onHidingAnimationEnd()
	{
		bottomBar.setVisibility(View.GONE);
	}

	boolean isBottomBarVisible()
	{
		return bottomBar.getVisibility() == View.VISIBLE;
	}

	private void updateColor()
	{
		viewColor.setBackgroundColor(colors.getFirstColor());
		image.updateImage();
	}

	private void pickColor()
	{
		activity.registerActivityResultListener(REQUEST_PICK_COLOR, (resultCode, data) -> onColorPicked(data));

		Intent intent = new Intent(activity, ActivityColorSelect.class);
		intent.putExtra(ActivityColorSelect.ALPHA_KEY, false);
		intent.putExtra(ActivityColorSelect.COLOR_KEY, colors.getFirstColor());
		activity.startActivityForResult(intent, REQUEST_PICK_COLOR);
	}

	private void onColorPicked(Intent data)
	{
		if(data == null) return;
		int color = data.getIntExtra(ActivityColorSelect.COLOR_KEY, Color.BLACK) | 0xFF000000;

		colors.setFirstColor(color);
		updateColor();

		activity.unregisterActivityResultListener(REQUEST_PICK_COLOR);
	}

	private void tryToAttachFragment()
	{
		try
		{
			attachFragment();
		}
		catch(Exception e)
		{
			ErrorHandler.report(new RuntimeException("Error: Could not instantiate fragment from fragment class." +
													 "Probably the fragment class does not contain " +
													 "default constructor.", e));
		}
	}

	private void attachFragment() throws InstantiationException, IllegalAccessException
	{
		Tool tool = activity.getTool();

		FragmentTransaction transaction = fragments.beginTransaction();
		if(tool.getBottomBarFragmentClass() != null)
		{
			bottomBarFragment = createToolBottomBar(tool);
			transaction.replace(R.id.bb_view_fragment, bottomBarFragment);
		}
		else if(fragments.getFragments().contains(bottomBarFragment))
			transaction.remove(bottomBarFragment);
		transaction.commit();
	}

	private ToolBottomBar createToolBottomBar(Tool tool) throws IllegalAccessException, InstantiationException
	{
		Bundle propArgs = new Bundle();
		propArgs.putInt("tool", activity.getTools().getToolId(tool));

		Class<? extends ToolBottomBar> propertiesClass = tool.getBottomBarFragmentClass();
		ToolBottomBar properties = propertiesClass.newInstance();
		properties.setArguments(propArgs);
		return properties;
	}
}
