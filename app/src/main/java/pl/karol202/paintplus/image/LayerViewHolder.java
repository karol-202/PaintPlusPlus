package pl.karol202.paintplus.image;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;
import android.widget.PopupMenu.OnMenuItemClickListener;
import pl.karol202.paintplus.R;

public class LayerViewHolder extends RecyclerView.ViewHolder
		implements OnLongClickListener, OnTouchListener, OnClickListener, OnMenuItemClickListener
{
	public static final int HEIGHT_DP = 64;
	
	private final int ELEVATION_DP = 18;
	
	private LayersAdapter adapter;
	private Layer layer;
	private boolean ghost;
	
	private View view;
	private ImageView imageLayerHandle;
	private TextView textLayerName;
	private ImageView imageLayerPreview;
	private ImageButton buttonLayerVisibility;
	private ImageButton buttonLayerMenu;
	
	private RippleDrawable rippleDrawable;
	private float elevationPx;
	
	public LayerViewHolder(LayersAdapter adapter, View view)
	{
		super(view);
		this.adapter = adapter;
		this.view = view;
		this.view.setOnTouchListener(this);
		this.view.setOnLongClickListener(this);
		elevationPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ELEVATION_DP, adapter.getContext().getResources().getDisplayMetrics());
		
		imageLayerHandle = (ImageView) view.findViewById(R.id.image_layer_handle);
		imageLayerHandle.setOnTouchListener(this);
		textLayerName = (TextView) view.findViewById(R.id.text_layer_name);
		imageLayerPreview = (ImageView) view.findViewById(R.id.image_layer_preview);
		buttonLayerVisibility = (ImageButton) view.findViewById(R.id.button_layer_visibility);
		buttonLayerVisibility.setOnClickListener(this);
		buttonLayerMenu = (ImageButton) view.findViewById(R.id.button_layer_menu);
		buttonLayerMenu.setOnClickListener(this);
	}
	
	public void bind(Layer layer)
	{
		bind(layer, false);
	}
	
	public void bind(Layer layer, boolean ghost)
	{
		this.layer = layer;
		this.ghost = ghost;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) setElevation();
		
		textLayerName.setText(layer.getName());
		imageLayerPreview.setImageBitmap(layer.getBitmap());
		
		if(adapter.getImage().isLayerSelected(layer))
		{
			setViewBackground(true);
			imageLayerHandle.setImageResource(R.drawable.ic_drag_handle_white_24dp);
			textLayerName.setTextColor(Color.WHITE);
			buttonLayerVisibility.setImageResource(layer.isVisible() ?
					R.drawable.ic_visible_white_24dp :
					R.drawable.ic_invisible_white_24dp);
			buttonLayerMenu.setImageResource(R.drawable.ic_menu_white_24dp);
		}
		else
		{
			setViewBackground(false);
			imageLayerHandle.setImageResource(R.drawable.ic_drag_handle_black_24dp);
			textLayerName.setTextColor(Color.BLACK);
			buttonLayerVisibility.setImageResource(layer.isVisible() ?
					R.drawable.ic_visible_black_24dp :
					R.drawable.ic_invisible_black_24dp);
			buttonLayerMenu.setImageResource(R.drawable.ic_menu_black_24dp);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void setElevation()
	{
		if(ghost) view.setTranslationZ(elevationPx);
	}
	
	private void setViewBackground(boolean selected)
	{
		Drawable drawable = ResourcesCompat.getDrawable(adapter.getContext().getResources(),
				selected ? R.drawable.layer_view_selected : R.drawable.layer_view,
				null);
		view.setBackground(drawable);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)  rippleDrawable = (RippleDrawable) drawable;
	}
	
	//Long click on the whole view
	@Override
	public boolean onLongClick(View v)
	{
		showMenu();
		return true;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(v == view)
		{
			if(view.getVisibility() == View.INVISIBLE) return false;
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) showRipple(event.getX(), event.getY());
			}//Te nawiasy są potrzebne
			else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
			{
				select();
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) hideRipple();
			}
			return true;
		}
		else if(v == imageLayerHandle) return onHandleTouch(event);
		return false;
	}
	
	private void select()
	{
		if(adapter.getImage().isLayerSelected(layer)) return;
		adapter.notifyItemChanged(adapter.getImage().getSelectedLayerIndex());
		adapter.getImage().selectLayer(layer);
		bind(layer);
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void showRipple(float x, float y)
	{
		rippleDrawable.setHotspot(x, y);
		rippleDrawable.setState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled });
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void hideRipple()
	{
		rippleDrawable.setState(new int[] { android.R.attr.state_enabled });
	}
	
	private boolean onHandleTouch(MotionEvent event)
	{
		LayerHandle handle = adapter.getLayerHandle();
		float x = event.getX();
		float y = event.getY();
		if(ghost)
		{
			x += view.getTranslationX();
			y += view.getTranslationY();
			
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				handle.setViewHolder(this);
				handle.onTouchStart(x, y);
			}
			else if(event.getAction() == MotionEvent.ACTION_MOVE) handle.onTouchMove(x, y);
			else if(event.getAction() == MotionEvent.ACTION_UP) handle.onTouchStop(x, y);
			return true;
		}
		else
		{
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				handle.setViewHolder(this);
				handle.onTouchStart(x, y);
			}
			return false;
		}
	}
	
	@Override
	public void onClick(View v)
	{
		if(v == buttonLayerVisibility) toggleVisibility();
		else if(v == buttonLayerMenu) showMenu();
	}
	
	private void toggleVisibility()
	{
		layer.setVisible(!layer.isVisible());
		if(adapter.getImage().isLayerSelected(layer)) buttonLayerVisibility.setImageResource(layer.isVisible() ?
				R.drawable.ic_visible_white_24dp :
				R.drawable.ic_invisible_white_24dp);
		else buttonLayerVisibility.setImageResource(layer.isVisible() ?
				R.drawable.ic_visible_black_24dp :
				R.drawable.ic_invisible_black_24dp);
	}
	
	private void showMenu()
	{
		PopupMenu menu = new PopupMenu(adapter.getContext(), buttonLayerMenu);
		menu.setOnMenuItemClickListener(this);
		menu.inflate(R.menu.menu_layer);
		menu.show();
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_layer_change_name:
			showNameDialog();
			return true;
		case R.id.action_layer_delete:
			delete();
			return true;
		}
		return false;
	}
	
	private void showNameDialog()
	{
		LayoutInflater inflater = LayoutInflater.from(adapter.getContext());
		View dialogView = inflater.inflate(R.layout.dialog_layer_name, null, false);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
		builder.setView(dialogView);
		builder.setTitle(R.string.dialog_layer_name);
		
		final EditText editTextName = (EditText) dialogView.findViewById(R.id.edit_layer_name);
		editTextName.setText(layer.getName());
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				layer.setName(editTextName.getText().toString());
				adapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		builder.show();
	}
	
	private void delete()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
		builder.setMessage(R.string.dialog_layer_delete);
		builder.setPositiveButton(R.string.layer_delete, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				adapter.getImage().deleteLayer(layer);
				adapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		builder.show();
	}
	
	public void setViewOffset(float x, float y)
	{
		view.setTranslationX(x);
		view.setTranslationY(y);
	}
	
	public Layer getLayer()
	{
		return layer;
	}
	
	public boolean isGhost()
	{
		return ghost;
	}
	
	public View getView()
	{
		return view;
	}
}