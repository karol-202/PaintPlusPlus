package pl.karol202.paintplus.options;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorChannel;
import pl.karol202.paintplus.color.ColorChannel.ColorChannelType;
import pl.karol202.paintplus.color.ColorChannelsAdapter;
import pl.karol202.paintplus.color.ColorCurvesView;
import pl.karol202.paintplus.color.manipulators.ColorsCurveManipulator;
import pl.karol202.paintplus.color.manipulators.CurveManipulatorParams;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.util.SeekBarTouchListener;

import static pl.karol202.paintplus.color.ColorChannel.ColorChannelType.HSV;
import static pl.karol202.paintplus.color.ColorChannel.ColorChannelType.RGB;

public class OptionColorCurves extends Option implements OnClickListener, AdapterView.OnItemSelectedListener, View.OnTouchListener, View.OnClickListener
{
	private ColorChannelType channelType;
	private ColorChannelsAdapter adapterIn;
	private ColorChannelsAdapter adapterOut;
	
	private AlertDialog alertDialog;
	private Spinner spinnerChannelIn;
	private Spinner spinnerChannelOut;
	private ColorCurvesView curvesView;
	private Button buttonPreview;
	private Button buttonRestore;
	
	public OptionColorCurves(Context context, Image image, ColorChannelType type)
	{
		super(context, image);
		this.channelType = type;
	}
	
	@Override
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_color_curves, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(channelType == RGB ? R.string.dialog_color_curves_rgb : R.string.dialog_color_curves_hsv);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, null);
		
		adapterIn = new ColorChannelsAdapter(context, channelType);
		adapterOut = new ColorChannelsAdapter(context, channelType);
		
		spinnerChannelIn = (Spinner) view.findViewById(R.id.spinner_curves_channel_in);
		spinnerChannelIn.setAdapter(adapterIn);
		spinnerChannelIn.setOnItemSelectedListener(this);
		if(channelType == RGB) spinnerChannelIn.setSelection(0);
		else if(channelType == HSV) spinnerChannelIn.setSelection(2);
		
		spinnerChannelOut = (Spinner) view.findViewById(R.id.spinner_curves_channel_out);
		spinnerChannelOut.setAdapter(adapterOut);
		spinnerChannelOut.setOnItemSelectedListener(this);
		if(channelType == RGB) spinnerChannelOut.setSelection(0);
		else if(channelType == HSV) spinnerChannelOut.setSelection(2);
		
		curvesView = (ColorCurvesView) view.findViewById(R.id.color_curves_view);
		curvesView.setOnTouchListener(new SeekBarTouchListener());
		curvesView.setChannelType(channelType);
		curvesView.setChannelIn((ColorChannel) spinnerChannelIn.getSelectedItem());
		curvesView.setChannelOut((ColorChannel) spinnerChannelOut.getSelectedItem());
		
		buttonPreview = (Button) view.findViewById(R.id.button_curves_preview);
		//buttonPreview.setOnTouchListener(this);
		
		buttonRestore = (Button) view.findViewById(R.id.button_curves_restore);
		buttonRestore.setOnClickListener(this);
		
		alertDialog = builder.show();
	}
	
	//OK
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		Layer layer = image.getSelectedLayer();
		Bitmap bitmapIn = layer.getBitmap();
		
		CurveManipulatorParams params = new CurveManipulatorParams(channelType);
		curvesView.attachCurvesToParamsObject(params);
		
		ColorsCurveManipulator curves = new ColorsCurveManipulator();
		Bitmap bitmapOut = curves.run(bitmapIn, params);
		layer.setBitmap(bitmapOut);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		if(parent == spinnerChannelIn) curvesView.setChannelIn((ColorChannel) spinnerChannelIn.getSelectedItem());
		else if(parent == spinnerChannelOut) curvesView.setChannelOut((ColorChannel) spinnerChannelOut.getSelectedItem());
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) { }
	
	//Preview
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		/*if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			alertDialog.hide();
			v.getParent().requestDisallowInterceptTouchEvent(true);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			alertDialog.show();
			v.getParent().requestDisallowInterceptTouchEvent(false);
		}*/
		return true;
	}
	
	//Restore
	@Override
	public void onClick(View v)
	{
		curvesView.restoreCurrentCurve();
	}
}