package pl.karol202.paintplus.options;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.util.Utils;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class OptionImageRotate extends Option implements DialogInterface.OnClickListener, SeekBar.OnSeekBarChangeListener
{
	private AlertDialog dialog;
	
	private SeekBar seekBarAngle;
	private TextView textAngle;
	
	public OptionImageRotate(Context context, Image image)
	{
		super(context, image);
	}
	
	@Override
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_rotate_image, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.dialog_rotate_image);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, this);
		
		seekBarAngle = (SeekBar) view.findViewById(R.id.seekBar_angle);
		seekBarAngle.setProgress(angleToProgress(0));
		seekBarAngle.setOnSeekBarChangeListener(this);
		
		textAngle = (TextView) view.findViewById(R.id.text_angle);
		textAngle.setText("0°");
		
		dialog = dialogBuilder.create();
		dialog.show();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if(which == BUTTON_POSITIVE)
		{
			float angle = progressToAngle(seekBarAngle.getProgress());
			image.rotate(angle);
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		String angleRes = context.getResources().getString(R.string.angle);
		textAngle.setText(angleRes + " " + progressToAngle(progress) + "°");
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
	
	private int angleToProgress(float angle)
	{
		return (int) Utils.map(angle, -180, 180, 0, seekBarAngle.getMax());
	}
	
	private float progressToAngle(int progress)
	{
		return Utils.map(progress, 0, seekBarAngle.getMax(), -180, 180);
	}
}