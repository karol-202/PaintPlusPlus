package pl.karol202.paintplus.image;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;
import android.widget.PopupMenu.OnMenuItemClickListener;
import pl.karol202.paintplus.R;

import java.util.ArrayList;

public class LayersAdapter extends RecyclerView.Adapter<LayersAdapter.ViewHolder>
{
	public class ViewHolder extends RecyclerView.ViewHolder
							implements OnLongClickListener, OnTouchListener, OnClickListener, OnMenuItemClickListener
	{
		private Layer layer;
		
		private View view;
		private ImageView imageLayerHandle;
		private TextView textLayerName;
		private ImageView imageLayerPreview;
		private ImageButton buttonLayerVisibility;
		private ImageButton buttonLayerMenu;
		
		private RippleDrawable rippleDrawable;
		
		public ViewHolder(View view)
		{
			super(view);
			this.view = view;
			this.view.setOnTouchListener(this);
			this.view.setOnLongClickListener(this);
			
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
			this.layer = layer;
			textLayerName.setText(layer.getName());
			imageLayerPreview.setImageBitmap(layer.getBitmap());
			
			if(image.isLayerSelected(layer))
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
		
		private void setViewBackground(boolean selected)
		{
			Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
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
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) showRipple(event.getX(), event.getY());
				}
				else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_OUTSIDE)
				{
					select();
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) hideRipple();
				}
			}
			else if(v == imageLayerHandle) onHandleTouch(event);
			return false;
		}
		
		private void select()
		{
			if(image.isLayerSelected(layer)) return;
			notifyItemChanged(image.getSelectedLayerIndex());
			image.selectLayer(layer);
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
		
		private void onHandleTouch(MotionEvent event)
		{
			
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
			if(image.isLayerSelected(layer)) buttonLayerVisibility.setImageResource(layer.isVisible() ?
																				    R.drawable.ic_visible_white_24dp :
																				    R.drawable.ic_invisible_white_24dp);
			else buttonLayerVisibility.setImageResource(layer.isVisible() ?
														R.drawable.ic_visible_black_24dp :
														R.drawable.ic_invisible_black_24dp);
		}
		
		private void showMenu()
		{
			PopupMenu menu = new PopupMenu(context, buttonLayerMenu);
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
			LayoutInflater inflater = LayoutInflater.from(context);
			View dialogView = inflater.inflate(R.layout.dialog_layer_name, null, false);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
					notifyDataSetChanged();
				}
			});
			builder.setNegativeButton(R.string.cancel, null);
			builder.show();
		}
		
		private void delete()
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(R.string.dialog_layer_delete);
			builder.setPositiveButton(R.string.layer_delete, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					image.deleteLayer(layer);
					notifyDataSetChanged();
				}
			});
			builder.setNegativeButton(R.string.cancel, null);
			builder.show();
		}
	}
	
	private Context context;
	private Image image;
	private ArrayList<Layer> layers;
	
	public LayersAdapter(Context context)
	{
		this.context = context;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.item_layer, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position)
	{
		holder.bind(layers.get(position));
	}
	
	@Override
	public int getItemCount()
	{
		return layers.size();
	}
	
	public void setImage(Image image)
	{
		this.image = image;
		this.layers = image.getLayers();
		notifyDataSetChanged();
	}
}