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

import android.view.View;
import android.widget.ImageButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.LayersAdapter;
import pl.karol202.paintplus.image.layer.LayersRecyclerView;
import pl.karol202.paintplus.options.OptionLayerNew;
import pl.karol202.paintplus.util.LayersSheetBehavior;
import pl.karol202.paintplus.util.Utils;

class ActivityPaintLayers implements View.OnClickListener
{
	private static final float KEYLINE_3_2 = 3f / 2f;
	private static final int SHEET_PANEL_SIZE_DP = 56;

	private ActivityPaint activity;
	private Image image;
	private View decorView;
	private LayersSheetBehavior bottomSheetBehaviour;
	private LayersAdapter layersAdapter;
	private int sheetPanelSizePx;

	private View bottomSheet;
	private LayersRecyclerView recyclerLayers;
	private ImageButton buttonAddLayer;

	ActivityPaintLayers(ActivityPaint activity)
	{
		this.activity = activity;

		decorView = activity.getWindow().getDecorView();
		sheetPanelSizePx = (int) Utils.dpToPixels(activity.getDisplayMetrics(), SHEET_PANEL_SIZE_DP);
	}

	void initLayers()
	{
		layersAdapter = new LayersAdapter(activity);

		bottomSheet = activity.findViewById(R.id.bottom_sheet);
		bottomSheetBehaviour = (LayersSheetBehavior) BottomSheetBehavior.from(bottomSheet);
		bottomSheetBehaviour.setSkipCollapsed(true);
		bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);

		recyclerLayers = activity.findViewById(R.id.recycler_layers);
		recyclerLayers.setAdapter(layersAdapter);

		buttonAddLayer = activity.findViewById(R.id.button_add_layer);
		buttonAddLayer.setOnClickListener(this);
	}

	void postInitLayers()
	{
		image = activity.getImage();
		layersAdapter.setImage(image);
	}

	void updateView()
	{
		int activityHeight = decorView.getHeight();
		int maxSheetHeight = (int) (activityHeight / KEYLINE_3_2);
		int maxRecyclerHeight = maxSheetHeight - sheetPanelSizePx;
		recyclerLayers.setMaxHeight(maxRecyclerHeight);
	}

	void updateData()
	{
		layersAdapter.notifyDataSetChanged();
	}

	void toggleLayersSheet()
	{
		if(bottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_HIDDEN)
			bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
		else bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
	}

	void closeLayersSheet()
	{
		bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
	}

	void setScrollingBlocked(boolean blocked)
	{
		recyclerLayers.setAllowScrolling(!blocked);
		bottomSheetBehaviour.setAllowDragging(!blocked);
	}

	@Override
	public void onClick(View v)
	{
		new OptionLayerNew(activity, image, () -> layersAdapter.notifyDataSetChanged()).execute();
	}
}
