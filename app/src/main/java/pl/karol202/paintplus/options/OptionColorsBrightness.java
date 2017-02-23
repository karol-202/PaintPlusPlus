package pl.karol202.paintplus.options;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.manipulators.ColorsBrightness;
import pl.karol202.paintplus.color.manipulators.params.BrightnessParams;
import pl.karol202.paintplus.color.manipulators.params.ManipulatorSelection;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.selection.Selection;

import java.util.Locale;

public class OptionColorsBrightness extends Option implements DialogInterface.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener
{
	private ColorsBrightness manipulator;
	private Layer layer;
	private Bitmap oldBitmap;
	
	private AlertDialog dialog;
	private SeekBar seekBarBrightness;
	private SeekBar seekBarContrast;
	private TextView textBrightness;
	private TextView textContrast;
	private Button buttonPreview;
	
	public OptionColorsBrightness(Context context, Image image)
	{
		super(context, image);
		this.manipulator = new ColorsBrightness();
		this.layer = image.getSelectedLayer();
		this.oldBitmap = Bitmap.createBitmap(layer.getBitmap());
	}
	
	@Override
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_colors_brightness, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.dialog_colors_brightness);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, this);
		
		seekBarBrightness = (SeekBar) view.findViewById(R.id.seekBar_brightness);
		seekBarBrightness.setOnSeekBarChangeListener(this);
		
		seekBarContrast = (SeekBar) view.findViewById(R.id.seekBar_contrast);
		seekBarContrast.setOnSeekBarChangeListener(this);
		
		textBrightness = (TextView) view.findViewById(R.id.brightness);
		textBrightness.setText(getText(seekBarBrightness));
		
		textContrast = (TextView) view.findViewById(R.id.contrast);
		textContrast.setText(getText(seekBarContrast));
		
		buttonPreview = (Button) view.findViewById(R.id.button_preview);
		buttonPreview.setOnTouchListener(this);
		
		dialog = dialogBuilder.create();
		dialog.show();
	}
	
	private String getText(SeekBar seekBar)
	{
		return String.format(Locale.US, "%1$d%%", getValue(seekBar));
	}
	
	private int getValue(SeekBar seekBar)
	{
		return seekBar.getProgress() - 100;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if(seekBar == seekBarBrightness)
		{
			textBrightness.setText(getText(seekBarBrightness));
		}
		else if(seekBar == seekBarContrast)
		{
			textContrast.setText(getText(seekBarContrast));
		}
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if(which == DialogInterface.BUTTON_POSITIVE) applyChanges();
		else if(which == DialogInterface.BUTTON_NEGATIVE) revertChanges();
	}
	
	private void applyChanges()
	{
		Selection selection = image.getSelection();
		ManipulatorSelection manipulatorSelection = ManipulatorSelection.fromSelection(selection, layer.getBounds());
		
		BrightnessParams params = new BrightnessParams(manipulatorSelection);
		params.setBrightness(getValue(seekBarBrightness) / 100f);
		params.setContrast(getValue(seekBarContrast) / 100f);
		
		Bitmap bitmapOut = manipulator.run(oldBitmap, params);
		layer.setBitmap(bitmapOut);
	}
	
	private void revertChanges()
	{
		layer.setBitmap(oldBitmap);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			applyChanges();
			dialog.hide();
			v.getParent().requestDisallowInterceptTouchEvent(true);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			dialog.show();
			v.getParent().requestDisallowInterceptTouchEvent(false);
		}
		return true;
	}
}