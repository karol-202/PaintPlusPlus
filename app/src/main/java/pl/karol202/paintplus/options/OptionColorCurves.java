package pl.karol202.paintplus.options;

import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorChannel;
import pl.karol202.paintplus.color.ColorChannel.ColorChannelType;
import pl.karol202.paintplus.color.ColorChannelsAdapter;
import pl.karol202.paintplus.color.ColorCurvesView;
import pl.karol202.paintplus.color.OnCurveEditListener;
import pl.karol202.paintplus.color.manipulators.ColorsCurveManipulator;
import pl.karol202.paintplus.color.manipulators.params.CurveManipulatorParams;
import pl.karol202.paintplus.color.manipulators.params.ManipulatorSelection;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.selection.Selection;
import pl.karol202.paintplus.util.SeekBarTouchListener;

import static pl.karol202.paintplus.color.ColorChannel.ColorChannelType.HSV;
import static pl.karol202.paintplus.color.ColorChannel.ColorChannelType.RGB;

public class OptionColorCurves extends Option implements OnClickListener, AdapterView.OnItemSelectedListener,
														 View.OnTouchListener, View.OnClickListener, OnCurveEditListener
{
	private ColorChannelType channelType;
	private ColorChannelsAdapter adapterIn;
	private ColorChannelsAdapter adapterOut;
	private Layer layer;
	private Bitmap oldBitmap;
	private ColorsCurveManipulator manipulator;
	
	private AlertDialog alertDialog;
	private Spinner spinnerChannelIn;
	private Spinner spinnerChannelOut;
	private ColorCurvesView curvesView;
	private TextView textPoint;
	private Button buttonPreview;
	private Button buttonRestore;
	
	public OptionColorCurves(Context context, Image image, ColorChannelType type)
	{
		super(context, image);
		this.channelType = type;
		this.layer = image.getSelectedLayer();
		this.oldBitmap = Bitmap.createBitmap(layer.getBitmap());
		this.manipulator = new ColorsCurveManipulator();
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
		builder.setNegativeButton(R.string.cancel, this);
		
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
		curvesView.setOnCurveEditListener(this);
		curvesView.setChannelType(channelType);
		curvesView.setChannelIn((ColorChannel) spinnerChannelIn.getSelectedItem());
		curvesView.setChannelOut((ColorChannel) spinnerChannelOut.getSelectedItem());
		
		textPoint = (TextView) view.findViewById(R.id.text_curve_point);
		textPoint.setText(curvesView.getInfoText());
		
		buttonPreview = (Button) view.findViewById(R.id.button_curves_preview);
		buttonPreview.setOnTouchListener(this);
		
		buttonRestore = (Button) view.findViewById(R.id.button_curves_restore);
		buttonRestore.setOnClickListener(this);
		
		alertDialog = builder.show();
	}
	
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
		
		CurveManipulatorParams params = new CurveManipulatorParams(manipulatorSelection, channelType);
		curvesView.attachCurvesToParamsObject(params);
		
		Bitmap bitmapOut = manipulator.run(oldBitmap, params);
		layer.setBitmap(bitmapOut);
	}
	
	private void revertChanges()
	{
		layer.setBitmap(oldBitmap);
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
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			applyChanges();
			alertDialog.hide();
			v.getParent().requestDisallowInterceptTouchEvent(true);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			alertDialog.show();
			v.getParent().requestDisallowInterceptTouchEvent(false);
		}
		return true;
	}
	
	//Restore
	@Override
	public void onClick(View v)
	{
		curvesView.restoreCurrentCurve();
	}
	
	@Override
	public void onCurveEdited()
	{
		textPoint.setText(curvesView.getInfoText());
	}
}