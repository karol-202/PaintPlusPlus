package pl.karol202.paintplus.options;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import pl.karol202.paintplus.PaintView;
import pl.karol202.paintplus.R;

import static android.content.DialogInterface.*;

public class ImageNew extends Option implements OnClickListener
{
	private final int MAX_SIZE = 2048 * 2048;

	private AlertDialog dialog;

	private EditText editX;
	private EditText editY;

	public ImageNew(Activity activity, PaintView paintView)
	{
		super(activity, paintView);
	}

	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.dialog_new_image, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setTitle(R.string.dialog_new_image);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, this);

		Point currentSize = paintView.getSize();

		editX = (EditText) view.findViewById(R.id.edit_image_x);
		editX.setText(Integer.toString(currentSize.x));

		editY = (EditText) view.findViewById(R.id.edit_image_y);
		editY.setText(Integer.toString(currentSize.y));

		dialog = dialogBuilder.create();
		dialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if(which == DialogInterface.BUTTON_POSITIVE)
		{
			int x = Integer.parseInt(editX.getText().toString());
			int y = Integer.parseInt(editY.getText().toString());
			if(x * y > MAX_SIZE)
			{
				Toast.makeText(activity, R.string.message_too_big, Toast.LENGTH_LONG).show();
				return;
			}
			paintView.clear(x, y);
		}
	}
}