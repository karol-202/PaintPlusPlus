package pl.karol202.paintplus.options;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;

public abstract class OptionFlip extends Option implements DialogInterface.OnClickListener
{
	private AlertDialog dialog;
	
	private RadioButton radioHorizontal;
	private RadioButton radioVertical;
	
	public OptionFlip(Context context, Image image)
	{
		super(context, image);
	}
	
	@Override
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_flip, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(getTitle());
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, null);
		
		radioHorizontal = (RadioButton) view.findViewById(R.id.radio_horizontal);
		radioVertical = (RadioButton) view.findViewById(R.id.radio_vertical);
		
		dialog = dialogBuilder.create();
		dialog.show();
	}
	
	protected abstract int getTitle();
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		int direction;
		if(radioHorizontal.isChecked() && !radioVertical.isChecked()) direction = Image.FLIP_HORIZONTALLY;
		else if(!radioHorizontal.isChecked() && radioVertical.isChecked()) direction = Image.FLIP_VERTICALLY;
		else
		{
			Toast.makeText(context, R.string.message_flip_direction, Toast.LENGTH_SHORT).show();
			return;
		}
		flip(direction);
		image.updateImage();
	}
	
	protected abstract void flip(int direction);
}