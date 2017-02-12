package pl.karol202.paintplus.options;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorChannel;
import pl.karol202.paintplus.color.ColorChannelsAdapter;
import pl.karol202.paintplus.color.ColorCurvesView;
import pl.karol202.paintplus.image.Image;

public class OptionColorCurves extends Option implements OnClickListener, AdapterView.OnItemSelectedListener, View.OnClickListener
{
	private ColorChannelsAdapter adapter;
	
	private AlertDialog alertDialog;
	private Spinner spinnerChannel;
	private ColorCurvesView curvesView;
	private Button buttonPreview;
	
	public OptionColorCurves(Context context, Image image)
	{
		super(context, image);
	}
	
	@Override
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_color_curves, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_color_curves);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, null);
		
		adapter = new ColorChannelsAdapter(context);
		
		spinnerChannel = (Spinner) view.findViewById(R.id.spinner_curves_channel);
		spinnerChannel.setAdapter(adapter);
		spinnerChannel.setSelection(ColorChannel.VALUE.ordinal());
		spinnerChannel.setOnItemSelectedListener(this);
		
		curvesView = (ColorCurvesView) view.findViewById(R.id.color_curves_view);
		curvesView.setChannel((ColorChannel) spinnerChannel.getSelectedItem());
		
		buttonPreview = (Button) view.findViewById(R.id.button_curves_preview);
		buttonPreview.setOnClickListener(this);
		
		alertDialog = builder.show();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		curvesView.setChannel((ColorChannel) spinnerChannel.getSelectedItem());
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) { }
	
	@Override
	public void onClick(View v)
	{
		
	}
}