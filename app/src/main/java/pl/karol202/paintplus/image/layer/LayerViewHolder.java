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

package pl.karol202.paintplus.image.layer;

import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
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
import pl.karol202.paintplus.history.action.ActionLayerDelete;
import pl.karol202.paintplus.history.action.ActionLayerNameChange;
import pl.karol202.paintplus.history.action.ActionLayerVisibilityChange;

public class LayerViewHolder extends RecyclerView.ViewHolder
		implements OnLongClickListener, OnTouchListener, OnClickListener, OnMenuItemClickListener
{
	static final int HEIGHT_DP = 64;
	
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
	private long animationDuration;
	private float animationTargetX;
	private float animationTargetY;
	
	LayerViewHolder(LayersAdapter adapter, View view)
	{
		super(view);
		this.adapter = adapter;
		this.view = view;
		this.view.setOnTouchListener(this);
		this.view.setOnLongClickListener(this);
		elevationPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ELEVATION_DP, adapter.getContext().getResources().getDisplayMetrics());
		animationDuration = adapter.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
		animationTargetX = 0;
		animationTargetY = 0;
		
		imageLayerHandle = view.findViewById(R.id.image_layer_handle);
		imageLayerHandle.setOnTouchListener(this);
		
		textLayerName = view.findViewById(R.id.text_layer_name);
		
		imageLayerPreview = view.findViewById(R.id.image_layer_preview);
		
		buttonLayerVisibility = view.findViewById(R.id.button_layer_visibility);
		buttonLayerVisibility.setOnClickListener(this);
		
		buttonLayerMenu = view.findViewById(R.id.button_layer_menu);
		buttonLayerMenu.setOnClickListener(this);
	}
	
	void bind(Layer layer)
	{
		this.layer = layer;
		
		view.setVisibility(View.VISIBLE);
		setViewOffset(0, 0);
		
		textLayerName.setText(layer.getName());
		imageLayerPreview.setImageBitmap(layer.getBitmap());
		buttonLayerVisibility.setContentDescription(getVisibilityButtonDescription());
		
		if(adapter.isLayerSelected(layer))
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
	
	private CharSequence getVisibilityButtonDescription()
	{
		return adapter.getContext().getString(layer.isVisible() ? R.string.desc_layer_visible : R.string.desc_layer_invisible);
	}
	
	private void setViewBackground(boolean selected)
	{
		Drawable drawable = ResourcesCompat.getDrawable(adapter.getContext().getResources(),
				selected ? R.drawable.layer_view_selected : R.drawable.layer_view,
				null);
		view.setBackground(drawable);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) rippleDrawable = (RippleDrawable) drawable;
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
			if(ghost) return false;
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
		if(adapter.isLayerSelected(layer) || adapter.areLayersLocked()) return;
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
		float x = event.getRawX();
		float y = event.getRawY();
		
		if(!ghost && event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if(adapter.areLayersLocked()) return false;
			handle.setViewHolder(this);
			handle.onTouchStart(x, y);
		}
		else if(ghost)
		{
			if(adapter.areLayersLocked())
			{
				handle.onTouchCancel();
				return false;
			}
			if(event.getAction() == MotionEvent.ACTION_MOVE) handle.onTouchMove(x, y);
			else if(event.getAction() == MotionEvent.ACTION_UP) handle.onTouchStop(x, y);
		}
		return true;
	}
	
	@Override
	public void onClick(View v)
	{
		if(v == buttonLayerVisibility) toggleVisibility();
		else if(v == buttonLayerMenu) showMenu();
	}
	
	private void toggleVisibility()
	{
		ActionLayerVisibilityChange action = new ActionLayerVisibilityChange(adapter.getImage());
		action.setLayerBeforeChange(layer);
		
		layer.setVisibility(!layer.isVisible());
		if(adapter.isLayerSelected(layer)) buttonLayerVisibility.setImageResource(layer.isVisible() ?
				R.drawable.ic_visible_white_24dp :
				R.drawable.ic_invisible_white_24dp);
		else buttonLayerVisibility.setImageResource(layer.isVisible() ?
				R.drawable.ic_visible_black_24dp :
				R.drawable.ic_invisible_black_24dp);
		
		action.applyAction();
	}
	
	private void showMenu()
	{
		PopupMenu menu = new PopupMenu(adapter.getContext(), buttonLayerMenu);
		menu.setOnMenuItemClickListener(this);
		menu.inflate(R.menu.menu_layer);
		if(adapter.areLayersLocked())
			for(int i = 0; i < menu.getMenu().size(); i++) menu.getMenu().getItem(i).setEnabled(false);
		if(adapter.isLastLayer(layer)) menu.getMenu().findItem(R.id.action_join).setEnabled(false);
		menu.show();
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_layer_properties:
			new LayerPropertiesDialog(adapter.getContext(), adapter.getImage(), layer).show();
			return true;
		case R.id.action_layer_change_name:
			showNameDialog();
			return true;
		case R.id.action_layer_duplicate:
			adapter.duplicateLayer(layer);
			return true;
		case R.id.action_join:
			adapter.joinWithNextLayer(layer);
			return true;
		case R.id.action_layer_delete:
			delete();
			return true;
		}
		return false;
	}
	
	@SuppressLint("InflateParams")
	private void showNameDialog()
	{
		LayoutInflater inflater = LayoutInflater.from(adapter.getContext());
		View dialogView = inflater.inflate(R.layout.dialog_layer_name, null, false);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
		builder.setView(dialogView);
		builder.setTitle(R.string.dialog_layer_name);
		
		final EditText editTextName = dialogView.findViewById(R.id.edit_layer_name);
		editTextName.setText(layer.getName());
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				ActionLayerNameChange action = new ActionLayerNameChange(adapter.getImage());
				action.setLayer(layer);
				
				layer.setName(editTextName.getText().toString());
				adapter.notifyDataSetChanged();
				
				action.applyAction();
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		builder.show();
	}
	
	private void delete()
	{
		String message = adapter.getContext().getString(R.string.dialog_layer_delete, layer.getName());
		AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
		builder.setMessage(message);
		builder.setPositiveButton(R.string.layer_delete, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				ActionLayerDelete action = new ActionLayerDelete(adapter.getImage());
				action.setLayerBeforeDeleting(layer);
				
				adapter.getImage().deleteLayer(layer);
				adapter.notifyDataSetChanged();
				
				action.applyAction();
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		builder.show();
	}
	
	public void hide()
	{
		view.setVisibility(View.INVISIBLE);
	}
	
	void setViewOffset(float x, float y)
	{
		view.setTranslationX(x);
		view.setTranslationY(y);
	}
	
	void setViewOffsetWithAnimation(float x, float y, AnimatorListener listener)
	{
		if(x == animationTargetX && y == animationTargetY) return;
		animationTargetX = x;
		animationTargetY = y;
		view.animate().translationX(x).translationY(y).setDuration(animationDuration).setListener(listener).start();
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void setElevation()
	{
		if(ghost) view.setTranslationZ(elevationPx);
	}
	
	void setGhost()
	{
		this.ghost = true;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) setElevation();
	}
	
	public Layer getLayer()
	{
		return layer;
	}
	
	public View getView()
	{
		return view;
	}
}