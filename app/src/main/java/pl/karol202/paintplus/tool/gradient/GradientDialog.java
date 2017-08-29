package pl.karol202.paintplus.tool.gradient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityResultListener;
import pl.karol202.paintplus.color.ActivityColorSelect;
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
	
	private AlertDialog dialog;
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
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				onOKClick();
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		
		gradientView = (GradientView) view.findViewById(R.id.gradient_view);
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
		
		textGradientPosition = (TextView) view.findViewById(R.id.text_gradient_position);
		textGradientPosition.setText("");
		
		buttonAdd = (ImageToggleButton) view.findViewById(R.id.button_add_gradient_point);
		buttonAdd.setOnCheckedChangeListener(new ImageToggleButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(ImageToggleButton button, boolean checked)
			{
				onAddButtonCheckedChange(checked);
			}
		});
		
		buttonDelete = (ImageButton) view.findViewById(R.id.button_delete_gradient_point);
		buttonDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				onDeleteButtonClick();
			}
		});
		buttonDelete.setEnabled(false);
		
		colorPreviewView = (ColorPreviewView) view.findViewById(R.id.gradient_color);
		colorPreviewView.setColor(gradientView.getSelectedColor());
		colorPreviewView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				onColorViewClick();
			}
		});
		
		dialog = builder.show();
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
		intent.putExtra(ActivityColorSelect.COLOR_KEY, gradientView.getSelectedColor());
		activity.registerActivityResultListener(REQUEST_CODE, new ActivityResultListener() {
			@Override
			public void onActivityResult(int resultCode, Intent data)
			{
				GradientDialog.this.onActivityResult(data);
			}
		});
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