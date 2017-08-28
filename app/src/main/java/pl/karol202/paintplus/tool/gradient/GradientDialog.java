package pl.karol202.paintplus.tool.gradient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityResultListener;
import pl.karol202.paintplus.color.ActivityColorSelect;

class GradientDialog implements DialogInterface.OnClickListener, View.OnClickListener, ActivityResultListener, GradientView.OnColorUpdateListener
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
	private ColorPreviewView colorPreviewView;
	
	GradientDialog(Activity activity, Gradient gradient)
	{
		if(!(activity instanceof ActivityPaint)) throw new IllegalArgumentException("GradientDialog can only be created in ActivityPaint.");
		this.activity = (ActivityPaint) activity;
		this.originalGradient = gradient;
		this.gradient = new Gradient(gradient);
	}
	
	void show()
	{
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.dialog_gradient, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.dialog_gradient);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, this);
		
		gradientView = (GradientView) view.findViewById(R.id.gradient_view);
		gradientView.setColorUpdateListener(this);
		gradientView.setGradient(gradient);
		
		colorPreviewView = (ColorPreviewView) view.findViewById(R.id.gradient_color);
		colorPreviewView.setColor(gradientView.getSelectedColor());
		colorPreviewView.setOnClickListener(this);
		
		dialog = builder.show();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if(which == DialogInterface.BUTTON_POSITIVE) applyGradient();
	}
	
	private void applyGradient()
	{
		originalGradient.setGradient(gradient);
		if(listener != null) listener.onGradientUpdated();
	}
	
	@Override
	public void onColorUpdated(int color)
	{
		colorPreviewView.setColor(color);
	}
	
	@Override
	public void onClick(View v)
	{
		if(gradientView.isAnyColorSelected()) pickColor();
	}
	
	private void pickColor()
	{
		Intent intent = new Intent(activity, ActivityColorSelect.class);
		intent.putExtra(ActivityColorSelect.COLOR_KEY, gradientView.getSelectedColor());
		activity.registerActivityResultListener(REQUEST_CODE, this);
		activity.startActivityForResult(intent, REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent data)
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