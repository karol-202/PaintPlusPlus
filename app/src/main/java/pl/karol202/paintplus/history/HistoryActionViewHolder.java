package pl.karol202.paintplus.history;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;

class HistoryActionViewHolder extends HistoryViewHolder
{
	static final int PREVIEW_SIZE_DP = 60;
	
	private ImageView imagePreview;
	private TextView textName;
	
	HistoryActionViewHolder(View view)
	{
		super(view);
		imagePreview = view.findViewById(R.id.image_history_action_preview);
		textName = view.findViewById(R.id.text_history_action);
	}
	
	@Override
	void bind(final History history, Action action)
	{
		imagePreview.setImageBitmap(action.getActionPreview());
		textName.setText(action.getActionName());
	}
}