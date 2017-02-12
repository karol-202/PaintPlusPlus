package pl.karol202.paintplus.options;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.util.GraphicsHelper;

import static android.content.DialogInterface.OnClickListener;

public class OptionFileNew extends Option implements OnClickListener
{
	private AlertDialog dialog;
	private EditText editX;
	private EditText editY;

	public OptionFileNew(Context context, Image image)
	{
		super(context, image);
	}

	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_new_image, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.dialog_new_image);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, this);

		editX = (EditText) view.findViewById(R.id.edit_image_x);
		editX.setText(String.valueOf(image.getWidth()));

		editY = (EditText) view.findViewById(R.id.edit_image_y);
		editY.setText(String.valueOf(image.getHeight()));

		dialog = dialogBuilder.create();
		dialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if(which != DialogInterface.BUTTON_POSITIVE) return;
		int x = parseInt(editX.getText().toString());
		int y = parseInt(editY.getText().toString());
		
		if(x == 0 || y == 0)
		{
			Toast.makeText(context, R.string.message_invalid_bounds, Toast.LENGTH_LONG).show();
			return;
		}
		if(x > GraphicsHelper.getMaxTextureSize() ||
		   y > GraphicsHelper.getMaxTextureSize())
		{
			Toast.makeText(context, R.string.message_too_big, Toast.LENGTH_LONG).show();
			return;
		}
		image.newImage(x, y);
		image.centerView();
	}
	
	private int parseInt(String text)
	{
		return text.equals("") || text.equals("-") ? 0 : Integer.parseInt(text);
	}
}