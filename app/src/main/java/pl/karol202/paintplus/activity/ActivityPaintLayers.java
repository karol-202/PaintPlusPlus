package pl.karol202.paintplus.activity;

import android.support.design.widget.BottomSheetBehavior;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.LayersAdapter;
import pl.karol202.paintplus.image.layer.LayersRecyclerView;
import pl.karol202.paintplus.options.OptionLayerNew;
import pl.karol202.paintplus.options.OptionLayerNew.OnLayerAddListener;
import pl.karol202.paintplus.util.LayersSheetBehavior;

public class ActivityPaintLayers implements View.OnClickListener
{
	private final float KEYLINE = 16f / 9f;
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
		sheetPanelSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SHEET_PANEL_SIZE_DP, activity.getDisplayMetrics());
	}
	
	public void initLayers()
	{
		layersAdapter = new LayersAdapter(activity);
		
		bottomSheet = activity.findViewById(R.id.bottom_sheet);
		bottomSheetBehaviour = (LayersSheetBehavior) BottomSheetBehavior.from(bottomSheet);
		bottomSheetBehaviour.setSkipCollapsed(true);
		bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
		
		recyclerLayers = (LayersRecyclerView) activity.findViewById(R.id.recycler_layers);
		recyclerLayers.setAdapter(layersAdapter);
		
		buttonAddLayer = (ImageButton) activity.findViewById(R.id.button_add_layer);
		buttonAddLayer.setOnClickListener(this);
	}
	
	public void postInitLayers()
	{
		image = activity.getImage();
		layersAdapter.setImage(image);
	}
	
	public void updateViews()
	{
		int activityWidth = decorView.getWidth();
		int activityHeight = decorView.getHeight();
		int maxSheetHeight = activityHeight - (int) (activityWidth / KEYLINE);
		int maxRecyclerHeight = maxSheetHeight - sheetPanelSizePx;
		recyclerLayers.setMaxHeight(maxRecyclerHeight);
	}
	
	public void toggleLayersSheet()
	{
		if(bottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_HIDDEN)
			bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
		else bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
	}
	
	public void closeLayersSheet()
	{
		bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
	}
	
	public void setScrollingBlocked(boolean blocked)
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
	
	public LayersAdapter getLayersAdapter()
	{
		return layersAdapter;
	}
}