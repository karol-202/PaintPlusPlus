package pl.karol202.paintplus.activity;

import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.ImageButton;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.LayersAdapter;
import pl.karol202.paintplus.image.layer.LayersRecyclerView;
import pl.karol202.paintplus.options.OptionLayerNew;
import pl.karol202.paintplus.options.OptionLayerNew.OnLayerAddListener;
import pl.karol202.paintplus.util.LayersSheetBehavior;
import pl.karol202.paintplus.util.Utils;

class ActivityPaintLayers implements View.OnClickListener
{
	private final float KEYLINE_3_2 = 3f / 2f;
	private final int SHEET_PANEL_SIZE_DP = 56;
	
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
		new OptionLayerNew(activity, image, new OnLayerAddListener()
		{
			@Override
			public void onLayerAdded()
			{
				layersAdapter.notifyDataSetChanged();
			}
		}).execute();
	}
}