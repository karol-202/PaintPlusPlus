package pl.karol202.paintplus.image.layer;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.layer.mode.LayerMode;
import pl.karol202.paintplus.image.layer.mode.LayerModeAdapter;
import pl.karol202.paintplus.image.layer.mode.LayerModeType;

import java.util.Locale;

public class LayerPropertiesDialog implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener
{
	private Context context;
	private Layer layer;
	private LayerModeAdapter adapter;
	
	private AlertDialog dialog;
	private Spinner spinnerMode;
	private SeekBar seekBarOpacity;
	private TextView textOpacity;
	
	public LayerPropertiesDialog(Context context, Layer layer)
	{
		this.context = context;
		this.layer = layer;
		init();
	}
	
	private void init()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_layer_properties, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_layer_properties);
		builder.setView(view);
		
		adapter = new LayerModeAdapter(context);
		
		spinnerMode = (Spinner) view.findViewById(R.id.spinner_layer_mode);
		spinnerMode.setAdapter(adapter);
		spinnerMode.setSelection(indexOf(layer.getMode()));
		spinnerMode.setOnItemSelectedListener(this);
		
		seekBarOpacity = (SeekBar) view.findViewById(R.id.seekBar_layer_opacity);
		seekBarOpacity.setProgress((int) (layer.getOpacity() * 100));
		seekBarOpacity.setOnSeekBarChangeListener(this);
		
		textOpacity = (TextView) view.findViewById(R.id.layer_opacity);
		textOpacity.setText(String.format(Locale.US, "%1$d%%", seekBarOpacity.getProgress()));
		
		dialog = builder.create();
	}
	
	private int indexOf(LayerMode mode)
	{
		return LayerModeType.getIndexOfType(mode);
	}
	
	public void show()
	{
		dialog.show();
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		try
		{
			LayerModeType type = adapter.getItem(position);
			LayerMode mode = type.getLayerModeClass().newInstance();
			layer.setMode(mode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) { }
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		layer.setOpacity(progress / 100f);
		textOpacity.setText(String.format(Locale.US, "%1$d%%", seekBarOpacity.getProgress()));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
}