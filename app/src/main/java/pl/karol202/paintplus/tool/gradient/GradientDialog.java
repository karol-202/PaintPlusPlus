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

package pl.karol202.paintplus.tool.gradient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.color.picker.ActivityColorSelect;
import pl.karol202.paintplus.util.ImageToggleButton;

import java.util.Locale;

class GradientDialog
{
	interface OnGradientUpdateListener
	{
		void onGradientUpdated();
	}
	
	private static final int REQUEST_CODE = 10;
	
	private OnGradientUpdateListener listener;
	private ActivityPaint activity;
	private Gradient originalGradient;
	private Gradient gradient;
	
	private GradientView gradientView;
	private TextView textGradientPosition;
	private ImageToggleButton buttonAdd;
	private ImageButton buttonDelete;
	private ColorPreviewView colorPreviewView;
	
	GradientDialog(Activity activity, Gradient gradient)
	{
		if(!(activity instanceof ActivityPaint)) throw new IllegalArgumentException("GradientDialog can only be created in ActivityPaint.");
		this.activity = (ActivityPaint) activity;
		this.originalGradient = gradient;
		this.gradient = new Gradient(gradient);
	}
	
	@SuppressLint("InflateParams")
	void show()
	{
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.dialog_gradient, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.dialog_gradient);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, (dialog, which) -> onOKClick());
		builder.setNegativeButton(R.string.cancel, null);
		
		gradientView = view.findViewById(R.id.gradient_view);
		gradientView.setGradientUpdateListener(new GradientView.OnGradientEditorUpdateListener() {
			@Override
			public void onGradientPositionUpdated(float position)
			{
				GradientDialog.this.onGradientPositionUpdated(position);
			}
			
			@Override
			public void onGradientSelectionUpdated(float position, int color)
			{
				GradientDialog.this.onGradientSelectionUpdated(position, color);
			}
			
			@Override
			public void onGradientPointAdded()
			{
				GradientDialog.this.onGradientPointAdded();
			}
		});
		gradientView.setGradient(gradient);
		
		textGradientPosition = view.findViewById(R.id.text_gradient_position);
		textGradientPosition.setText("");
		
		buttonAdd = view.findViewById(R.id.button_add_gradient_point);
		buttonAdd.setOnCheckedChangeListener((button, checked) -> onAddButtonCheckedChange(checked));
		
		buttonDelete = view.findViewById(R.id.button_delete_gradient_point);
		buttonDelete.setOnClickListener(v -> onDeleteButtonClick());
		buttonDelete.setEnabled(false);
		
		colorPreviewView = view.findViewById(R.id.gradient_color);
		colorPreviewView.setColor(gradientView.getSelectedColor());
		colorPreviewView.setOnClickListener(v -> onColorViewClick());
		
		builder.show();
	}
	
	private void onOKClick()
	{
		applyGradient();
	}
	
	private void applyGradient()
	{
		originalGradient.setGradient(gradient);
		if(listener != null) listener.onGradientUpdated();
	}
	
	private void onGradientPositionUpdated(float position)
	{
		if(position == -1) textGradientPosition.setText("");
		else textGradientPosition.setText(String.format(Locale.US, "%.0f%%", position * 100));
	}
	
	private void onGradientSelectionUpdated(float position, int color)
	{
		onGradientPositionUpdated(position);
		buttonDelete.setEnabled(gradientView.canDeletePoint());
		colorPreviewView.setColor(color);
	}
	
	private void onGradientPointAdded()
	{
		buttonAdd.setChecked(false);
		onAddButtonCheckedChange(false);
	}
	
	private void onAddButtonCheckedChange(boolean checked)
	{
		gradientView.setAddingMode(checked);
	}
	
	private void onDeleteButtonClick()
	{
		gradientView.deleteSelectedPoint();
	}
	
	private void onColorViewClick()
	{
		if(gradientView.isAnyColorSelected()) pickColor();
	}
	
	private void pickColor()
	{
		Intent intent = new Intent(activity, ActivityColorSelect.class);
		intent.putExtra(ActivityColorSelect.ALPHA_KEY, true);
		intent.putExtra(ActivityColorSelect.COLOR_KEY, gradientView.getSelectedColor());
		activity.registerActivityResultListener(REQUEST_CODE, (resultCode, data) -> GradientDialog.this.onActivityResult(data));
		activity.startActivityForResult(intent, REQUEST_CODE);
	}
	
	private void onActivityResult(Intent data)
	{
		activity.unregisterActivityResultListener(REQUEST_CODE);
		int color = data.getIntExtra(ActivityColorSelect.COLOR_KEY, Color.BLACK);
		
		gradientView.setSelectedColor(color);
	}
	
	void setGradientUpdateListener(OnGradientUpdateListener listener)
	{
		this.listener = listener;
	}
}