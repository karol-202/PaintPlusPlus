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

package pl.karol202.paintplus.options;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.*;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.AppContext;
import pl.karol202.paintplus.history.action.ActionLayerAdd;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.util.GraphicsHelper;
import pl.karol202.paintplus.util.Utils;

public class OptionLayerNew extends Option implements DialogInterface.OnClickListener, TextWatcher
{
	public interface OnLayerAddListener
	{
		void onLayerAdded();
	}
	
	private OnLayerAddListener listener;
	private Bitmap preview;
	private Canvas prevEdit;
	private int layerWidth;
	private int layerHeight;
	private int imageWidth;
	private int imageHeight;
	private boolean dontFireEvent;
	
	private AlertDialog dialog;
	private EditText editName;
	private EditText editWidth;
	private EditText editHeight;
	private EditText editX;
	private EditText editY;
	private ImageView imagePreview;
	
	public OptionLayerNew(AppContext context, Image image)
	{
		this(context, image, null);
	}
	
	public OptionLayerNew(AppContext context, Image image, OnLayerAddListener listener)
	{
		super(context, image);
		this.listener = listener;
	}

	@SuppressLint("InflateParams")
	@Override
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.dialog_new_layer, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setTitle(R.string.dialog_new_layer);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, this);
		
		imageWidth = layerWidth = getImage().getWidth();
		imageHeight = layerHeight = getImage().getHeight();
		
		editName = view.findViewById(R.id.edit_layer_name);
		editName.setText(getImage().getDefaultLayerName());
		
		editWidth = view.findViewById(R.id.edit_layer_width);
		editWidth.setText(String.valueOf(layerWidth));
		editWidth.addTextChangedListener(this);
		
		editHeight = view.findViewById(R.id.edit_layer_height);
		editHeight.setText(String.valueOf(layerHeight));
		editHeight.addTextChangedListener(this);
		
		editX = view.findViewById(R.id.edit_layer_x);
		editX.setText("0");
		editX.addTextChangedListener(this);
		
		editY = view.findViewById(R.id.edit_layer_y);
		editY.setText("0");
		editY.addTextChangedListener(this);
		
		imagePreview = view.findViewById(R.id.image_size_preview);
		
		dialog = dialogBuilder.create();
		dialog.setOnShowListener(dialog -> createPreview());
		dialog.show();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if(which != DialogInterface.BUTTON_POSITIVE) return;
		if(layerWidth == 0 || layerHeight == 0)
		{
			getAppContext().createSnackbar(R.string.message_invalid_bounds, Snackbar.LENGTH_LONG).show();
			return;
		}
		if(layerWidth > GraphicsHelper.getMaxTextureSize() ||
		   layerHeight > GraphicsHelper.getMaxTextureSize())
		{
			getAppContext().createSnackbar(R.string.message_too_big, Snackbar.LENGTH_LONG).show();
			return;
		}
		String name = editName.getText().toString();
		int x = parseInt(editX.getText().toString());
		int y = parseInt(editY.getText().toString());
		
		Layer layer = getImage().newLayer(layerWidth, layerHeight, name);
		if(layer == null) getAppContext().createSnackbar(R.string.too_many_layers, Toast.LENGTH_SHORT).show();
		else
		{
			layer.setPosition(x, y);
			if(listener != null) listener.onLayerAdded();
			
			ActionLayerAdd action = new ActionLayerAdd(getImage());
			action.setLayerAfterAdding(layer);
			action.applyAction();
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) { }
	
	@Override
	public void afterTextChanged(Editable s)
	{
		if(!dontFireEvent)
		{
			dontFireEvent = true;
			
			this.layerWidth = parseInt(editWidth.getText().toString());
			this.layerHeight = parseInt(editHeight.getText().toString());
			
			dontFireEvent = false;
		}
		updatePreview();
	}
	
	private void createPreview()
	{
		int x = imagePreview.getWidth();
		int y = imagePreview.getHeight();
		preview = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
		imagePreview.setImageBitmap(preview);
		prevEdit = new Canvas(preview);
		updatePreview();
	}
	
	private void updatePreview()
	{
		preview.eraseColor(Color.TRANSPARENT);
		
		int offsetX = parseInt(editX.getText().toString());
		int offsetY = parseInt(editY.getText().toString());
		
		int left = Math.min(0, offsetX);
		int top = Math.min(0, offsetY);
		int right = Math.round(Math.max(imageWidth, layerWidth + offsetX));
		int bottom = Math.round(Math.max(imageHeight, layerHeight + offsetY));
		
		int min = Math.min(left, top);
		int max = Math.max(right, bottom);
		int previewSize = Math.max(preview.getWidth(), preview.getHeight());
		
		float oldLeft = Utils.map(0, min, max, 0, previewSize);
		float oldTop = Utils.map(0, min, max, 0, previewSize);
		float oldRight = Utils.map(imageWidth, min, max, 0, previewSize);
		float oldBottom = Utils.map(imageHeight, min, max, 0, previewSize);
		RectF oldR = new RectF(oldLeft, oldTop, oldRight, oldBottom);
		
		float newLeft = Utils.map(offsetX, min, max, 0, previewSize);
		float newTop = Utils.map(offsetY, min, max, 0, previewSize);
		float newRight = Utils.map(offsetX + layerWidth, min, max, 0, previewSize);
		float newBottom = Utils.map(offsetY + layerHeight, min, max, 0, previewSize);
		RectF newR = new RectF(newLeft, newTop, newRight, newBottom);
		
		Paint paint = new Paint();
		
		paint.setColor(Color.argb(255, 255, 255, 141));
		prevEdit.drawRect(oldR, paint);
		
		paint.setColor(Color.argb(204, 27, 124, 209));
		prevEdit.drawRect(newR, paint);
	}
	
	private int parseInt(String text)
	{
		return text.equals("") || text.equals("-") ? 0 : Integer.parseInt(text);
	}
}